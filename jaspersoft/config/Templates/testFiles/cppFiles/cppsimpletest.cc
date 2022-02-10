/* 
 * File:   %<%NAME%>%.%<%EXTENSION%>%
 * Author: %<%USER%>%
 *
 * Created on %<%DATE%>%, %<%TIME%>%
 */

#include <stdlib.h>
#include <iostream>

/*
 * Simple C++ Test Suite
 */

void test1() {
    std::cout << "%<%NAME%>% test 1" << std::endl;
}

void test2() {
    std::cout << "%<%NAME%>% test 2" << std::endl;
    std::cout << "%TEST_FAILED% time=0 testname=test2 (%<%NAME%>%) message=error message sample" << std::endl;
}

int main(int argc, char** argv) {
    std::cout << "%SUITE_STARTING% %<%NAME%>%" << std::endl;
    std::cout << "%SUITE_STARTED%" << std::endl;

    std::cout << "%TEST_STARTED% test1 (%<%NAME%>%)" << std::endl;
    test1();
    std::cout << "%TEST_FINISHED% time=0 test1 (%<%NAME%>%)" << std::endl;

    std::cout << "%TEST_STARTED% test2 (%<%NAME%>%)\n" << std::endl;
    test2();
    std::cout << "%TEST_FINISHED% time=0 test2 (%<%NAME%>%)" << std::endl;

    std::cout << "%SUITE_FINISHED% time=0" << std::endl;

    return (EXIT_SUCCESS);
}

