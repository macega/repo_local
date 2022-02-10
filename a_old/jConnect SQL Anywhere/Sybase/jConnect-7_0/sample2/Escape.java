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
 * Escape class demonstrates how to use  jConnect Escape Syntax<p>
 *
 * Escape may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class Escape  extends Sample
{

    static public final int   MAXROWS = 5;
    static public final int   MAJORVERSION = 2;
    static public final int   JDBC_CLIENT = 5;

    Escape()
    {
        super();
    }

    public void sampleCode()
    {

        String queryFN = "select {fn database()}"; 
        String queryLike = 
            "select name from syscolumns where name like '%\\_%' {escape '\\'}"; 
        String queryDate =
            "select * from sales where date = {d '1991-03-20'}"; 
        String queryTimestamp =
            "select * from sales where date = {ts '1988-01-13 00:00:00'}"; 
        String queryCall = "{call sp_mda(?,?)}";

        //  Note, this currently is only supported by SQL Anywhere
        String queryOuterJoin=
            "select au_fname, au_lname, pub_name from {oj authors " +
            "left outer join publishers on authors.city = publishers.city}";


        try 
        {

            //  Display escape scalar function example
            displayRows(queryFN);

            //  Display like escape example
            displayRows(queryLike);

            //  Display date escape example
            displayRows(queryDate);

            //  Display timestamp escape example
            displayRows(queryTimestamp);

            //  Display outer join escape example
            try
            {
                displayRows(queryOuterJoin);
            }
            catch(SQLException ex)
            {

                // If this is Adaptive (SQL) Server, ANSI Left Outer Join
                // is not supported so display a message
                // For other RDBMS, a different error code might occur
                // Adapative Server Anywhere does support this.

                if (ex.getErrorCode() == 102)
                {
                    output("**ANSI Left Outer Join is not supported**\n");
                }
                else
                {
                    throw ex;
                }
            }

            //  Demonstrate escape call example
            CallableStatement cs = _con.prepareCall(queryCall);
            cs.setInt(1, JDBC_CLIENT);
            cs.setInt(2, MAJORVERSION);

            cs.setMaxRows(MAXROWS);
            output("Executing: " + queryCall +"\n");

            ResultSet rs = cs.executeQuery ();
            dispResultSet(rs);
            rs.close();
            cs.close();


        }
        catch (SQLException sqe)
        {
            displaySQLEx(sqe);
        }
    }


    /**
     * Execute the desired DML statement and then call dispResultSet to
     * display thre rows and columns 
     * @param query  SQL query to execute
     * @exception SQLException .
     */
    public void displayRows( String query) 
        throws SQLException
    {

        Statement stmt = _con.createStatement();
        output("Executing: " + query + "\n");
        ResultSet rs = stmt.executeQuery (query);
        dispResultSet(rs);
        rs.close();
    }


}
