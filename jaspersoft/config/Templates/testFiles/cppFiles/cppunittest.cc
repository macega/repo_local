/* 
 * File:   %<%NAME%>%.%<%EXTENSION%>%
 * Author: %<%USER%>%
 * 
 * Created on %<%DATE%>%, %<%TIME%>%
 */

#include %<%QUOTES%>%%<%NAME%>%.%<%DEFAULT_HEADER_EXT%>%%<%QUOTES%>%

CPPUNIT_TEST_SUITE_REGISTRATION(%<%CLASSNAME%>%);

%<%CLASSNAME%>%::%<%CLASSNAME%>%() {
}

%<%CLASSNAME%>%::~%<%CLASSNAME%>%() {
}

void %<%CLASSNAME%>%::setUp() {
    this->example = new int(1);
}

void %<%CLASSNAME%>%::tearDown() {
    delete this->example;
}

void %<%CLASSNAME%>%::testMethod() {
    CPPUNIT_ASSERT(*example == 1);
}

void %<%CLASSNAME%>%::testFailedMethod() {
    CPPUNIT_ASSERT(++*example == 1);
}
