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
 * The SimpleSelect class demonstrates how a simple select 
 * statement is executed.
 * <UL>
 * <LI>It creates a connection and a statement.
 * <LI>Then query is executed. The pubs2 database needs to be installed
 * in your SQL Server. If it is not installed change the query string
 * and recompile SimpleSelect.
 * <LI>The result set is displayed.
 * <LI>Finally, the statement and connection are closed.
 * </UL>
 *
 * <P>SimpleSelect may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see java.sql.Connection
 *  @see java.sql.Statement
 *  @see Sample
 */

public class SimpleSelect extends Sample
{


    SimpleSelect()
    {
        super();
    }

    public void sampleCode()
    {
        String query = "SELECT * FROM pubs2..titles"; 

        try 
        {

            // Get the DatabaseMetaData object and display
            // some information about the connection
            // by calling  a method that displays driver name and version

            printDriverInfo();

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
