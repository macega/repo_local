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
 * Version class  displays the version and expiration date (if any) for
 * the jConnect driver being used.<br>
 *
 * <P>Version may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 * -e = display expiration string
 *
 *  @see Sample
 */
public class Version extends Sample
{

    static Properties _props = null;  // Local copy of Cmdline properties
    static Driver _driver = null;

    Version()
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
            //Inorder to get Licensee information (Trial, Production Version, PowerJ, ..),
            //need to make a connection otherwise it simply states that
            //this is an (Unlicensed Version).  Therefore we will try to make a 
            //connection, and catch the Exception and print what version info we have
            //if the connection couldn't be made.
            _con = null;
            try
            {
                String prox = _cmdline._props.getProperty("proxy");
                error(" PROXY = "+prox+"\n");
                _con = DriverManager.getConnection(_cmdline._props.getProperty("server"), 
                    _cmdline._props);
            }
            catch(SQLException connectExcep)
            {
                //Oh well we can't access Licensee information because the connection
                // failed, but will Display as much version info we can
            }


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

    }

    public void sampleCode()
    {

        String url = _props.getProperty("server");

        try
        {

            DriverPropertyInfo dpi[] = _driver.getPropertyInfo(url, _props);
            // get the version string
            for (int i = 0; i < dpi.length; i++)
            {
                if (dpi[i].name.equals("VERSIONSTRING"))
                {
                    output(dpi[i].value + "\n");
                    break;
                }
                if (dpi[i].name.equals("EXPIRESTRING"))
                {
                    String expires = dpi[i].value;
                    if(expires.equals(""))
                    {
                        output("\n Driver does not contain an expiration"
                            + " date\n");
                    }
                    else
                    {
                        output("\n This Driver expires on: "
                            + " expires\n");
                    }
                    break;
                }
            }


        }
        catch (SQLException sqe)
        {
            displaySQLEx(sqe);

        }
    }
}
