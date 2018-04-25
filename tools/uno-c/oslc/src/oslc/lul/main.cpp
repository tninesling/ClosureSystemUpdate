/*
 * main.cpp
 *
 *  Created on: Dec 4, 2017
 *      Author: admin
 */

//
// main test file for library
//

#include <oslc/lul/Misc.h>


//#pragma message ("LIB_EXE_MODE ON=" STRINGIFY(LIB_EXE_MODE))
#ifdef LIB_EXE_MODE
  #pragma message ("LIB_EXE_MODE is ON")
#else
  #pragma message ("LIB_EXE_MODE is OFF")
#endif


#ifdef LIB_EXE_MODE


//#include <oslc/lul/Log.h>
//#include <oslc/lul/StringParser.h>
//#include <oslc/lul/Option.h>

//#include<iostream>
//using namespace std;

#include <oslc/lul/TestHarness.h>
using namespace oslc::lul;

int main(int argc, char **argv)
{
   // test harness
   TestHarness::getTestHarness().runTests();

   return 0;
}


#endif //LIB_EXE_MODE
