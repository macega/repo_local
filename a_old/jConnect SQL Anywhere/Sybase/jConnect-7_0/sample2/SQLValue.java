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
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.StringBufferInputStream;
import java.math.BigDecimal;

/**
 * 
 * The purpose of this class is to provide a means of
 * storing column values of a result set, and to hide 
 * the data conversion routines from the perspective
 * of the ResultSet interface.<p> 
 *   
 * Note that all of the "set" methods are private while
 * all the "get" methods are public.  This class only allows
 * read-only objects; they are assigned by the constructor
 * and copies can be returned in different types, but the
 * original type is fixed.<p>
 *
 * This class has four data members: one to store the SQL type
 * of the value, one to hold a series of flags describing the
 * type in more detail, and two to hold the object value.  If
 * the object can be stored as an int without any loss of
 * precision, it is, otherwise it is stored as a java.lang.Object.
 * This allows for efficient processing of integral types.<p>
 *
 */
public class SQLValue
{

    /**
	 * The value is an integral value that fits in an int,
	 * and so its value is stored in the _intValue field.
	 */
    protected static final int IS_INT      = 0x0001;

    /** The value is a boolean. */
    protected static final int IS_BOOL     = 0x0002;

    /** The value is a string. */
    protected static final int IS_STRING   = 0x0004;

    /** The value is a real. */
    protected static final int IS_REAL     = 0x0008;

    /** The value is binary. */
    protected static final int IS_BINARY   = 0x0010;

    /** The value is a date, time or timestamp. */
    protected static final int IS_DATETIME = 0x0020;

    /** The value is a long integer. */
    protected static final int IS_LONG     = 0x0040;

    /** The value is a BigDecimal. */
    protected static final int IS_BIGNUM   = 0x0080;

    /** The value is an unknown type. */
    protected static final int IS_UNKNOWN  = 0x0100;

    /** The value is a NULL value. */
    protected static final int IS_NULL     = 0x8000;

    /** The SQL type of the value. */
    protected int    _type;

    /** Flags that further identify the type of the value. */
    protected int    _flags;

    /** The data value if it fits in an int. */
    protected int    _intValue;

    /** The data value. */
    protected Object _value;

    /** Constructs a null constant data value. */
    public SQLValue()
    {
        setNull(Types.OTHER);
    }

    /**
	 * Constructs a constant data value.
	 *
	 * @param o       An object.  If non-null sets it to this value.
	 * @param sqlType The type of the object.  Ignored unless o is
	 *                null.
	 */
    public SQLValue(Object o, int sqlType)
    {
        if (o != null)
        {
            setObject(o);
        }
        else 
        {
            setNull(sqlType);
        }
    }

    /**
	 * Constructs a string constant data value.
	 * @param s The string value.
	 */
    public SQLValue(String s)
    {
        setString(s);
    }

    /**
	 * Constructs an integer constant data value.
	 * @param i The integer value.
	 */
    public SQLValue(int i)
    {
        setInt(i);
    }

    /**
	 * Constructs a boolean constant data value.
	 * @param b The boolean value.
	 */
    public SQLValue(boolean b)
    {
        setBoolean(b);
    }

    /**
	 * Constructs a byte constant data value.
	 * @param b The byte value.
	 */
    public SQLValue(byte b)
    {
        setByte(b);
    }

    /**
	 * Constructs a short constant data value.
	 * @param s The short value.
	 */
    public SQLValue(short s)
    {
        setShort(s);
    }

    /**
	 * Constructs a long constant data value.
	 * @param l The long value.
	 */
    public SQLValue(long l)
    {
        setLong(l);
    }

    /**
	 * Constructs a float constant data value.
	 * @param f The float value.
	 */
    public SQLValue(float f)
    {
        setFloat(f);
    }

    /**
	 * Constructs a double constant data value.
	 * @param d The double value.
	 */
    public SQLValue(double d)
    {
        setDouble(d);
    }

    /**
	 * Constructs a constant data value from an arbitrary object.
	 * @param o The object from which to construct a constant data value.
	 */
    public SQLValue(Object o)
    {
        setObject(o);
    }

    /**
	 * Constructs a byte array constant data value.  The array is copied.
	 * @param bytes The byte array.
	 */
    public SQLValue(byte[] bytes)
    {
        setBytes(bytes);
    }

