/*
 * TestHarness_test.cpp
 *
 *  Created on: Feb 15, 2018
 *      Author: admin
 */

#include "TestHarness.h"
#include <sstream>
#include <iostream>

namespace oslc {
namespace lul {


//template<typename T>
//struct lval { T t; T &getlval() { return t; } };


//#define GEN_STR_BASE(tempStrm,s,f) \
//  std::stringstream tempStrm; \
//  tempStrm<<s; \
//  f(tempStrm.str())
//
//#define GEN_STR_CALL_FUNC(s,f) GEN_STR_BASE(MACRO_CONCAT(TEST_BASE_NAME,__COUNTER__),s,f)


REGISTER_TEST("TestHarness","test 1")
{
  LOG_TEST_MSG("Logging a test message");
  std::string testStr = "abcd";
  std::stringstream tempStrm;
  tempStrm<<"Using str: "<<testStr<<12345;
  LOG_TEST_MSG(tempStrm.str());
  //LOG_TEST_MSG(GEN_STR("abc"<<"def"<<345));
  GEN_STR_CALL_FUNC("Gen test "<<" def "<<345,LOG_TEST_MSG);
  LOG_TEST_MSG("abc "<<"def "<<345);
//  auto a = (static_cast<std::ostringstream&>(
//    lval<std::ostringstream>().getlval() << "Value: " << 5).str().c_str());
  int num = 10;
  TEST_EXP(num==10);
  return true;
}


} /* namespace lul */
} /* namespace oslc */
