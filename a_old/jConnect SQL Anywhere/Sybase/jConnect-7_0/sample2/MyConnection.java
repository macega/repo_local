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
 * MyConnection class demonstrates how to use the Connection class methods<br>
 *
 * <P>MyConnection may be invoked with the optional parameters:<br>
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class MyConnection extends Sample
{

    MyConnection()
    {
        super();
    }

    public void sampleCode()
    {


        try
        {
            // use tempdb as pubs2 on the jConnect server is read only
            execDDL("use tempdb");

            // Display what the JDBC funtion database() is on this RDBMS

            output("Native SQL for: select {fn database()} =  ");
            output("\t\t" + _con.nativeSQL("select {fn database()}") 
                + "\n");

            output("getAutoCommit= " +  _con.getAutoCommit() + "\n");

            // Change AutoCommit State
            _con.setAutoCommit (false);
            output("getAutoCommit() after setAutoCommit(false)= " +
                _con.getAutoCommit() + "\n");

            output("getTransactionIsolation()= " +
                _con.getTransactionIsolation() + "\n");

            // Connection Transaction levels
            // TRANSACTION_READ_UNCOMMITTED=1   (iso 0)
            // TRANSACTION_READ_COMMITTED=2     (iso 1)
            // TRANSACTION_REPEATABLE_READ=4    (iso 2)
            // TRANSACTION_SERIALIZABLE=8       (iso 3)

            output("TRANSACTION_READ_UNCOMMITTED= " +
                _con.TRANSACTION_READ_UNCOMMITTED +"  (iso 0)\n");
            output("TRANSACTION_READ_COMMITTED= " +
                _con.TRANSACTION_READ_COMMITTED + "   (iso 1)\n");
            output("TRANSACTION_REPEATABLE_READ= " +
                _con.TRANSACTION_REPEATABLE_READ + "   (iso 2)\n");
            output("TRANSACTION_SERIALIZABLE= " +
                _con.TRANSACTION_SERIALIZABLE   + "   (iso 3)\n");

            _con.setTransactionIsolation(
                Connection.TRANSACTION_READ_UNCOMMITTED);
            output("getTransactionIsolation() after calling " +
                "setTransactionIsolation( " +
                "Connection.TRANSACTION_READ_UNCOMMITTED)="
                + _con.getTransactionIsolation() + "\n");

            output("isReadOnly=  " +  _con.isReadOnly() + "\n");

            // Note: setReadOnly() can not be implemented on Adaptive Server
            // and Adaptive Server Anywhere

            _con.setReadOnly(true);
            output("isReadOnly() after calling setReadOnly(true)=  " +
                _con.isReadOnly() + "\n");

            output("getCatalog()=  " +  _con.getCatalog() +"\n");
            _con.setCatalog("master");
            output("getCatalog() after calling setCatalog()=  " +
                _con.getCatalog() + "\n");

            output("isClosed()=  " +  _con.isClosed() + "\n");


        }
        catch (SQLException ex)
        {
            displaySQLEx(ex);
        }
    }
}