    /**
	 * Constructs a BigDecimal constant data value.
	 * @param b The big decimal value.
	 */
    public SQLValue(BigDecimal  b)
    {
        setObject(b);
    }

    /**
	 * Constructs a Date constant data value.
	 * @param d The date value.
	 */
    public SQLValue(Date d)
    {
        setObject(d);
    }

    /**
	 * Constructs a Time constant data value.
	 * @param t The time value.
	 */
    public SQLValue(Time t)
    {
        setObject(t);
    }

    /**
	 * Constructs a Timestamp constant data value.
	 * @param t The timestamp value.
	 */
    public SQLValue(Timestamp  t)
    {
        setObject(t);
    }

    /**
	 * Retrieves a null constant data value with undefined type (Types.OTHER).
	 * @return The null constant data value.
	 */
    public static SQLValue getNullValue()
    {
        return new SQLValue();
    }

    /**
	 * Retrieves a null constant data value of a specific type.
	 * @return A null constant data value.
	 */
    public static synchronized SQLValue getNullValue(int type)
    {
        return new SQLValue(null, type);
    }

    /**
	  * Retrieves the original type for this data value.  The type
	  * is one of the constants on the java.sql.Types interface.
	  *
	  * @return The type that describes this data value.
	  */
    public final int getType()
    {
        return _type;
    }

