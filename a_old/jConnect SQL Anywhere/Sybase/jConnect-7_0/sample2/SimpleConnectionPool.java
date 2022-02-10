/*
Confidential property of Sybase, Inc.
Copyright 2001, 2012
Sybase, Inc.  All rights reserved.
Unpublished rights reserved under U.S. copyright laws.

This software contains confidential and trade secret information of Sybase,
Inc.   Use,  duplication or disclosure of the software and documentation by
the  U.S.  Government  is  subject  to  restrictions set forth in a license
agreement  between  the  Government  and  Sybase,  Inc.  or  other  written
agreement  specifying  the  Government's rights to use the software and any
applicable FAR provisions, for example, FAR 52.227-19.
Sybase, Inc. One Sybase Drive, Dublin, CA 94568, USA
*/

package sample2;

import java.sql.*;
import java.util.*;

// need ConnectionPoolDataSource and related 
// interfaces from the JDBC 2.0 Optional Package
import javax.sql.*;

// need JNDI Contexts to do lookup
import javax.naming.Context;
import javax.naming.InitialContext;

/**
  * The purpose of this sample is to outline how to use
  * JNDI to obtain <B>pooled</B> JDBC connections using
  * the ConnectionPoolDataSource interface from the 
  * JDBC 2.0 Optional Package. <P>
  *
  * Note that this code is not expected to excecute 
  * successfully until you have set up your environment.
  * For this particular sample, you must: <OL>
  * <LI> Set up an LDAP server
  * <LI> Create an entry on the LDAP server using the
  *      Sybase OIDs
  * <LI> Change the Context.PROVIDER_URL property to
  *      point to your LDAP server
  * <LI> Change the lookup() call to use whatever
  *      search criteria reflects the entry you
  *      created in #2 (above).
  * </OL>
  *
  * This code assumes the LDAP server is running on
  * host 'some_ldap_server' and listening on port 238.
  * It assumes there is an entry with at least this
  * much information (LDIF format):
  *
  * <PRE>
  * dn:servername=myPool, o=MyCompany, c=US
  * 1.3.6.1.4.1.897.4.2.5:TCP#1# mymachine 4000
  * 1.3.6.1.4.1.897.4.2.10:user=me&password=mine
  * 1.3.6.1.4.1.897.4.2.18: ConnectionPoolDataSource
  * objectclass: sybaseServer
  * </PRE>
  *
  * This code differs from most of the other samples
  * in that there are no references to Sybase.  There
  * are no com.sybase.jdbcx interfaces, and there is no
  * JDBC URL with "jdbc:sybase:Tds:hostname:2638".
  * While there are String references to Sybase and Sun
  * in <B>this</B> example, those properties would
  * typically be set as part of the system environment.
  * They only exist in this sample to illustrate what
  * the properties are and how they should be specified.<P>
  *
  * Also note the import statements.  While no Sybase
  * classes are needed, you must download JNDI and the
  * JDBC 2.0 Optional Package, and put them in your
  * classpath in order to compile and run this sample.<P>
  *
  * For more information, please see the <I>jConnect
  * Programmer's Reference</I>.
  */
public class SimpleConnectionPool extends Sample implements ConnectionEventListener
{

    ConnectionPoolDataSource _poolDataSource = null;

