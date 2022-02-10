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

import javax.net.ssl.*;
import javax.net.*;
import java.io.*;
import java.util.*; 
import java.lang.*; 
import java.net.*; 
import com.sybase.jdbcx.SybSocketFactory;

/**
 * <P>This sample is provided to illustrate how to establish a secure
 * connection with Sybase Adaptive Server Enterprise (ASE) version 12.5 
 * or higher.
 *
 * <P><B>IMPORTANT NOTE:</B> This class uses the 
 * javax.net.ssl.SSLSocket.setEnabledProtocols() method, which is available
 * only in JDK 1.4 and higher. Therefore, this class must be compiled using 
 * JDK 1.4+, and must be run using a 1.4+ Java environment.
 *
 * <P>See sample2/EncryptASE.java for an example of how to use this 
 * SybSocketFactory implementation.
 *
 * <P>See below for a brief explanation of the SSL protocol.
 *
 * <BR>
 * <HR>
 * <H1><U>SSL Security Protocol</U></H1>
 * <P>
 * Secure Sockets Layer (SSL) is a security protocol for protecting TCP/IP 
 * communication between a client and server.  SSL provides three basic
 * security features:
 * <UL>
 *    <LI> Encryption, to ensure the privacy of communication
 *    <LI> Authentication, to verify the identity of a client or server and 
 *         prevent impersonations or message forgeries
 *    <LI> Message verification, to make sure that a message has not been 
 *         tampered with.
 * </UL>
 * When jConnect is used with SSL, all TDS communication sent to the server 
 * is encrypted. 
 *
 */