    /**
     * Retrieves the value as a boolean, converting as necessary.
     *
     * @return The value as a boolean.
     * @exception SQLException If the data could not be converted.
     */
    public boolean getBoolean() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0))
        {
            return false;
        }

        if (! (((_flags)&(IS_BIGNUM|IS_INT|IS_LONG|IS_REAL|IS_STRING))!=0))
        {
            throwConversionException("boolean");
        }

        if ((_flags & IS_INT) != 0)
        {
            if (_intValue == 0) return false;
            else if (_intValue == 1) return true;
            else throwConversionException("boolean");
        }

        // Special case for "true" and "false"

        if ((((_flags)&IS_STRING)!=0))
        {
            String s = (String) _value;

            if (s.equalsIgnoreCase("true"))
            {
                _intValue  = 1;
                _flags    |= IS_INT | IS_BOOL;
                return true;
            }
            else if (s.equalsIgnoreCase("false"))
            {
                _intValue  = 0;
                _flags    |= IS_INT | IS_BOOL;
                return false;
            }
        }

        if (isIntValue(0, 1, "boolean"))
        {
            _flags |= IS_BOOL;
            return(_intValue == 1);
        }

        if (_value != null) throwConversionException("boolean");
        return false;
    }

    /**
     * Retrieves the value as a byte, converting as necessary.
     *
     * @return The value as a byte.
     * @exception SQLException If the data could not be converted.
     */
    public byte getByte() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return 0;

        if (! (((_flags)&(IS_BIGNUM|IS_INT|IS_LONG|IS_REAL|IS_STRING))!=0))
        {
            throwConversionException("byte");
        }

        if (isIntValue(-128, 127, "byte"))
        {
            return (byte) _intValue;
        }

        if (_value != null) throwConversionException("byte");
        return 0;
    }


    /**
     * Retrieves the value as a byte array, converting as necessary.
     *
     * @return The value as a byte array.
     * @exception SQLException If the data could not be converted.
     */
    public byte[] getBytes() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return null;

        if ((((_flags)&IS_STRING)!=0)  && _value != null)
        {
            return ((String) _value).getBytes();
        }
        else if (! (((_flags)&IS_BINARY)!=0))
        {
            throwConversionException("byte[]");
        }

        try 
        {
            return (byte[]) _value;
        }
        catch(Exception e)
        {
        }

        if (_value != null) throwConversionException("byte[]");
        return null;
    }

    /**
     * Retrieves the value as a double, converting as necessary.
     *
     * @return The value as a double.
     * @exception SQLException If the data could not be converted.
     */
    public double getDouble() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return 0;

        if (! (((_flags)&(IS_INT|IS_LONG|IS_REAL|IS_STRING))!=0))
        {
            throwConversionException("double");
        }

        if ((_value != null) && (_flags & IS_BIGNUM) != 0)
        {
            return ((BigDecimal) _value).doubleValue();
        }

        if (_value != null &&  (((_flags)&IS_REAL)!=0))
        {
            try 
            {
                if (_type == Types.REAL)
                {
                    return ((Float) _value).doubleValue();
                }
                else 
                {
                    return ((Double) _value).doubleValue();
                }
            }
            catch(Exception e)
            {
            }
        }
        else if ((_flags & IS_INT) != 0)
        {
            return (double) _intValue;
        }

        try 
        {
            return Double.valueOf(_value.toString()).doubleValue();
        }
        catch(Exception e)
        {
        }

        if (_value != null) throwConversionException("double");
        return 0;
    }

    /**
     * Retrieves the value as an integer, converting as necessary.
     *
     * @return The value as an integer.
     * @exception SQLException If the data could not be
     *                                    converted.
     */
    public int getInt() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return 0;

        if (! (((_flags)&(IS_BIGNUM|IS_INT|IS_LONG|IS_REAL|IS_STRING))!=0)) 
        {
            throwConversionException("int");
        }

        if ((_flags & IS_INT) != 0 ||
        isIntValue(Integer.MIN_VALUE, Integer.MAX_VALUE, "int"))
        {
            return _intValue;
        }

        if (_value != null) throwConversionException("int");
        return 0;
    }

    /**
     * Retrieves the value as a float, converting as necessary.
     *
     * @return The value as a float.
     * @exception SQLException If the data could not be converted.
     */
    public float getFloat() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return 0;

        if ((_value != null) && (_flags & IS_BIGNUM) != 0)
        {
            return ((BigDecimal) _value).floatValue();
        }

        if (! (((_flags)&(IS_INT|IS_LONG|IS_REAL|IS_STRING))!=0))
        {
            throwConversionException("float");
        }

        if (_value != null &&  (((_flags)&IS_REAL)!=0))
        {
            try 
            {
                if (_type == Types.REAL)
                {
                    return ((Float) _value).floatValue();
                }
                else 
                {
                    return ((Double) _value).floatValue();
                }
            }
            catch(Exception e)
            {
            }
        }
        else if ((((_flags)&IS_INT)!=0))
        {
            return (float) _intValue;
        }

        try 
        {
            return Float.valueOf(_value.toString()).floatValue();
        }
        catch(Exception e)
        {
        }

        if (_value != null) throwConversionException("float");
        return 0;
    }

    /**
     * Retrieves the value as a long, converting as necessary.
     *
     * @return The value as a long.
     * @exception SQLException If the data could not be converted.
     */
    public long getLong() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return 0;

        if (! (((_flags)&(IS_INT|IS_LONG|IS_REAL|IS_STRING))!=0)) 
        {
            throwConversionException("long");
        }

        if ((_flags & IS_INT) != 0)
        {
            return (long) _intValue;
        }

        if (_value == null) return 0;

        if ((_flags & IS_BIGNUM) != 0)
        {
            return ((BigDecimal) _value).longValue();
        }

        try 
        {
            if ((((_flags)&IS_REAL)!=0))
            {
                if (_type == Types.REAL)
                {
                    return ((Float) _value).longValue();
                }
                else 
                {
                    return ((Double) _value).longValue();
                }
            }
            else if (_value instanceof Long)
            {
                return ((Long) _value).longValue();
            }
            else if (_value instanceof Boolean)
            {
                return ((Boolean) _value).booleanValue() ? 1 : 0;
            }
            else 
            {
                return Long.parseLong(_value.toString());
            }
        }
        catch(Exception e)
        {
            throwConversionException("long");
        }
        return 0;
    }

    /**
     * Determines whether the value is a NULL value.
     * Note that a NULL value still has a SQL type 
     * associated with it.
     *
     * @return <code>true</code> if the value is NULL.
     */
    public boolean isNull()
    {
        return(_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)  ||
            (_value == null && ! (((_flags)&IS_INT)!=0)));
    }

    /**
     * Retrieves the value as an Object, converting as necessary.
     *
     * @return The value as an Object.
     * @exception SQLException If the data could not be converted.
     */
    public Object getObject() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return null;

        if (_value == null)
        {
            if ((((_flags)&IS_INT)!=0))
            {
                switch(_type)
                {
                    case Types.BIT:
                        return new Boolean(getBoolean());
                    case Types.TINYINT:
                        return new Byte(getByte());
                    case Types.SMALLINT:
                        return new Short(getShort());
                    case Types.INTEGER:
                        return new Integer(getInt());
                    case Types.BIGINT:
                        return new Long(getLong());
                    default:
                        throwConversionException("Object"); 
                        return null;
                }
            }
        }
        return _value;
    }

    /**
     * Retrieves the value as a short, converting as necessary.
     *
     * @return The value as a short.
     * @exception SQLException If the data could not be
     *                                    converted.
     */
    public short getShort() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return 0;

        if (! (((_flags)&(IS_BIGNUM|IS_INT|IS_LONG|IS_REAL|IS_STRING))!=0))
        {
            throwConversionException("short");
        }

        if (isIntValue(-32768, 32767, "short"))
        {
            return (byte) _intValue;
        }

        if (_value != null) throwConversionException("short");
        return 0;
    }

    /**
     * Retrieves the value as a string, converting as necessary.
     *
     * @return The value as a string.
     * @exception SQLException If the data could not be
     *                                    converted.
     */
    public String getString() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return null;

        if (_type == Types.CHAR || _type == Types.VARCHAR)
        {
            return (String) _value;
        }
        else if (_type == Types.BINARY || _type == Types.VARBINARY)
        {
            try 
            {
                return new String((byte[]) _value);
            }
            catch(Exception e)
            {
                throwConversionException("string");
                return null;
            }
        }
        // Otherwise convert...

        if ((_flags & IS_INT) != 0)
        {
            return String.valueOf(_intValue);
        }

        return(_value != null ? _value.toString() : null);
    }


    /**
     * Retrieves the constant value as an InputStream.
	  * This method will only succeed if the data value is a
	  * byte[], all other types will throw an SQLException.
     *
     * @return The underlying byte array as an InputStream.
	  * @exception If a data conversion error occurred.
     */
    public InputStream getBinaryStream() throws SQLException
    {
        if ((_type == Types.NULL) || (_flags == IS_NULL))
        {
            return null;
        }
        else if ((_type == Types.VARBINARY) && (_flags == IS_BINARY))
        {
            try
            {
                return new ByteArrayInputStream((byte[]) _value);
            }
            catch (Exception e)
            {
                throwConversionException("InputStream (Binary)");
            }
        }
        throwConversionException("InputStream (Binary)");
        return null;
    }

    /**
     * Retrieves the constant value as an InputStream.
	  * This method will only succeed if the data value is a
	  * String, all other types will throw an SQLException.
     *
     * @return The underlying String value as an InputStream.
	  * @exception If a data conversion error occurred.
     */
    public InputStream getAsciiStream() throws SQLException
    {

        if ((_type == Types.NULL) || (_flags == IS_NULL))
        {
            return null;	
        }
        else if ((_type == Types.VARCHAR) && (_flags == IS_STRING))
        {
            try
            {
                // return new StringBufferInputStream((String) _value);
                return new ByteArrayInputStream( ((String)_value).getBytes() );
            }
            catch (Exception e)
            {
                throwConversionException("InputStream (ASCII)");
            }
        }
        throwConversionException("InputStream (ASCII)");
        return null;
    }


    /**
     * Retrieves the constant value as a BigDecimal, converting as necessary
     * without throwing an exception if the conversion fails.
     *
     * @param scale The scale of the BigDecimal.
     * @return The converted value as a BigDecimal.
	  * @exception If a data conversion error occurred.
     */
    public BigDecimal getBigDecimal(int scale) throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return null;

        BigDecimal num = null;

        try 
        {
            if (_value != null)
            {
                if (_type == Types.CHAR || _type == Types.VARCHAR
                || _type == Types.LONGVARCHAR)
                {
                    num = new BigDecimal((String) _value);
                }
                else if ((_flags & IS_BIGNUM) != 0)
                {
                    num = (BigDecimal) _value;
                }
                else if (_type == Types.REAL)
                {
                    Float numF = (Float) _value;
                    num = new BigDecimal(numF.doubleValue());
                }
                else if (_type == Types.DOUBLE)
                {
                    Double numD = (Double) _value;
                    num = new BigDecimal(numD.doubleValue());
                }
            }

            if (num == null && ((_flags & IS_INT) != 0 ||
            isIntValue(Integer.MIN_VALUE, Integer.MAX_VALUE, "BigDecimal")))
            {
                num = new BigDecimal (_intValue);
            }
        }
        catch (NumberFormatException e)
        {
            throwConversionException("BigDecimal");
        }

        // Convert to appropriate scale...

        if (num != null)
        {
            try 
            {
                if (num.scale() == scale)
                {
                    return num;
                }
                else 
                {
                    return num.setScale(scale);
                }
            }
            catch (Exception e)
            {
                throwConversionException("BigDecimal");
            }
        }

        if (_value != null) throwConversionException("BigDecimal");
        return null;
    }


    /**
     * Retrieves the constant value as a Date, converting as necessary.
     *
     * @return The converted value as an Object. You need to cast this
     *  to a Date (this avoids dependence on the JDBC package).
     * @exception DataConversionException If the data could not be
     *  converted.
     */
    public Date getDate() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return null;

        try 
        {
            if (_value != null)
            {
                if (_type == Types.DATE)
                {
                    return (Date) _value;
                }
                else if (_type == Types.CHAR || _type == Types.VARCHAR ||
                _type == Types.LONGVARCHAR)
                {
                    String str = (String) _value;
                    try 
                    {
                        return Date.valueOf(str);
                    }
                    catch (IllegalArgumentException e)
                    {
                        try 
                        {
                            return new Date(Timestamp.valueOf(str).getTime());
                        }
                        catch (IllegalArgumentException e1)
                        {
                            return new Date(Time.valueOf(str).getTime());
                        }
                    }
                }
                else if (_type == Types.TIMESTAMP)
                {
                    return new Date(((Timestamp) _value).getTime());
                }
                else if (_type == Types.TIME)
                {
                    return new Date(((Time) _value).getTime());
                }
                else 
                {
                    throwConversionException("Date");
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            throwConversionException("Date");
        }

        if (_value != null) throwConversionException("Date");
        return null;
    }


    /**
     * Retrieves the constant value as a Time, converting as necessary.
     *
     * @return The converted value as an Object. You need to cast this
     *  to a Time (this avoids dependence on the JDBC package).
     * @exception DataConversionException If the data could not be
     *  converted.
     */
    public Time getTime() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return null;

        try 
        {
            if (_value != null)
            {
                if (_type == Types.TIME)
                {
                    return (Time) _value;
                }
                else if (_type == Types.CHAR || _type == Types.VARCHAR ||
                _type == Types.LONGVARCHAR)
                {
                    String str = (String) _value;
                    try 
                    {
                        return Time.valueOf(str);
                    }
                    catch (IllegalArgumentException e)
                    {
                        try 
                        {
                            return new Time(Timestamp.valueOf(str).getTime());
                        }
                        catch (IllegalArgumentException e1)
                        {
                            return new Time(Date.valueOf(str).getTime());
                        }
                    }
                }
                else if (_type == Types.TIMESTAMP)
                {
                    return new Time(((Timestamp) _value).getTime());
                }
                else if (_type == Types.DATE)
                {
                    return new Time(((Date) _value).getTime());
                }
                else 
                {
                    throwConversionException("Time");
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            throwConversionException("Time");
        }

        if (_value != null) throwConversionException("Time");
        return null;
    }



    /**
     * Retrieves the constant value as a Timestamp, converting as necessary.
     *
     * @return The converted value as an Object. You need to cast this 
     *  to a Timestamp (this avoids dependence on the JDBC package).
     * @exception SQLException If the data could not be converted.
     */
    public Timestamp getTimestamp() throws SQLException
    {
        if (_type == Types.NULL ||  (((_flags)&IS_NULL)!=0)) return null;

        try 
        {
            if (_value != null)
            {
                if (_type == Types.TIMESTAMP)
                {
                    return (Timestamp) _value;
                }
                else if (_type == Types.CHAR || _type == Types.VARCHAR
                || _type == Types.LONGVARCHAR)
                {
                    String str = (String) _value;
                    try 
                    {
                        return Timestamp.valueOf(str);
                    }
                    catch (IllegalArgumentException e)
                    {
                        try 
                        {
                            return new Timestamp(Date.valueOf(str).getTime());
                        }
                        catch (IllegalArgumentException e1)
                        {
                            return new Timestamp(Time.valueOf(str).getTime());
                        }
                    }
                }
                else if (_type == Types.DATE)
                {
                    return new Timestamp(((Date) _value).getTime());
                }
                else if (_type == Types.TIME)
                {
                    return new Timestamp(((Time) _value).getTime());
                }
                else 
                {
                    throwConversionException("Timestamp");
                }
            }
        }
        catch (IllegalArgumentException e)
        {
            throwConversionException("Timestamp");
        }

        if (_value != null) throwConversionException("Timestamp");
        return null;
    }

    //
    // Private methods 
    //

    /**
     * Specifies the value as a boolean.
     *
     * @param b The new value as a boolean.
     */
    private void setBoolean(boolean b)
    {
        _type      = Types.BIT;
        _flags     = IS_INT | IS_BOOL;
        _intValue  = b ? 1 : 0;
        _value     = null;
    }

    /**
     * Specifies the value as a byte.
     *
     * @param b The new value as a byte.
     */
    private void setByte(byte b)
    {
        _type      = Types.TINYINT;
        _flags     = IS_INT;
        _intValue  = (int) b;
        _value     = null;
    }

    /**
     * Specifies the value as a copy of the byte array.
     *
     * @param data The new value as a byte array.
     */
    private void setBytes(byte[] data) 
    {
        byte[] ndata = new byte[ data.length ];

        System.arraycopy(data, 0, ndata, 0, data.length);
        data = ndata;        

        _type     = Types.VARBINARY;
        _flags    = IS_BINARY;
        _intValue = 0;
        _value    = data;
    }

    /**
     * Specifies the value as a double.
     *
     * @param d The new value as a double.
     */
    private void setDouble(double d)
    {
        _type      = Types.DOUBLE;
        _flags     = IS_REAL;
        _intValue  = 0;
        _value     = new Double(d);
    }

    /**
     * Specifies the value as a float.
     *
     * @param f The new value as a float.
     */
    private void setFloat(float f) 
    {
        _type      = Types.REAL;
        _flags     = IS_REAL;
        _intValue  = 0;
        _value     = new Float(f);
    }

    /**
     * Specifies the value as an integer.
     *
     * @param i The new value as an integer.
     */
    private void setInt(int i)
    {
        _type      = Types.INTEGER;
        _flags     = IS_INT;
        _intValue  = i;
        _value     = null;
    }

    /**
     * Specifies the value as a long.
     * 
     * @param l The new value as a long.
     */
    private void setLong(long l)
    {
        _type     = Types.BIGINT;
        _flags    = IS_LONG;
        _intValue = 0;
        _value    = new Long(l);
    }

    /**
     * Specifies the value as a SQL NULL with a specific type.
     *
     * @param sqlType The type of the NULL value.
     */
    private void setNull(int sqlType)
    {
        _type     = sqlType;
        _flags    = IS_NULL;
        _intValue = 0;
        _value    = null;
    }

    /**
     * Specifies the value as an Object.
     *
     * @param o The object that is to be made the new value.
     */
    private void setObject(Object o)
    {
        _value    = o;
        _flags    = 0;
        _intValue = 0;

        if (_value == null)
        {
            _type  = Types.NULL;
            _flags = IS_NULL;
            return;
        }

        if (_value instanceof String)
        {
            _type  = Types.VARCHAR;
            _flags = IS_STRING;
        }
        else if (_value instanceof Boolean)
        {
            _type     = Types.BIT;
            _flags    = IS_INT | IS_BOOL;
            _intValue = (((Boolean) _value).booleanValue() ? 1 : 0);
        }
        else if (_value instanceof Short)
        {
            _type     = Types.SMALLINT;
            _flags    = IS_INT;
            _intValue = ((Short) _value).intValue();
        }
        else if (_value instanceof Byte)
        {
            _type     = Types.TINYINT;
            _flags    = IS_INT;
            _intValue = ((Byte) _value).intValue();

        }
        else if (_value instanceof Integer)
        {
            _type     = Types.INTEGER;
            _flags    = IS_INT;
            _intValue = ((Integer) _value).intValue();
        }
        else if (_value instanceof Long)
        {
            _type     = Types.BIGINT;
            _flags    = IS_LONG;
        }
        else if (_value instanceof Float)
        {
            _type     = Types.REAL;
            _flags    = IS_REAL;
        }
        else if (_value instanceof Double)
        {
            _type     = Types.DOUBLE;
            _flags    = IS_REAL;
        }
        else if (_value instanceof byte[])
        {
            _type     = Types.VARBINARY;
            _flags    = IS_BINARY;
        }
        else if (_value instanceof  BigDecimal)
        {
            _type  = Types.NUMERIC;
            _flags = IS_BIGNUM;
        }
        else if (_value instanceof Date)
        {
            _type  = Types.DATE;
            _flags = IS_DATETIME;
        }
        else if (_value instanceof Time)
        {
            _type  = Types.TIME;
            _flags = IS_DATETIME;
        }
        else if (_value instanceof Timestamp)
        {
            _type  = Types.TIMESTAMP;
            _flags = IS_DATETIME;
        }
        else 
        {
            _type     = Types.OTHER;
            _flags    = IS_UNKNOWN;
        }
    }

    /**
     * Specifies the value as a short.
     *
     * @param s The new value as a short.
     */
    private void setShort(short i)
    {
        _type      = Types.SMALLINT;
        _flags     = IS_INT;
        _intValue  = (int) i;
        _value     = null;
    }

    /**
     * Specifies the value as a string.
     *
     * @param s The new value as a string.
     */
    private void setString(String s)
    {
        _type   = Types.VARCHAR;
        _flags  = IS_STRING;
        _value  = s;
    }

    /**
     * Checks to see if the value is an integer in the specified range. 
     * Optionally throws an exception if the integer is not within the range.<p>
     *
     * @param min The minimum value of the range.
     * @param max The maximum value of the range.
     * @param throwIt If <code>true</code>, an exception is thrown if the 
     *  value is not in the specified range.
     *
     * @return <code>true</code> if the value is in the specified range;
     *  otherwise returns false.
     *
     * @exception SQLException If the integer value is not
     *  in the given range and throwIt is true.
     */
    private boolean isIntValue(int min, int max, String targetType)
        throws SQLException
    {
        if ((_flags & IS_INT) == 0)
        {
            if (_value == null) return false;

            if (_value instanceof  BigDecimal)
            {
                _intValue  = ((BigDecimal) _value).intValue();
                _flags    |= IS_INT;
            }

            try 
            {
                if (_value instanceof Boolean)
                {
                    _intValue  = ((Boolean) _value).booleanValue() ? 1 : 0;
                    _flags    |= IS_INT | IS_BOOL;
                }
                else if ((((_flags)&IS_REAL)!=0))
                {
                    if (_value instanceof Float)
                    {
                        _intValue = ((Float) _value).intValue();
                    }
                    else 
                    {
                        _intValue = ((Double) _value).intValue();
                    }
                    _flags    |= IS_INT;
                }
                else 
                {
                    _intValue  = Integer.parseInt(_value.toString());
                    _flags    |= IS_INT;
                }
            }
            catch(Exception e)
            {
                throwConversionException(targetType);           
            }
        }

        if (_intValue >= min && _intValue <= max)
        {
            return true;
        }
        else 
        {
            throwConversionException(targetType);
        }

        return false;
    }

    /**
     * Constructs and throws a SQLException for the
     * given conversion type.
     *
     * @param targetType The kind of conversion being attempted.
     * @exception SQLException Always thrown.
     */
    private void throwConversionException(String targetType) throws SQLException
    {
        String msg = "conversion from " + getTypeName(_type);
        msg += " to " + targetType + " is not defined";

        throw new SQLException(msg);
    }

    /**
     * Retrieves the name of the given type.
     *
     * @param type A data value type.
     * @return A string containing the name of the type.
     */
    private String getTypeName(int type)
    {
        switch(type)
        {
            case Types.BIT:
                return "BIT";
            case Types.TINYINT:
                return "TINYINT";
            case Types.BIGINT:
                return "BIGINT";
            case Types.LONGVARBINARY:
                return "LONGVARBINARY";
            case Types.VARBINARY:
                return "VARBINARY";
            case Types.BINARY:
                return "BINARY";
            case Types.LONGVARCHAR:
                return "LONGVARCHAR";
            case Types.NULL:
                return "NULL";
            case Types.CHAR:
                return "CHAR";
            case Types.NUMERIC:
                return "NUMERIC";
            case Types.DECIMAL:
                return "DECIMAL";
            case Types.INTEGER:
                return "INTEGER";
            case Types.SMALLINT:
                return "SMALLINT";
            case Types.FLOAT:
                return "FLOAT";
            case Types.REAL:
                return "REAL";
            case Types.DOUBLE:
                return "DOUBLE";
            case Types.VARCHAR:
                return "VARCHAR";
            case Types.DATE:
                return "DATE";
            case Types.TIME:
                return "TIME";
            case Types.TIMESTAMP:
                return "TIMESTAMP";
        }
        return "OTHER";
    }


}
