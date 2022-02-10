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
 * MyResultSet class demonstrates how to use the  ResultSet class.
 * This example uses the wasNull() method to check for SQL NULLs
 * <p>
 *
 * MyResultSet may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *
 *  @see Sample
 */
public class MyResultSet extends Sample
{


    MyResultSet()
    {
        super();
    }

    public void sampleCode()
    {
        String createQuery = "create table #foobar(f1 varchar(20) null,"  +
            " f2 int null)";
        String insertQuery = "insert #foobar values(null, 1)";
        String insertQuery1 = "insert #foobar values('Hello Lance',null)";
        String selectQuery = "select * from #foobar"; 

        try 
        {

            // create our table and populate it with two rows
            execDDL(createQuery);
            execDDL(insertQuery);
            execDDL(insertQuery1);

            // Execute the desired DML statement and then  display the 
            // rows and columns

            Statement stmt = _con.createStatement();;
            output("Executing: " + selectQuery + "\n");
            ResultSet rs = stmt.executeQuery (selectQuery);

            // Get the ResultSetMetaData.  This will be used for
            // the column headings

            ResultSetMetaData rsmd = rs.getMetaData ();
            int numCols = rsmd.getColumnCount ();

            // Display column headings

            for (int i=1; i<=numCols; i++) 
            {
                if (i > 1) output("\t\t");
                output(rsmd.getColumnLabel(i));
            }

            output("\n");

            // Display data, fetching until end of the result set

            while (rs.next ())
            {

                // Loop through each column, getting the
                // column data and displaying

                for (int i=1; i<=numCols; i++) 
                {
                    if (i > 1) 
                    output("\t\t");
                    String  foobar = rs.getString(i);

                    // Display NULL if a SQL NULL was encountered
                    output( rs.wasNull() ?   "NULL" : foobar);
                }
                output("\n");

                // Fetch the next result set row
            }

            // Close our resources

            rs.close();
            stmt.close();


        }
        catch (SQLException ex)   
        {
            displaySQLEx(ex);
        }
    }

}
