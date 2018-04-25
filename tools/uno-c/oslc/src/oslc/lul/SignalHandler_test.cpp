/*
 * SignalHandler_test.cpp
 *
 *  Created on: Feb 17, 2018
 *      Author: admin
 */

#include <oslc/lul/SignalHandler.h>

#include <oslc/lul/TestHarness.h>

namespace oslc {
namespace lul {

REGISTER_TEST("SignalHandler","Test 1")
{

	SignalHandler sh;
	sh.set(SIGABRT).set(SIGFPE).set(SIGSEGV);

	//std::abort();
	int retVal = sh.retPoint();
	if(retVal==0)
	{
	  int x = 3/0;
	}
	else
	{
	  LOG_INFO("Caught exception "<<retVal);
	}

	retVal = sh.retPoint();
	if(retVal==0)
	{
	  //std::abort();
	  int *iptr = 0;
	  *iptr = 1;
	}
	else
	{
	  LOG_INFO("Caught exception "<<retVal);
	}

// abort not working
//	retVal = sh.retPoint();
//	if(retVal==0)
//	{
//	  std::abort();
//	}
//	else
//	{
//	  LOG_INFO("Caught exception "<<retVal);
//	}

	//TEST_EXP(3>100);

	return true;

}

} /* namespace lul */
} /* namespace oslc */