    /**
      * The constructor obtains a ConnectionPoolDataSource
      * reference via JNDI.  This datasource is used when
      * new database connections need to be established
      * and maintained in some container (pool).
      */
    public SimpleConnectionPool()
    {
        // calls super()

        // configure the JNDI environment.

        // To keep clients simpler and more reusable, 
        // it is recommended that these properties are
        // set by your environment.  

        Properties props = new Properties();
        props.put(Context.PROVIDER_URL,
            "ldap://some_ldap_server:238/o=MyCompany,c=US");
        props.put(Context.OBJECT_FACTORIES, 
            "com.sybase.jdbc4.jdbc.SybObjectFactory");
        props.put(Context.INITIAL_CONTEXT_FACTORY, 
            "com.sun.jndi.ldap.LdapCtxFactory");

        // others ?? you might want to specify 
        // some security properties or the preferred
        // language...

        try
        {
            output("accessing JNDI\n");

            // If the properties are set by your
            // environment, then this call can become:
            // Context ctx = new InitialContext();
            // which will default to the system properties.
            Context ctx = new InitialContext(props);

            // Using the JNDI properties, pass in the
            // String name identifying the object to
            // be returned.  Since the environment for this
            // sample is using LDAP and the SybObjectFactory,
            // the lookup string is a Relative Distinguished
            // Name (RDN).  The LDAP entry is located by 
            // applying this search criteria and the 
            // Context.PROVIDER_URL setting.  Then, the
            // InitialContext connects to the ldap server and 
            // locates the entry as: 
            // ldap://some_ldap_server:238/servername=myASE,o=MyCompany,c=US
            // Then, that DirContext and the Name for that entry
            // are passed into the SybObjectFactory.  The factory
            // uses that information to construct a new DataSource
            // which is returned from lookup().
            _poolDataSource = (ConnectionPoolDataSource) ctx.lookup("servername=myPool");

            output("ConnectionPoolDataSource reference obtained.\n");
        }
        catch (Exception e)
        {
            error("rats! JNDI lookup failed. " +
                "Your environment is not set up properly.\n" +
                e);
        }
    }

    public void sampleCode()
    {
        Connection conn = null;
        boolean closed = false;
        String query = "SELECT * FROM sysusers";

        try
        {
            conn = getConnectionFromPool();

            // Use the JDBC API
            // just like if the Connection was obtained
            // from the DriverManager.
            Statement stmt = conn.createStatement();

            output("Executing: " + query + "\n");
            ResultSet rs = stmt.executeQuery (query);
            dispResultSet(rs);

            conn.close();
            closed = true;
            output("Connection closed by client.\n");
        }
        catch (SQLException sqe)
        {
            error("client got a SQLException.");
            displaySQLEx(sqe);
        }
        finally
        {
            // very important that the client closes
            // the connection.  Otherwise, the pool
            // implementation either waits forever
            // or has to break the connection by
            // calling getConnection() on the PooledConnection
            // again.
            try
            {
                if ((conn != null) && !closed)
                {
                    conn.close();
                }
            }
            catch (SQLException sqe)
            {
                output("exception when closing?\n");
                displaySQLEx(sqe);
            }
        }

        output("done!\n");
    }

    /**
      * This method is missing the pool implementation
      * logic.  Users should consider maintaining the
      * PooledConnections in some kind of container
      * so that when this method is called, 
      */
    public Connection getConnectionFromPool()
    {
        try
        {

            // using the DataSource reference retrieved
            // from JNDI, simply request a JDBC connection.
            // Note that you can override the default
            // user and password for this DataSource by
            // using the other getConnection method:
            // Connection conn = dataSource.getConnection("someone", "else");

            // -------------------------------
            // insert pool logic here
            // eg,
            // if (pool.hasAvailableConnection())
            //     pConn = pool.getAvailableConnection();
            // else
            //     pConn = _poolDataSource.getPooledConnection();
            // -------------------------------

            PooledConnection pConn = _poolDataSource.getPooledConnection();

            output("Pooled connection established.\n");

            // at this point, a JDBC connection
            // has been made to the database.  As a
            // connection pool implementation, several
            // pooled connections should be made, and
            // then "handed out" to end clients.
            // This object will listen to the end clients
            // use of the connection to know when they
            // call Connection.close()

            // register the pool implementation as a listener
            pConn.addConnectionEventListener(this);

            // give the end client the handle
            return pConn.getConnection();
        }
        catch (SQLException sqe)
        {
            error("problem obtaining pooled connection");
            displaySQLEx(sqe);        
        }
        return null;
    }

    /**
      * Implements ConnectionEventListener.
      */
    public void connectionClosed(ConnectionEvent event)
    {
        output("listener heard that the client closed the connection.\n");
    }

    /**
      * Implements ConnectionEventListener.
      */
    public void connectionErrorOccurred(ConnectionEvent event)
    {
        output("listener heard that the client had a fatal error " +
            " and has invalidated the connection.\n");
    }

}
