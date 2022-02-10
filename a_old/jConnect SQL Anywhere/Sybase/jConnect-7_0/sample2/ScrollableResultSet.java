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

/**
* <P>This interface is an extension of the JDBC 1.0 
* <CODE>java.sql.ResultSet</CODE> interface.  The purpose is to define
* the additional methods for scrollable behavior.  The methods are duplicates
* of the methods added to the JDBC 2.0 API for the <CODE>ResultSet</CODE>
* interface.  However, this interface is a subset of the JDBC 2.0 ResultSet.
* This interface does not provide update or delete methods.
* <P>
* The constants defined on this interface have the same semantics as those
* found in the JDBC 2.0 API for java.sql.ResultSet. No guarantee is made
* that ScrollableResultSet.FETCH_FORWARD == ResultSet.FETCHFORWARD.
*
* @see java.sql.ResultSet
*/
public interface ScrollableResultSet extends java.sql.ResultSet
{

}
