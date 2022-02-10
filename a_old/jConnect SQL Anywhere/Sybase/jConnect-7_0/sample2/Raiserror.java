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
 * Raiserror class demonstrates how to process raiserror messages
 * that are raised as SQLExceptions<p>
 *
 * Due to permissions restricting a guest from creating procedures, 
 * we will not be creating a stored procedure, only executing it.
 * The required stored procedures have been pre-loaded
 * onto our demo server, and included in pubs2_sql.sql or pubs2_any.sql
 * for you to be able to run them from your server.
 * We have included comments on how you would actually execute the
 * creation of a stored procedure and drop within the *CREATE PROCEDURE*
 * comments throughout the sample.
 *
 * Raiserror may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class Raiserror extends Sample
{

    Raiserror()
    {
        super();
    }

    public void sampleCode()
    {


        String procName = "sp_raiserrorSample";
        /* *CREATE PROCEDURE*
           String dropProc = "drop proc " + procName;
           String createProc =
               "create proc " + procName  +
               " as " +
               "raiserror 24000 'I raised this error'"  +
               "raiserror 24001 'I raised this error'"  +
               "raiserror 25000 'I raised this error'"  +
               "\nselect au_id,au_fname, au_lname from authors "  +
               "raiserror 26000 'I raised this error'"  +
               "\nselect title_id, type, price from titles"  ;
        */

        try
        {
            /* *CREATE PROCEDURE*
               output("Going to execute the create proc\n");
               // Create the Proc
               execDDL(createProc);
            */

            output("Going to create stmt \n");
            Statement stmt = _con.createStatement();;
            output("Executing: " + procName + "\n");
            int rsnum = 0;                   // Number of Result Sets processed
            int rowsAffected = 0;
            boolean results = false;
            ResultSet rs = null;

            try
            {
                results = stmt.execute(procName);
            }
            catch(SQLException ex)
            {
                output("Exception on execute()\n");
                processSQLException( ex);

                // Because we received an SQLException, we need to poll to see
                // If there are more results to process.
                results = getMoreResults(stmt);
            }
            do
            {

                if(results)
                {
                    try
                    {
                        rs = stmt.getResultSet();
                    }
                    catch(SQLException ex)
                    {
                        output("Exception while invoking getResultSet\n");
                        processSQLException( ex);
                    }
                    output("\n\nDisplaying ResultSet: " + rsnum + "\n");
                    dispResultSet(rs);
                    rsnum++;

                    rs.close();
                }
                else
                {
                    rowsAffected = stmt.getUpdateCount();
                    if (rowsAffected >= 0)
                    output(rowsAffected + " rows Affected.\n");
                }
                //  Created a Raiserror.getMoreResults() method to handle Multiple
                //  raiserrors in a row.  Would be more elegant to extend
                //  the ResultSet class.
                results = getMoreResults(stmt);
            }
            while (results || rowsAffected != -1);

            stmt.close();


            /* *CREATE PROCEDURE*
               //  drop the Proc
               execDDL(dropProc);
            */
        }
        catch (SQLException ex)
        {
            processSQLException(ex);
        }
    }

    /**
     * Process a SQLException.
     * @param ex  SQLException object
     * @return    A boolean stating whether we are here due to Raiserror
     */
    public boolean processSQLException(SQLException ex)
    {
        // A SQLException was generated.  Catch it and
        // display the error information.  Note that there
        // could be multiple error objects chained
        // together

        boolean gotRaiserror = false;


        while (ex != null) 
        {
            if(ex.getSQLState()== null && ex.getErrorCode() >= 17000)
            {
                // The SQLException is due to a raiserror command.

                output("***Raiserror encountered***\n");
                output ("Error:   " + ex.getErrorCode () + "\n");
                output ("Message:  " + ex.getMessage () + "\n");
                gotRaiserror = true;

            }
            else
            {
                output ("\n*** SQLException caught ***\n");
                output ("Error:   " + ex.getErrorCode ()+ "\n");
                output ("Message:  " + ex.getMessage () + "\n");
                output ("SQLState: " + ex.getSQLState () + "\n\n");
            }
            ex = ex.getNextException ();
        }

        return(gotRaiserror);

    }
    /**
     * Execute ResultSet.getMoreResults().  If an exception was raised due
     * to a raiserror command, call ResultSet.getMoreResults() a second time
     * @param stmt     Statement object to use
     * @return         boolean which indicates whether a raiserror command
     *                 was encountered
     */

    public boolean getMoreResults(Statement stmt)
    {
        boolean gotRaiserror = false;
        boolean results = false;

        try
        {
            results = stmt.getMoreResults();
        }
        catch(SQLException ex)
        {
            output("Exeception invoking getMoreResults\n");
            gotRaiserror= processSQLException( ex);
            if(gotRaiserror)
            {
                results = getMoreResults(stmt);
            }
        }
        return (results);
    }


}
