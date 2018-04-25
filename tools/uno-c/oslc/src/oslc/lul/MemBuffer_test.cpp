/*
 * MemBuffer_test.cpp
 *
 *  Created on: Jan 8, 2018
 *      Author: admin
 */

#include "MemBuffer.h"

#include <iostream>
#include <istream>
#include <streambuf>
#include <string>

#include <oslc/lul/TestHarness.h>


namespace oslc {
namespace lul {

//REGISTER_TEST("Base Tests","XXXX")
//{
//	//std::abort();
//	//throw 101;
//
//   int x = 0;
//
//   //x = 3/x;;
//
//   TEST_EXP(5>100);
//   return 0;
//
//}

REGISTER_TEST("MemBuffer","test 1")
{
    char buffer[] = "I'm a buffer with embedded nulls\0and line\n feeds";

    MemBuffer sbuf(buffer, buffer + sizeof(buffer));
    std::istream in(&sbuf);
    std::string line;
    while (std::getline(in, line)) {
        std::cout << "line: " << line << "\n";
    }

    std::ostream out(&sbuf);
    int iVal = 12345;
    out<<iVal;

    std::iostream inOut(&sbuf);
    inOut>>iVal;
    inOut<<iVal;

    return true;
}


} /* namespace lul */
} /* namespace oslc */
