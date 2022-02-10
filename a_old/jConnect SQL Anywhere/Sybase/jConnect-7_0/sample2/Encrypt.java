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
 * <P>Encrypt class demonstrates how to use the SSL encryption with jConnect
 * by utilizing a custom socket implementation of javax.net.SSLSocket, 
 * and javax.net.SSLSocketFactory.
 *
 * <P><B>NOTE</B>: Customers wishing to use SSL encryption when connecting
 * to Sybase Adaptive Server Enterprise (version 12.5 or higher) should look
 * at the EncryptASE.java and MySSLSocketFactoryASE.java samples. The
 * Encrypt.java sample will not connect to ASE 12.5+ using SSL. 
 *
 * <P> NOTE: In order to run a implementation of javax.net.SSLSocket
 * <UL> 
 *     <LI> javax.net.* classes must be in your CLASSPATH if you're using
 *          JDK 1.2 or 1.3 (if you're using JDK1.4 or higher, the SSL 
 *           classes are contained within the java runtime environment.
 *           For more information, refer to the following URL:
 *               http://java.sun.com/products/jsse
 *     <LI> The SSLSocket can only communicate to a ServerSocket that speaks
 *          SSL and has common cipher suites enabled. 
 * 
 *          <BR> Therefore, you will not be able to run this sample unless 
 *          you configure it for your specific cipher suites, and pass in the
 *          url to a SSL Server Socket at connection time.
 * </UL>
 *
 * <P>Encrypt.java must be invoked with the parameters:
 * -U username<br>
 * -P password<br>
 * -D debuglibraries<br>
 * -S server<p>
 *
 *  @see Sample
 */
public class Encrypt extends Sample
{

    /**
     * Language query - you may want to change this to any valid query
     * in your database.
     */
    public static final String QUERY = "select name from syslogins";

    /**
     * constructor
     */
    Encrypt()
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
            "sample2.MySSLSocketFactory");


        // set the cipher suites
        cmdline._props.put("CIPHER_SUITES_1", 
            "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5");
        /*	
	// depending on the cipher suite set on the server
	// side, you could set as many as you would like
        cmdline._props.put("CIPHER_SUITES_2", 
			"SSL_DH_DSS_EXPORT_WITH_DES40_CBC_SHA");
        cmdline._props.put("CIPHER_SUITES_3", 
			"SSL_RSA_EXPORT_WITH_RC2_CBC_40_MD5");
        cmdline._props.put("CIPHER_SUITES_4", 
			"SSL_DH_RSA_EXPORT_WITH_DES40_CBC_SHA");
	*/
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
