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
 * SetObject class demonstrates how to use the setObject method<br>
 *
 * <P>SetObject.java may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class SetObject extends Sample
{

    SetObject()
    {
        super();
    }

    public void sampleCode()
    {
        String fname = "Anne";
        String lname = "Ringer";
        String query = "select au_id, au_lname, au_fname" +
            " from authors where au_lname = ? and au_fname = ?";

        try
        {

            // Demonstrate the use of setObject for defining IN params
            // with a PreparedStatement object

            PreparedStatement pstmt = _con.prepareStatement(query);
            output("Executing: " + query + "\n");

            output("Value being passed for au_lname=" + lname + "\n");
            pstmt.setObject(1, lname);

            // Do the same thing, but simply specify the SQL Type mapping

            output("Value being passed for au_fname=" + fname + "\n");
            pstmt.setObject(2, fname, Types.CHAR);

            ResultSet rs = pstmt.executeQuery ();
            dispResultSet(rs);

            rs.close();
            pstmt.close();


        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
    }
}
