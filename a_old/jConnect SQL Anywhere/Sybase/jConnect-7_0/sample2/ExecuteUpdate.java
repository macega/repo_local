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
 * ExecuteUpdate class demonstrates how to use the executeUpdate method<br>
 *
 * <P>ExecuteUpdate may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class ExecuteUpdate extends Sample
{

    ExecuteUpdate()
    {
        super();
    }

    public void sampleCode()
    {

        String createQuery = "create table #test(f1 int, f2 char(10))"; 
        String insertQuery = "insert #test values(1, 'Lance')"; 
        String updateQuery = "update #test set f2= 'Tonya'"; 
        String selectQuery = "select * from #test";

        try
        {

            // Create our table
            Statement statement = _con.createStatement();
            output("Executing: " + createQuery + "\n");
            int numrows = statement.executeUpdate(createQuery);
            output("Number of rows affected= " + numrows + "\n");

            // Now insert  our data
            output("Executing: " + insertQuery);
            numrows = statement.executeUpdate(insertQuery);
            output("Number of rows affected= " + numrows + "\n");

            // Display the new row
            output("Executing: " + selectQuery + "\n");
            ResultSet rs = statement.executeQuery (selectQuery);
            dispResultSet(rs);
            rs.close();


            // Now Update the row
            output("Executing: " + updateQuery + "\n");
            numrows = statement.executeUpdate(updateQuery);
            output("Number of rows affected= " + numrows + "\n");

            // Display the updated row
            output("Executing: " + selectQuery + "\n");
            rs = statement.executeQuery (selectQuery);
            dispResultSet(rs);
            rs.close();
        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
    }
}
