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
 * UpdateCursor class demonstrates how to use  multiple Statement objects
 * to do an update using a cursor<br>
 *
 * <P>UpdateCursor may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class UpdateCursor extends Sample
{


    UpdateCursor()
    {
        super();
    }

    public void sampleCode()
    {

        String query =
            "select au_id, au_lname, au_fname from #authors  for update";
        String createQuery = "select * into #authors from authors" +
            "\ncreate unique clustered index myind on #authors(au_id)";
        String au_id= "486-29-1786";
        String au_fname = "Lance";
        String au_lname = "Andersen";
        String selectQuery =
            "select au_id, au_lname, au_fname from #authors where au_id ='" +
            au_id + "'";


        try
        {

            // Create our temp table and unique index for our cursor
            execDDL(createQuery);

            // Demonstrate how to use multiple Statement objects  to modify
            // an existing row using a cursor

            ResultSet rs = null;
            String cursorName = new String("read_authors");
            Statement stmt1 = _con.createStatement();
            Statement stmt2 = _con.createStatement();
            Statement stmt3 = _con.createStatement();

            // Display the row before the update
            output("Executing: " + selectQuery + "\n");
            rs = stmt3.executeQuery (selectQuery);
            dispResultSet(rs);
            rs.close();
            // Open our cursor
            stmt1.setCursorName(cursorName);
            output("Executing: " + query + "\n");
            rs = stmt1.executeQuery(query);

            String cursor = rs.getCursorName();
            while(rs.next())
            {
                if(rs.getString("au_id").equals(au_id))
                {
                    // Update our row using the current cursor position

                    output( "\n\nModifying: au_id= " + au_id +
                        ", au_lname= " + rs.getString("au_lname") +
                        ", au_fname= " + rs.getString("au_fname") + "\n");
                    String updateQuery = 
                        "update #authors set au_lname = '" + au_lname
                        + "',au_fname= '" + au_fname + 
                        "' where current of " + cursor;

                    output("Executing: " + updateQuery + "\n");
                    stmt2.executeUpdate(updateQuery);
                }
            }

            // Display the updated row
            output("Executing: " + selectQuery + "\n");
            rs = stmt3.executeQuery (selectQuery);
            dispResultSet(rs);
            rs.close();

            stmt1.close();
            stmt2.close();
            stmt3.close();

        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
    }
}
