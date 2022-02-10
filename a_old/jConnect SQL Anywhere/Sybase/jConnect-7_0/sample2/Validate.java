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
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * Validate class can be used that your CLASSPATH and proxy property are
 * set properly<br>
 *
 * <P>Validate may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class Validate extends Sample
{

    static Properties _props = null;  // Local copy of Cmdline properties
    static Driver _driver = null;
    Validate()
    {
        super();
    }

    public void run()
    {
        try
        {

            addMoreProps(_cmdline);

            // Load the Driver

            DriverManager.registerDriver((Driver)
                Class.forName("com.sybase.jdbc4.jdbc.SybDriver").newInstance());

            _driver = DriverManager.getDriver(
                _cmdline._props.getProperty("server"));
            int major = _driver.getMajorVersion();
            int minor = _driver.getMinorVersion();
            output("Using JDBC driver version " +
                major + "." + minor + "\n");

            // Save off CommandLine Properties
            _props = _cmdline._props;


            // Run the sample specific code
            sampleCode();
        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
        catch (ClassNotFoundException e)
        {
            error("Unexpected exception : " + e.toString() + "\n");
            error("\nThis error usually indicates that " +
                "your Java CLASSPATH environment has not been set properly.\n");
            e.printStackTrace();
        }
        catch (Exception e)
        {
            error("Unexpected exception : " + e.toString() + "\n");
            error("\nCould not Load the jConnect driver\n");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                // Close the connection
                if (_con != null)
                _con.close();
            }
            catch(SQLException sqe) 
            {
            }
            error(" Closing connection on sample\n");
            error(" Sample Finished Executing\n");
            stop();
        }

    }

    public void sampleCode()
    {

        String defaultProxy = "localhost:8000";
        boolean tryingProxy = false;
        String url = _props.getProperty("server");
        String cmdLineProxy = _props.getProperty("proxy");

        try
        {
            // attempt to connect to the sample database
            if(!_anApplet) //because then it complains about not having a proxy
            {
                _props.put("proxy", "");
                _con = DriverManager.getConnection(url, _props);
                DriverPropertyInfo dpi[] = _driver.getPropertyInfo(url, _props);
                // get the version string
                for (int i = 0; i < dpi.length; i++)
                {
                    if (dpi[i].name.equals("VERSIONSTRING"))
                    {
                        output(dpi[i].value+"\n");
                        break;
                    }
                }
                output("Your CLASSPATH, Java environment, and " +
                    "jdbcConnect drivers are properly installed\n\ttrying to " +
                    "connect to the Sybase sample dataserver --\n\t...this will " +
                    " fail if your machine is not connected to the internet.\n");

                // attempt to connect to the sample database
                output("Connected successfully\n");
                _con.close();
            }
            output("Now connecting through your HTTP/JDBC Gateway\n");
            tryingProxy = true;
            if(cmdLineProxy== null)
            {
                _props.put("proxy", defaultProxy);
            }
            else
            {
                _props.put("proxy", cmdLineProxy);
            }
            _con = DriverManager.getConnection(url, _props);
            output("Connected successfully\n");
            _con.close();



        }
        catch (SQLException sqe)
        {
            String sqlstate = sqe.getSQLState();
            String message = sqe.toString();
            error("Unexpected exception: " + sqlstate + "\n");
            if (sqlstate.equals("JZ0I4"))
            {
                if (message.indexOf("502") > 0)
                {
                    error("\nThis error usually indicates that " +
                        "your HTTP gateway was unable to reach the database " +
                        "that you specified.\n\n  One source of this problem may " +
                        "be that you are running the httpd/gateway under a " +
                        "JDK1.0.2 level VM.  1.0.2 has a bug where if you try " +
                        "to connect to an IP Address which your operation system " +
                        "does not have a 'name' for (gethostbyname fails?) " +
                        "then it cannot open a socket to it.  This problem is " +
                        "fixed in JDK1.1 - if you kill your httpd gateway and " +
                        "restart it under JDK1.1 this problem may be resolved.\n")
                        ;
                }
            }
            if (sqlstate.equals("JZ006"))
            {
                if (tryingProxy)
                {
                    error("\nThis error usually indicates that " +
                        "your HTTP gateway is NOT running. " +
                        "Check to see that " + _props.getProperty("proxy")
                        + " is responding.\n");
                }
                else
                {
                    error("\nThis error often indicates that " +
                        "the database you are trying to connect to is not up. " +
                        "Try to verify (use a non-jdbc tool) that " + url +
                        " is running.\n");
                }
            }
            output("Did not connect successfully\n");
            sqe.printStackTrace();

        }
    }
}
