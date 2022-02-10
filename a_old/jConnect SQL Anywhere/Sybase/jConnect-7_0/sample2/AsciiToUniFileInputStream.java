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
import java.sql.*;
import java.io.*;

/**
 *
 *  AsciiToUniFileInputStream class assists in reading a byte stream and
 *  converting it so that it appears to be a unicode stream
 *
 *
 * <P>A char/varchar/longvarchar column value can be retrieved as a
 * stream of Unicode characters. This class extends PadByteInputStream by
 * converting stream of SQL char data to a stream of unicode chacters.
 * 
 * <P>How Object is created:<br>
 * The object is created when user calls ResultSet.getUnicodeStream().
 * <P>Lifetime of the object:<br>
 * It may be passed to the user (from getFooStream()), and will not be
 * garbage collected until the user discards all references to it.
 * <P>What happens when object is destroyed:<br>
 * No special action is needed on cleanup.
 * <P> This class is used by the sample program UnicodeStream.java
 *
 * @see java.io.FilterInputStream
 * @see com.sybase.jdbcx.SybResultSet#getUnicodeStream
 * @see Sample
 * @see UnicodeStream
 */
public class AsciiToUniFileInputStream extends FilterInputStream
{

    protected int _padByteLengthRemaining;
    // Array holding two consecutive read() return values. Each read() returns
    // one of the slots in this array
    protected int _bytes[];
    // This boolean flag indicates which of the two slots in the above
    // byte[] should be returned from the read.
    protected boolean _even;

    // Constructors:
    /**
    * <P> Create a new AsciiToUniFileInputStream
    * @param stream the FileInputStream this is a wrapper on
    * @param length how many characters are in the stream
    */
    public AsciiToUniFileInputStream(FileInputStream stream, int length )
        throws IOException
    {

        super(stream);
        _bytes = new int[2];
        _padByteLengthRemaining = length * 2;
        _even = true;
    }

    /**
     * <P>Return the next byte of a Unicode character.
     * @exception IOException .
     */
    public int read() throws IOException
    {
        //* DONE
        if (_padByteLengthRemaining == 0)
        {
            return -1;
        }
        if (_even)
        {
            _bytes[1] = super.read();
            if (-1 == _bytes[1])
            {
                return -1;
            }
        }
        _padByteLengthRemaining--;
        _even = !_even;
        return _even ? _bytes[1] : _bytes[0];
    }


}
// end of class AsciiToUniFileInputStream
