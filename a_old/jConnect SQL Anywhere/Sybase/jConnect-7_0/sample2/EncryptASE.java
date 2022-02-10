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
 * EncryptASE demonstrates how to use the SSL encryption with jConnect
 * by utilizing a custom socket implementation of javax.net.SSLSocket, 
 * and javax.net.SSLSocketFactory.
 *
 * <P> NOTE: This sample differs from the Encrypt.java sample in that this
 *     sample must be run under JDK 1.4. In order to use this sample: 
 * <UL> 
 *     <LI> You must run this using JDK 1.4 or higher. JDK 1.4
 *          is available for free from Sun.
 *     <LI> Before you run this sample, you must separately compile
 *          MySSLSocketFactoryASE.java with JDK 1.4 or higher. Please read
 *          the source code for MySSLSocketFactoryASE.java for details on
 *          how to configure that class for your SSL environment.
 *     <LI> The SSLSocket can only communicate to a server that speaks
 *          SSL and has common cipher suites enabled. This sample is 
 *          designed to exhibit how to connect to an ASE (version
 *          12.5 or higher) which has SSL enabled and has an SSL port
 *          in operation. See the ASE documentation for information on how
 *          to configure ASE for SSL communication. 
 * </UL>
 *
 * <P>EncryptASE.java must be invoked with the parameters:
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class EncryptASE extends Sample
{

    /**
     * Language query - you may wish to customize this query
     */
    public static final String QUERY = "select name from syslogins";

    /**
     * constructor
     */
    EncryptASE()
    {
        super();
    }

    /**
     * Set connection properties
     * @param cmdline - commandline args object
     */
    public void addMoreProps(CommandLine cmdline)
    {
        // set the SYBSOCKET_FACTORY property
        cmdline._props.put("SYBSOCKET_FACTORY", 
            "sample2.MySSLSocketFactoryASE");
    }

    /**
     * Demonstrate the use of SSL Encryption
     * with a PreparedStatement object
     */
    public void sampleCode()
    {
        try
        {

            PreparedStatement pstmt = _con.prepareStatement(QUERY);
            output("Executing: " + QUERY + "\n");

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
