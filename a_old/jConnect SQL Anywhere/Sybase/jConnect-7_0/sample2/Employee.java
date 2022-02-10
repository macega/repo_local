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
public class Employee implements Person, java.io.Serializable
{
    private String _firstName;
    private String _lastName;
    private String _middleInitial;

    public Employee(String fn, String ln, String mi) 
    {
        super();
        _firstName = fn;
        _lastName = ln;
        _middleInitial = mi;
    }

    public void setFirstName(String fn) 
    {
        _firstName = fn;
    }

    public String getFirstName() 
    {
        return _firstName;
    }

    public void setLastName(String ln) 
    {
        _lastName = ln;
    }

    public String getLastName() 
    {
        return _lastName;
    }

    public void setMiddleInitial(String mi) 
    {
        _middleInitial = mi;
    }

    public String getMiddleInitial() 
    {
        return _middleInitial;
    }

    public String toString() 
    {
        return getFirstName() + " " + getMiddleInitial() + " " + getLastName();
    }
}
