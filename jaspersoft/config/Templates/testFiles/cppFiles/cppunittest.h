/* 
 * File:   %<%NAME%>%.%<%EXTENSION%>%
 * Author: %<%USER%>%
 *
 * Created on %<%DATE%>%, %<%TIME%>%
 */

#ifndef %<%GUARD_NAME%>%
#define	%<%GUARD_NAME%>%

#include <cppunit/extensions/HelperMacros.h>

class %<%CLASSNAME%>%  : public CPPUNIT_NS::TestFixture {
	CPPUNIT_TEST_SUITE(%<%CLASSNAME%>%);
	CPPUNIT_TEST(testMethod);
	CPPUNIT_TEST(testFailedMethod);
	CPPUNIT_TEST_SUITE_END();

public:
	%<%CLASSNAME%>%();
	virtual ~%<%CLASSNAME%>%();
	void setUp();
	void tearDown();

private:
	int *example;
	void testMethod();
	void testFailedMethod();
};

#endif	/* %<%GUARD_NAME%>% */

