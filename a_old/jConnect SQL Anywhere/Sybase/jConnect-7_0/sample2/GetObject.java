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
 * GetObject class demonstrates how to use the getObject method. In
 * This example, we will get a datetime column using getObject() and
 * getTimestamp() and demonstrate that they are both Timestamp objects<p>
 *
 * GetObject may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class GetObject extends Sample
{

    GetObject()
    {
        super();
    }

    public void sampleCode()
    {

        String query = "select pub_id, pubdate, pubdate from titles";

        try
        {

            // Create our table
            Statement statement = _con.createStatement();
            output("Executing: " + query + "\n");

            ResultSet rs = statement.executeQuery (query);
            // Get the ResultSetMetaData.  This will be used for
            // the column headings
            ResultSetMetaData rsmd = rs.getMetaData ();

            // Get the number of columns in the result set

            int numCols = rsmd.getColumnCount ();

            // Display column headings

            for (int i=1; i<=numCols; i++) 
            {
                if (i > 1) 
                output("\t\t");
                output(rsmd.getColumnLabel(i));
            }
            output("\n");


            // Display data, fetching until end of the result set

            while (rs.next ())
            {

                // Loop through each column, getting the column data 
                // Columns 2 and 3 are defined as datetime.
                // We will get the column 2 using getObject and column 3
                // using getTimestamp.

                output(rs.getString(1));
                Object a = rs.getObject(2);
                output("\t" + a);

                if(a instanceof Timestamp)
                output(" (is a Java Timestamp)");
                Timestamp ts = rs.getTimestamp(3);
                output("\t" + ts);

                if(ts instanceof Timestamp)
                output(" (is a Java Timestamp)");
                output("\n");

                // Fetch the next result set row

            }
            rs.close();

        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
    }
}