public class MySSLSocketFactoryASE extends SSLSocketFactory 
    implements SybSocketFactory
{
    /**
    * Create a socket, set the cipher suites it can use, return the
    * socket.
    * 
    * @param host - server host name
    * @param port - server port number
    * @return Socket - SSLSocket instance
    * @see javax.net.SSLSocketFactory#createSocket
    */
    public Socket createSocket(String host, int port)
        throws IOException, UnknownHostException
    {

        // Before creating the socket we need to set some system props
        setSystemProperties(); 
        SSLSocket s = (SSLSocket)
            SSLSocketFactory.getDefault().createSocket(host, port);
        setProtocol(s);

        // Everything is set up properly now. Go through the handshake to
        // finalize the ssl connection.
        s.startHandshake();    
        return s;
    }
    /**
    * return an SSLSocket
    * <BR> Demonstrates how to set cipher suites based on
    * connection properties.
    * 
    *  <P>The following is an example of other cipher suites that could
    *  be set, assuming your <code>ServerSocket</code> has them enabled.
    *
    *  <UL>
    *  <LI> Properties _props = new Properties();
    *  <LI> // Set other url, password, etc properties.
    *  <LI> _props.put("CIPHER_SUITES_1",
    *           "SSL_DH_anon_EXPORT_WITH_RC4_40_MD5");
    *  <LI> _props.put("CIPHER_SUITES_2",
    *           "SSL_DH_DSS_EXPORT_WITH_DES40_CBC_SHA");
    *  <LI> _props.put("CIPHER_SUITES_3",
    *           "SSL_RSA_EXPORT_WITH_RC2_CBC_40_MD5");
    *  <LI> _props.put("CIPHER_SUITES_4",
    *           "SSL_DH_RSA_EXPORT_WITH_DES40_CBC_SHA");
    *  <LI> _conn = _driver.getConnection(url, _props);
    * </UL>
    * 
    * @param host - server host name
    * @param port - server port number
    * @param props - connection properties
    * @return Socket - SSLSocket instance
    * @exception IOException
    * @exception UnknownHostException
    * @see sample2.EncryptASE
    * @see com.sybase.jdbcx.SybSocketFactory#createSocket
    */
    public Socket createSocket(String host, int port, 
        Properties props)
        throws IOException, UnknownHostException
    {

        // check to see if cipher suites are set in the connection
        // properties
        Vector cipherSuites = new Vector();
        String cipherSuiteVal = null;
        int cipherIndex = 1;

        // Loop through possible multiple cipher suites
        do
        {
            if((cipherSuiteVal = props.getProperty("CIPHER_SUITES_"
            + cipherIndex++)) == null)
            {
                if(cipherIndex <= 2)
                {
                    // No cipher suites available
                    // return what the object considers its default
                    // SSLSocket, with cipher suites enabled.
                    return createSocket(host, port);
                }
                else
                {
                    // we have at least one cipher suite to enable 
                    // per request on the connection
                    break;
                }
            }
            else 
            {
                // add to the cipher suite Vector, so that
                // we may enable them together
                cipherSuites.addElement(cipherSuiteVal);
            }
        }
        while(true);

        // let's create a String[] out of the created vector
        String enableThese[] = new String[cipherSuites.size()];
        cipherSuites.copyInto(enableThese);

        // enable the cipher suites
        setSystemProperties();
        SSLSocket s = 
            (SSLSocket) SSLSocketFactory.getDefault().createSocket(host, port);
        s.setEnabledCipherSuites(enableThese);
        setProtocol(s);

        // Setup complete. Let's do the handshake.
        s.startHandshake();
        // return the SSLSocket
        return s;
    }

    /** 
     * Before creating an SSL connection to ASE, there are some
     * administrative tasks that the user must complete. 
     * <UL>
     * <LI>Using the certauth and certreq tools provided with ASE, the
     * user should create a private and public key, as well as a
     * trusted root certificate for the ASE server
     * (see the ASE documentation for information on using
     * the certauth and certreq utilities).
     * </LI>
     * <LI>The user should use the keytool utility provided with the JDK1.4
     * installation to import the trusted root certificate created above
     * into a keystore. See Suns's documentation for JSSE (Java Secure Socket
     * Extension) for more information. At the time of this sample's creation,
     * that documentation was at
     * http://java.sun.com/j2se/1.4/docs/guide/security/jsse/JSSERefGuide.html
     * </LI>
     * </UL>
     * <P><B>Note:</B> When writing application code, customers will want to
     * configure their VM installation so that they will have
     * no need to set the javax.net.ssl.trustStore System property. This
     * configuration involves placing or using keystore files
     * in defined locations within the Java installation.
     * <P>Further information on this is available in the java documentation
     * referenced above.
     * <P>For purposes of this sample, customers will want to alter the
     * trustStoreLocation variable prior to trying this sample. 
     * The location should point to the user's trust store file.
     * If a customer wishes to place the trust store file in the
     * defined location within the Java installation, the setSystemProperties
     * method can be altered so that it simply does nothing.
     * <P>Finally, do not be misled by the fact that the system property
     * we are setting is named 'trustStore', while the file itself is
     * called .keystore. See the JSSE documentation referenced above for
     * clarification.
    **/
    protected void setSystemProperties()
    {
        String trustStoreLocation = "/foo/bar/.keystore";
        System.setProperty
            ("javax.net.ssl.trustStore", trustStoreLocation); 
    }

    /**
     * At the time of this sample's writing, ASE accepted only TLS1.0 SSL
     * connections. The socket must be set up to use only the TLS protocol;
     * otherwise, the socket will try to connect using a variety of protocols,
     * causing the connection attempt to fail.
     */
    protected void setProtocol(SSLSocket s)
    {
        String [] enableTheseProtocols =
        {
            "TLSv1"
        }
        ;
        s.setEnabledProtocols (enableTheseProtocols);
    }

    // javax.net.SSLSocketFactory contains several other methods
    // that need to be implemented because they are abstract.
    // They are not necessary for this sample, so we shall
    // simply stub them.

    /**
     *  Not implemented for this sample
     *  @return null
     */
    public java.lang.String[] getDefaultCipherSuites()
    {
        return null;
    }
    /**
     *  Not implemented for this sample
     *  @return null
     */
    public java.lang.String[] getSupportedCipherSuites()
    {
        return null;
    }
    /**
     *  Not implemented for this sample
     *  @return null
     */
    public java.net.Socket createSocket(java.lang.String host, int port,
        java.net.InetAddress clientAddress, int clientPort)
    {
        return null;
    }
    /**
     *  Not implemented for this sample
     *  @return null
     */
    public java.net.Socket createSocket(java.net.InetAddress host, int port)
    {
        return null;
    }
    /**
     *  Not implemented for this sample
     *  @return null
     */
    public java.net.Socket createSocket(java.net.InetAddress host, int
        port, java.net.InetAddress clientAddress, int clientPort)
    {
        return null;
    }
    /**
     *  Not implemented for this sample
     *  @return null
     */
    public java.net.Socket createSocket(java.net.Socket s, String host, 
        int port, boolean autoClose)
    {
        return null;
    }
}
