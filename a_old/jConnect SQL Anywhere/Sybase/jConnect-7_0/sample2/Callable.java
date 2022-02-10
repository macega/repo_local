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
 * <P>Callable class demonstrates how a callable statement is executed.
 *
*  <UL>
 *   <LI> Prepares a callable statement
 *   <LI> Passes parameters to the Callable Statement
 *   <LI> Executes the Callable Statement
 *   <LI> Retrieves, and compares the return value, and output 
 *        parameters with what was expected.
 * </UL> 
 * Due to permissions restricting a guest from creating procedures, 
 * we will not be creating a stored procedure, only executing it.
 * The required stored procedures have been pre-loaded
 * onto our demo server, and included in pubs2_sql.sql or pubs2_any.sql
 * for you to be able to run them from your server.
 * We have included comments on how you would actually execute the
 * creation of a stored procedure and drop within the *CREATE PROCEDURE*
 * comments throughout the sample.
 *
 * Callable may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 *  @see java.sql.CallableStatement
 *  @see java.sql.Statement
 */
public class Callable extends Sample
{


    Callable()
    {
        super();
    }

    public void sampleCode()
    {
        boolean returnval = false;
        int intParam = 42;
        String procName = "sp_callableSample";
        String sendString = "The Answer to Life, the Universe, and Everything.";

        /* *CREATE PROCEDURE*
          String createProc =
            "create procedure " + procName +
            "(@p1 int, @p2 varchar(255) out) " +
            "as begin " +
            "select @p1, @p2 " +
            "select @p2 = '" + sendString + "' " +
            "return " + intParam +
            " end";
        */

        try 
        {
            // Display info about the jConnect Driver
            printDriverInfo();

            /* *CREATE PROCEDURE*
              // Create a procedure to execute -- 2 input params,
              // 1 resultSet w/1 row, 1 output param, 1 returned status
              output("Creating stored procedure...");
              Statement create = _con.createStatement();
              try
              {
                  create.executeUpdate(createProc);
              }
              catch (SQLException sqe)
              {
                  error(" Attempted to use tempdb to" 
                      + "create a stored procedure, but failed most likely"
                      + "due to permissions not being granted on the server\n");
                  throw sqe;
              }
              create.close();
             */

            // Prepare the call
            output("Preparing the call...\n");
            CallableStatement stmt = _con.prepareCall(
                "{?=call " + procName + "(?, ?)}");

            // The return status (i.e. parameter #1) can't be set.

            // Set the first argument (i.e. input parameter #2), an integer
            stmt.setInt(2, intParam);

            // Set the second argument (i.e. input parameter #3), a string
            stmt.setString(3, sendString);

            // Register the return-status
            stmt.registerOutParameter(1, Types.INTEGER);

            // Register the output parameter
            stmt.registerOutParameter(3, Types.VARCHAR);

            // Send the query
            output("Executing the procedure...\n");
            ResultSet rs = stmt.executeQuery();
            if (rs == null) 
            {
                error("Error: Expected results\n");
                returnval = true;
            }
            else
            {
                // Get the next row
                if (! rs.next())
                {
                    error("Error: Expected rows\n");
                    returnval = true;
                }
                else
                {
                    // Get first column
                    int gotInt = rs.getInt(1);
                    if (gotInt != intParam)
                    {
                        error(
                            "Expected a " + intParam + 
                            " returned in Column1, got " + gotInt + "\n");
                        returnval = true;
                    }
                    // Get second column
                    String gotString = rs.getString(2);
                    if (gotString == null)
                    {
                        error("Expected a String\n");
                        returnval = true;
                    }
                    else if (!gotString.equals(sendString))
                    {
                        error(
                            "Expected '" + sendString + "' , got " + 
                            gotString + "\n");
                        returnval = true;
                    }
                }
            }

            // Get parameter #1 (the return status)
            int returnStat = stmt.getInt(1);
            if (returnStat != intParam)
            {
                error(
                    "Expected a " + intParam + 
                    " return status, got " + returnStat + "\n");
                returnval = true;
            }
            else
            {
                output(
                    "got expected return status '" + returnStat + "'\n");
            }

            // We can't get parameter #2 because it was not
            // registered as OUTPUT.

            // Get parameter #3
            String outString = stmt.getString(3);
            if (outString == null)
            {
                error("Error: Expected a String\n");
                returnval = true;
            }
            else if (!outString.equals(sendString))
            {
                error(
                    "Error: Expected '" + sendString + "', got " + outString 
                    + "\n");
                returnval = true;
            }
            else
            {
                output(
                    "got expected output string '" + outString + "'\n");
            }

            stmt.close();

            /* *CREATE PROCEDURE*
              // drop the procedure
              output("Dropping the procedure...\n");
              Statement drop = _con.createStatement();
              drop.executeUpdate("drop procedure " + procName);
              drop.close();
            */

        }
        catch (SQLException ex)   
        {
            displaySQLEx(ex);
        }
        if (returnval == true)
        output("Error\n");
        else
        output("All returned values correct.\n");

    }

}
