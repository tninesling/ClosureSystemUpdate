/*
 * Serializer_test.cpp
 *
 *  Created on: Feb 15, 2018
 *      Author: admin
 */

#include "Serializer.h"
#include <iostream>

#include <oslc/lul/TestHarness.h>


namespace oslc {
namespace lul {

REGISTER_TEST("Serializer","Serializer test 1")
{
  Serializer<std::istream> ser(std::cin);
  return true;
}


} /* namespace lul */
} /* namespace oslc */
