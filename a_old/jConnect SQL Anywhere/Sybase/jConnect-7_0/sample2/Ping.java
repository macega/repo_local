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
 * Ping class  may be used to see if you are able to connect to a RDBMS
 * via jConnect<br> 
 *
 * <P>Ping may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class Ping extends Sample
{

    String _url = null;

    Ping()
    {
        super();
    }

    public void run()
    {
        try
        {

            addMoreProps(_cmdline);

            _url=_cmdline._props.getProperty("server");

            // Load the Driver

            DriverManager.registerDriver((Driver)
                Class.forName("com.sybase.jdbc4.jdbc.SybDriver").newInstance());

            _con = DriverManager.getConnection(
                _cmdline._props.getProperty("server"), _cmdline._props);

            // Run the sample specific code
            sampleCode();

        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
            output("Could not connect RDBMS using the URL: ");
            output(_url + "\n");

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
    }

    public void sampleCode()
    {

        output("Successfully connected to the RDBMS using the URL: ");
        output(_url + "\n");
    }
}
