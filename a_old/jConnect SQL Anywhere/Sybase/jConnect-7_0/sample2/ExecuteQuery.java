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
 * ExecuteQuery class demonstrates how to use the executeQuery method<br>
 *
 * <P>ExecuteQuery may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */

public class ExecuteQuery extends Sample
{


    ExecuteQuery()
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
            ResultSet rs = stmt.executeQuery (query);
            dispResultSet(rs);

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
