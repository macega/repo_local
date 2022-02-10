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
 * DriverMgr class Demonstrates the use of the DriverManager class.
 *
 * <P>DriverMgr may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class DriverMgr extends Sample
{
    static Driver _driver = null; 
    static String _url = null; 
    static Properties _props = null;

    DriverMgr()
    {
        super();
    }

    public void run()
    {

        try
        {

            addMoreProps(_cmdline);

            // Save off the data we will need in SampleCode()

            _url = _cmdline._props.getProperty("server");
            _props = _cmdline._props;

            // Load the Driver

            //  The following line will result in the driver being loaded
            //  in order to work around a bug in the IE browser.

            _driver = (Driver) new com.sybase.jdbc4.jdbc.SybDriver();

            //  When the driver is registered, below, it will result in
            //  the driver being registered twice.  Because of this hack
            //  for IE, the line below may be commented out.

            DriverManager.registerDriver(_driver);


            // Run the sample specific code
            sampleCode();

        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
        catch (java.lang.Exception ex)
        {
            //  Got some other type of exception.  Dump it.
            ex.printStackTrace ();
        }
    }

    public void sampleCode()
    {


        try
        {

            // Display the current Drivers
            displayDrivers();
            output("getLoginTimeout= "+
                DriverManager.getLoginTimeout() + "\n");

            DriverManager.setLoginTimeout(15);

            output("getLoginTimeout() after  "+
                "setLoginTimeout(15)= " +
                DriverManager.getLoginTimeout() + "\n");

            // Note, would be nice to use PrintWriter, but the DriverManager
            // does not support it yet.

            PrintStream logstream = null;
            if(!_anApplet)
            {
                logstream = new PrintStream(new
                    FileOutputStream("./DriverMgr.out"));
            }
            else
            {
                error("Since you are running as an applet, \n"
                    +" A log file could not be created, so \n"
                    +" any log output will be sent to stdout.\n");
                logstream = System.out;
            }

            DriverManager.setLogStream(logstream);
            DriverManager.println("Writing to the Driver Manager LogStream\n");

            // Deregister the driver
            output("executing dregisterDriver(_driver)\n");
            DriverManager.deregisterDriver(_driver);
            displayDrivers();

            //  Test out getDriver()

            Driver adriver = DriverManager.getDriver(_url);
            output("Driver which can acces url '" + _url +
                "' is: " + adriver.getClass().getName() + "\n");


        }
        catch (Exception ex)
        {

            ex.printStackTrace ();
        }
    }
    /**
     * Displays all of the currently loaded JDBC drivers
     */
    public void displayDrivers()
    {
        output("Registered JDBC Drivers:\n");
        for(Enumeration edrivers = DriverManager.getDrivers();
        edrivers.hasMoreElements();)
        {
            output( edrivers.nextElement().toString() + "\n");
        }
    }

}
