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
import com.sybase.jdbcx.*;

/**
 *
 * NameBindRPC class demonstrates how to execute a stored procedure
 * via a language command in a Callable Statement using T-SQL
 * Name-Binding for parameters.<p>
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
 * NameBindRPC may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */

class NameBindRPC extends Sample
{

    NameBindRPC()
    {
        super();
    }

    public void sampleCode()
    {


        String procName = "sp_nameBindRPCSample";
        String procStmt = "{?=call " + procName + " @p4=?, @p2=?}";

        /*   *CREATE PROCEDURE*
           String dropProc = "drop proc " + procName;
           String createProc =
               "create proc " + procName  +
               " (@p2 int, @p3 int = 47, @p4 char(30)) " +
               " as " +
               " print 'This is a print statement' " +
               " select @p2, @p3, @p4" +
               " return 1";

        */


        try
        {


            /* *CREATE PROCEDURE*
               //  Create the Proc
               execDDL(createProc);
            */

            CallableStatement cstmt = _con.prepareCall(procStmt);
            output("Executing: " + procName + "\n");

            // Define the input/output params
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setString(2, "Yikes");
            cstmt.setInt(3, 123);

            ResultSet rs = cstmt.executeQuery();


            while(rs.next())
            {

                // we expect 123, 47, "Yikes" -- we passed in the first
                // and 3rd values (out of order) and picked up the
                // 2nd one as the default value.
                output("Expecting output of: 123, 47, 'Yikes'\n");
                for (int i = 1; i <= 3; i++)
                {
                    output("Column " + i + ": " + rs.getString(i) + "\n");
                }
                output("return status= " + cstmt.getString(1) + "\n");

            }
            cstmt.close();

            /*  *CREATE PROCEDURE*
               //  drop the Proc
               execDDL(dropProc);
            */


        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
    }

}
