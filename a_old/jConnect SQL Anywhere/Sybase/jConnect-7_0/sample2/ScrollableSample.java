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
import sample2.ExtendedResultSet;

/**
 * ScrollableSample class demonstrates how to use the methods in the
 * ExtendedResultSet class.  This provides some of the functionality
 * found in the JDBC 2.0 ResultSet class for scrollable ResultSet support.<br>
 *
 * <P>ScrollableSample may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */

public class ScrollableSample extends Sample
{


    ScrollableSample()
    {
        super();
    }

    public void sampleCode()
    {
        String query = "select pub_id, pub_name from publishers"; 

        try 
        {


            // Execute the desired DML statement and then call dispResultSet to
            // display the rows and columns

            Statement stmt = _con.createStatement();;
            output("Executing: " + query + "\n");
            ExtendedResultSet rs = new ExtendedResultSet(
                stmt.executeQuery (query));

            // Save the direction, concurrencty the Scrolling type for the
            // ResultSet.
            String direction = null;
            String concurrency = null;
            String scrollType = null;

            switch(rs.getFetchDirection())
            {
                case ExtendedResultSet.FETCH_FORWARD:
                    direction = "FETCH_FORWARD";
                    break;
                case ExtendedResultSet.FETCH_REVERSE:
                    direction = "FETCH_REVERSE";
                    break;
                case ExtendedResultSet.FETCH_UNKNOWN:
                    direction = "FETCH_UNKNOWN";
                    break;
            }

            switch(rs.getConcurrency())
            {
                case ExtendedResultSet.CONCUR_READ_ONLY:
                    concurrency = "CONCUR_READ_ONLY";
                    break;
                case ExtendedResultSet.CONCUR_UPDATABLE:
                    concurrency = "CONCUR_UPDATABLE";
                    break;
            }
            switch(rs.getType())
            {
                case ExtendedResultSet.TYPE_FORWARD_ONLY:
                    scrollType = "TYPE_FORWARD_ONLY";
                    break;
                case ExtendedResultSet.TYPE_SCROLL_INSENSITIVE:
                    scrollType = "TYPE_SCROLL_INSENSITIVE";
                    break;
            }

            output("\ngetFetchDirection()= " + direction + " (" +
                rs.getFetchDirection()  + ")\n" 
                + "getFetchSize() = " + rs.getFetchSize() + "\n"
                + "getType() = " + scrollType + " (" + rs.getType() + ")\n"
                + "getConcurrency() = " + concurrency + " (" +
                rs.getConcurrency() + ")");

            // Now demonstrate some of the Scrollable features
            output("\nisBeforeFirst() = " + rs.isBeforeFirst() + "\n");
            dispResultSet(rs);
            output("\nisAfterLast() = " + rs.isAfterLast());

            // Move to 1st row in ResultSet
            output("\nMove to 1st Row");
            rs.first();
            output("\nrow number = " + rs.getRow() + "  " +
                rs.getString(1) + ", " + rs.getString(2));
            output("\nisFirst() = " + rs.isFirst());

            // Move to 3rd row
            output("\nMove to Row 3");
            rs.absolute(3);
            output("\nrow number = " + rs.getRow() + "  " +
                rs.getString(1) + ", " + rs.getString(2));

            // Move to back 1 row
            output("\nMove back  1 Row ");
            rs.relative(-1);
            output("\nrow number = " + rs.getRow() + "  " +
                rs.getString(1) + ", " + rs.getString(2));

            // Move to back 1 row
            output("\nMove back  1 Row ");
            rs.previous();
            output("\nrow number = " + rs.getRow() + "  " +
                rs.getString(1) + ", " + rs.getString(2));

            // Move to last row
            output("\nMove to last Row ");
            rs.last();
            output("\nrow number = " + rs.getRow() + "  " +
                rs.getString(1) + ", " + rs.getString(2));
            output("\nisLast()= " + rs.isLast());


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
