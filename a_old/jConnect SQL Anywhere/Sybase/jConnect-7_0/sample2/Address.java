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
/**
 * The Address class is used by the HandleObject sample, which shows
 * how to insert, update and delete data stored as a Java Object in a Sybase
 * Database.
 *
 * The HandleObject sample requires that this class and the AddressSubclass
 * class be put together into a jar and installed on the targeted server.
 * Use the following instructions to install the jar file:
 *
 * 1) Build the samples using the Makefile (for Unix) or make_nt.bat (NT).
 * 2) Place the sample2/Address.class and sample2/AddressSubclass.class files
 *    into a jar file:
 *    a) go to the $JDBC_HOME/classes directory
 *    b) run the jar command:
 *     jar cvf0 Address.jar sample2/Address.class sample2/AddressSubclass.class
 * 3) Install it onto the server
 *    a) for ASE (SQL Server), use:
 *       $SYBASE/bin/installjava -f <dir>/Address.jar -new -U sa -P passwd
 *    b) for ASA (Anywhere):
 *       install java new jar from file '<dir>/Address.jar'
 *
 * When running HandleObject sample, add the 'Address.jar' to the classpath.
 *
 */
public class Address implements java.io.Serializable 
{
    public String _street;
    public String _zip;

    // A default constructor
    public Address ( ) 
    {
        _street = "Unknown";
        _zip = "None";
    }

    // A constructor with parameters
    public Address (String s, String z) 
    {
        _street = s;
        _zip = z;
    }

    // A method to return a display representation of the full address
    public String display( ) 
    {
        return "Street= " + _street + " ZIP= " + _zip;
    }
}
