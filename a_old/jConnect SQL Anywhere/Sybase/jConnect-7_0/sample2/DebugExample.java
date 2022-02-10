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
import com.sybase.jdbcx.Debug;
import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * DebugExample class   Demonstrates how to use the Debug Class<br>
 *
 * Note:  You must use $JDBC_HOME/devclasses
 *
 * <P>DebugExample may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class DebugExample extends Sample
{

    DebugExample()
    {
        super();
    }

    public void sampleCode()
    {

        Debug debug = _sybDriver.getDebug();

        String traceOutputFile = "." + System.getProperty("file.separator")
            + "Debug.trace";

        // Trace specific classes.  Could have specified "ALL" to trace
        // all classes
        String debugLibs = "SybConnection:SybStatement:Debug:STATIC";
        String selectQuery = "select pub_id, title_id, title from titles";

        try
        {

            // Open a PrintStream where to redirect the trace output
            PrintStream debugLogStream = null;
            if(!_anApplet)
            {
                debugLogStream = new PrintStream(new
                    FileOutputStream(traceOutputFile));
            }
            else
            {
                //since can't create a file over the wire, 
                //simply print to standard out - Java Console
                error("Since you are running as an applet, \n"
                    +" A log file could not be created, so \n"
                    +" any log output will be sent to stdout.\n");
                debugLogStream = System.out;
            }

            // Enable Debug Tracing
            debug.debug(true, debugLibs, debugLogStream);

            // Write a message to the Debug PrintStream
            debug.println( "*** Calling Connection.createStatement ***");
            Statement stmt = _con.createStatement();
            debug.startTimer(null);
            output("Executing: " + selectQuery + "\n");
            ResultSet rs = stmt.executeQuery (selectQuery);
            debug.stopTimer(null,"*****executeQuery snapshot");
            dispResultSet(rs);

            // Demonstrate the use of asrt()
            // Note that we don't use assert(), because in JDK1.4, assert
            // is a keyword.

            debug.asrt(null, false,"Test of debug.asrt");
            stmt.close();
            rs.close();
        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
