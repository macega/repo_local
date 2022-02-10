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
import sun.io.*;

/**
* <P>This is an impure (but efficient) implementation of
* <CODE>CharsetConverter</CODE>
* which relies on the <CODE>sun.io</CODE> package, especially classes
* <CODE>ByteToCharConverter</CODE> and <CODE>CharToByteConverter</CODE>.
* It may be used with jConnect instead of the 100% Pure
* Java
* implementation by setting the <CODE>CHARSET_CONVERTER_CLASS</CODE>
* connection property to <CODE>sample2.SunIoConverter</CODE>.
* <P>
* <STRONG>NOTE:</STRONG> This class uses classes from the package
* <CODE>sun.io</CODE>, which may not be
* present in all Java VMs.  JavaSoft is free to change anything
* in the <CODE>sun.io</CODE> package in future releases.  If you decide to use
* this converter or any derivative of it in your application for
* performance reasons, you may need to
* make code changes
* in the future if <CODE>sun.io</CODE> changes.
* @sybshow
*/
public class SunIoConverter implements com.sybase.jdbcx.CharsetConverter
{

    // Members:
    private ByteToCharConverter _toUnicode;
    private CharToByteConverter _fromUnicode;
    // Constructor:
    public SunIoConverter() 
    {
        _toUnicode = ByteToCharConverter.getDefault();
        _fromUnicode = CharToByteConverter.getDefault();
    }

    //Methods:
    /**
    * @see com.sybase.jdbcx.CharsetConverter#setEncoding
    */
    public void setEncoding(String encoding) 
        throws UnsupportedEncodingException
    {
        _toUnicode = ByteToCharConverter.getConverter(encoding);
        _fromUnicode = CharToByteConverter.getConverter(encoding);
    }
    /**
    * @see com.sybase.jdbcx.CharsetConverter#fromUnicode
    */
    // NOTE: this method does not throw CharConversionException
    public byte[] fromUnicode(String fromString) throws CharConversionException
    {
        return(_fromUnicode.convertAll(fromString.toCharArray()));
    }

    /**
    * @see com.sybase.jdbcx.CharsetConverter#toUnicode
    */
    // NOTE: this method does not throw CharConversionException
    public String toUnicode(byte[] fromBytes) throws CharConversionException
    {
        return new String(_toUnicode.convertAll(fromBytes));
    }
}
