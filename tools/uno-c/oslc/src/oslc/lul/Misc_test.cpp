/*
 * Misc_test.cpp
 *
 *  Created on: Feb 25, 2018
 *      Author: admin
 */



#include <oslc/lul/TestHarness.h>

#include "Misc.h"


REGISTER_TEST("Misc","test 1")
{
  std::vector<int> v = { 1,2,3,4,5,6,7,8,9,10 };
  TEST_EXP(v.size()==10);
  oslc::lul::erase_if(v,[](int x){ return x<=5; });
  TEST_EXP(v.size()==5);
  return true;
}
