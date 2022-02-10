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
import java.util.*;
import java.sql.*;
import java.io.*;

/**
 * BatchUpdates class demonstrates how batch updates are handled
 * for <code>Statement</code>, <code>PreparedStatement</code>, 
 * and <code>CallableStatement</code>.
 * It also includes a sample for retrieving rowsAffected from 
 * <code>BatchUpdateException</code>.
 *
 * Prepare may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  <BR> see JDBC 2.0 Specification
 *  @see java.sql.CallableStatement
 *  @see java.sql.PreparedStatement
 *  @see java.sql.Statement
 *  @see Sample
 */
public class BatchUpdates extends Sample
{

    BatchUpdates()
    {
        super();
    }

    public void sampleCode()
    {
        boolean returnval = false;
        String table = "batchTable";
        String procName = "sp_updateBatch";

        // According to the JDBC 2.0 Specification, only DDL and DML 
        // commands that return a simple update count may be executed as
        // part of a batch {insert, update, delete, ...}
        // create a table for updating

        /* 
        String createTable = "create table "+table+
            "(empl_no int, company varchar(20), lname varchar(20))";
        String createProc = "create proc "+procName+
            "(@type int, @empl_no int, @lname varchar(20)) as "+
            "begin "+
            "if (@type = 1) "+
            "    insert "+table+" values (@empl_no,'Our Company',@lname) "+
            "else "+
            "    update "+table+" set company = 'Sybase' where lname like 'C%' "+
            "end";
        String dropTable = "drop table "+table;
        String dropProc = "drop "+procName;

        try
        {
            output("create the batch update table and procedure\n");
            Statement stmt = _con.createStatement();
            stmt.executeUpdate("use pubs2");
            stmt.executeUpdate(createTable);

			// this needs to be created in unchained mode
            stmt.executeUpdate(createProc);
			// or buy creating the proc with "anymode"
			stmt.execute("sp_procxmode "+procName+", 'anymode'");
        }
        catch(SQLException sqe)
        {
            error(" Attempted to create a table and stored procedure, but failed: "
                +sqe.toString());
        }
        */

        try
        {
            // Display info about the jConnect Driver
            printDriverInfo();

            // As an example, lets see if the server supportsBatchUpdates
            DatabaseMetaData dma = _con.getMetaData();
            output("Backend supports Batch Update Statements? ");
            if(dma.supportsBatchUpdates())
            {
                output("YES");
            }
            else
            {
                output("NO, stopping sample");
                return;
            }
            Statement stmt = _con.createStatement();
            // this will clean the table every time sample is run
            stmt.executeUpdate("delete "+table);

            // must be in unchained mode
            _con.setAutoCommit(false);

            int[] rowsAffected = null; // rows affected

            output("\nStatement BATCH\n");
            try
            {
                //############ S T A R T * B A T C H ############
                stmt.addBatch("insert "+table+" values (1,'Our Company','Smith')");
                stmt.addBatch("insert "+table+" values (2,'Our Company','Clegg')");
                stmt.addBatch("insert "+table+" values (3,'Our Company','Cartesian')");
                stmt.addBatch("insert "+table+" values (4,'Our Company','Einstein')");
                stmt.addBatch("insert "+table+" values (5,'Our Company','Clinton')");
                stmt.addBatch("insert "+table+" values (6,'Our Company','Reno')");
                stmt.addBatch("insert "+table+" values (7,'Our Company','Franklin')");
                stmt.addBatch("insert "+table+" values (8,'Our Company','King')");
                stmt.addBatch("insert "+table+" values (9,'Our Company','Chavez')");
                stmt.addBatch("insert "+table+" values (10,'Our Company','Ruiz')");
                stmt.addBatch("update "+table+" set company = 'The Practice' "+
                    " where lname like 'R%' or lname like 'E%' ");
                stmt.addBatch("delete "+table+" where lname like 'Clinton'");

                rowsAffected = stmt.executeBatch();
                //############ E N D * B A T C H ############
            }
            catch(BatchUpdateException bue)
            {
                error("Statement BATCH caused an Unexpected "
                    +bue.toString()+"\n");
                rowsAffected = bue.getUpdateCounts(); 
            }
            printRowsAffected(rowsAffected);

            // Now the application level can decide whether or not
            // to commit or rollback the changes
            _con.commit();
            //stmt.execute("commit");

            output("\nPreparedStatement BATCH\n");
            PreparedStatement ps = _con.prepareStatement(
                "update "+table+" set lname = ? where empl_no = ?");
            try
            {
                //############ S T A R T * B A T C H ############
                ps.setString(1,"Smith-Hitachi");
                ps.setInt(2,1);
                ps.addBatch();

                ps.setString(1,"King Jr.");
                ps.setInt(2,8);
                ps.addBatch();

                // sets are remembered
                ps.setString(1,"Luther King Jr.");
                ps.addBatch();

                rowsAffected = ps.executeBatch();
                //############ E N D * B A T C H ############
            }
            catch(BatchUpdateException bue)
            {
                error("PreparedStatement BATCH caused an Unexpected "
                    +bue.toString()+"\n");
                rowsAffected = bue.getUpdateCounts(); 
            }
            printRowsAffected(rowsAffected);

            // Now the application level can decide whether or not
            // to commit or rollback the changes
            _con.commit();
            //stmt.execute("commit");


            output("\nCallableStatement BATCH\n");
            CallableStatement cs = _con.prepareCall(
                "{call "+procName+" (?,?,?)}");
            // NOTE: you may not have out, or inout parameters in batch

            try
            {
                //############ S T A R T * B A T C H ############
                cs.setInt(1,1);
                cs.setInt(2,11);
                cs.setString(3,"Cartier");
                cs.addBatch();

                // will do the update
                cs.setInt(1,2);
                // not need to setXX, because of previous sets
                cs.addBatch();

                cs.setInt(1,1);
                cs.setInt(2,12);
                cs.setString(3,"Carter");
                cs.addBatch();

                cs.setInt(1,1);
                cs.setInt(2,12);
                cs.setString(3,"Beck");
                cs.addBatch();

                cs.setInt(1,1);
                cs.setInt(2,14);
                cs.setString(3,"Jordan");
                cs.addBatch();

                // will do the update -row Affected
                cs.setInt(1,2);
                cs.addBatch();

                rowsAffected = cs.executeBatch();
                //############ E N D * B A T C H ############
            }
            catch(BatchUpdateException bue)
            {
                error("CallableStatement BATCH caused an Unexpected "
                    +bue.toString()+"\n");
                rowsAffected = bue.getUpdateCounts(); 
            }
            printRowsAffected(rowsAffected);

            // Now the application level can decide whether or not
            // to commit or rollback the changes
            //stmt.execute("rollback");
            _con.rollback();


            output("\nBatchUpdateException Example\n");
            stmt = _con.createStatement();
            try
            {
                //############ S T A R T * B A T C H ############
                stmt.addBatch("insert "+table+" values (11,'McGwuire')");
                stmt.addBatch("insert "+table+" values (12,'Sosa')");
                stmt.addBatch("insert "+table+" values (13,'Morris')");
                stmt.addBatch("insert bad"+table+" values (14,'Wong')");
                rowsAffected = stmt.executeBatch();
                //############ E N D * B A T C H ############
            }
            catch(BatchUpdateException bue)
            {
                // caught exception during batch
                // we can find out how many rows were 
                // successful before the error
                rowsAffected = bue.getUpdateCounts();

                // NOTE:  Of all Sybase Backend Products, only Adaptive
                // Server Anywhere allows you to retrieve a successful row
                // int[] > 0.  The other backends automatically rollback
                // the entire transaction, and thus no rows are successfully 
                // affected.
                if(rowsAffected.length == 0)
                {
                    output("0 rows Affected\n");
                }
            }
            printRowsAffected(rowsAffected);

            // Now the application level can decide whether or not
            // to commit or rollback the changes
            if(rowsAffected.length >= 3) // 3/4 is acceptable success.
            {
                //stmt.execute("commit");
                _con.commit();
            }
            else
            {
                //stmt.execute("rollback");
                _con.rollback();
            }

            //reset auto commit, since we are done batching
            _con.setAutoCommit(true);

            output("\nResult Set \n");
            //Now lets print out what's in the table
            ResultSet rs = stmt.executeQuery("select * from "+table);
            dispResultSet(rs);
        }
        catch(SQLException sqe)
        {
            error("Failed to get ResultSet: "+sqe.toString()+"\n");
        }

        /*
        // cleanup
        try
        {
            Statement stmt = _con.createStatement();
            stmt.executeUpdate(dropTable);
            stmt.executeUpdate(dropProc);
        }
        catch(SQLException sqle)
        {
            error("Failed Cleanup\n"+sqle.toString()+"\n");
        }
        */
    }

    /** 
    * print out the rows affected 
    */
    public void printRowsAffected(int[] rowsAffected)
    {
        for(int i = 0; i < rowsAffected.length; i++)
        {
            output(rowsAffected[i]+" Rows Affected\n");
        }
    }

}

