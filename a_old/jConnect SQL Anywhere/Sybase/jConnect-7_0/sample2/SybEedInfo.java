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
import com.sybase.jdbcx.EedInfo;

/**
 * SybEedInfo class demonstrates how to use the EedInfo interface
 * class which extends SQLException which extends SQLWarnings.<p>
 *
 * The program also demonstrates how to obtain the output from the
 * T-SQL print command via SQLWarning.<p>
 *
 * SybEedInfo may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class SybEedInfo extends Sample
{

    SybEedInfo()
    {
        super();
    }

    public void sampleCode()
    {

        String createQuery = "create table #test(f1 int, f2 char(10))";
        String insertQuery = "insert #test values(1,NULL)";


        try
        {

            // Demonstrate SQLWarning

            execDDL("print 'hello world'");

            // Create our table

            execDDL(createQuery);

            // Now insert our data which will result in an SQLException
            // due to inserting a NULL into a non-NULL column

            execDDL(insertQuery);

        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
    }
    /**
     * Overload checkForWarning
     * Checks for and displays warnings.  Returns true if a warning
     * existed
     * @param warn   SQLWarning object
     * @return       True if we displayed a warning
     * @exception SQLException .
     */

    public boolean checkForWarning (SQLWarning warn) throws SQLException
    {
        boolean rc = false;

        // If a SQLWarning object was given, display the
        // warning messages.  Note that there could be
        // multiple warnings chained together

        if (warn != null)
        {
            output("\n *** Warning ***\n");
            rc = true;
            while (warn != null)
            {

                output ("Error:   " + warn.getErrorCode () +"\n");
                output ("Message:  " + warn.getMessage () + "\n");

                if(warn instanceof EedInfo)
                {
                    // This SQLWarning contains additional Sybase Adaptive
                    // Server error message info.

                    EedInfo eed = (EedInfo) warn;
                    output("      Severity: " + eed.getSeverity() + "\n");
                    output("   Line Number: " + eed.getLineNumber() +
                        "\n");
                    output("   Server Name: " + eed.getServerName() +
                        "\n");
                    output("   Error State: " + eed.getState() + "\n");
                    output("Procedure Name: " + eed.getProcedureName() +
                        "\n");

                }

                output ("SQLState: " + warn.getSQLState () + "\n");
                warn = warn.getNextWarning ();
            }
        }
        return rc;
    }

    /**
     * Overload displaySQLEx to support EedInfo
     * @param ex   SQLException object
     */
    public void displaySQLEx(SQLException ex)
    {
        // A SQLException was generated.  Catch it and
        // display the error information.  Note that there
        // could be multiple error objects chained
        // together

        output ("\n*** SQLException caught ***\n");

        while (ex != null) 
        {

            output ("Error:   " + ex.getErrorCode ()+ "\n");
            output ("Message:  " + ex.getMessage () + "\n");

            if(ex instanceof EedInfo)
            {
                // This SQLException contains additional Sybase Adaptive
                // Server error message info.

                EedInfo eed = (EedInfo) ex;
                output("      Severity: " + eed.getSeverity() + "\n");
                output("   Line Number: " + eed.getLineNumber()+"\n");
                output("   Server Name: " + eed.getServerName()+"\n");
                output("   Error State: " + eed.getState() + "\n");
                output("Procedure Name: " + eed.getProcedureName()
                    + "\n");

            }

            output ("SQLState: " + ex.getSQLState () + "\n\n");
            ex = ex.getNextException ();
        }

    }
}
