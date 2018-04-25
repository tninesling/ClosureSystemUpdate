/*
 * Version_test.cpp
 *
 *  Created on: Feb 22, 2018
 *      Author: admin
 */

#include <oslc/lul/TestHarness.h>

#include <oslc/lul/Version.h>
using namespace oslc::lul;

//GENERATE_VERSION_INFO(PRODUCTX,0,0,1,"b");
DECLARE_VERSION_INFO(PRODUCTX);

DEFINE_VERSION_INFO(PRODUCTX,0,0,1,"b");

REGISTER_TEST("Version","test 1")
{
	//GEN_TEMPLATE_STR_LITERAL_FUNC(DBASEHDA);
	//
	//const char*res = GLF_DBASEHDA();
	//
	//const char *funcTest() { return "ABC"; }
	//
	//VersionInfo<GET_TEMPLATE_STR_LITERAL_FUNC(DBASEHDA)> versionInfo(0,0,2,"bla","","");
	//
	//VersionInfo<funcTest> versionInfo2(0,0,2,"bla","","");
	//using namespace oslc::lul;

	auto vi3 = GET_VERSION_INFO(PRODUCTX);
	//DBASEHDAVersionInfo *vi4 = GET_VERSION_INFO(DBASEHDA);
	PRODUCTXVersionInfo *vi4 = GET_VERSION_INFO(PRODUCTX);

	LOG_TEST_MSG(vi3->BuildDate<<":"<<vi4->BuildTime);
	LOG_TEST_MSG(vi4->getStdVersionString());

	// test duplicate version definition
	bool duplicateExceptionRaised = false;
	try
	{
	  //PRODUCTXVersionInfo badIdeaVersion(1,2,3);
	  DEFINE_VERSION_INFO(PRODUCTX,0,0,1,"bad");
	}
	catch(std::runtime_error e)
	{
		duplicateExceptionRaised = true;
		LOG_TEST_MSG("Caught duplicate version exception: "<<e.what());
	}

	//if(!duplicateExceptionRaised)
		//  cout<<"Error: did not catch duplicate version exception"<<endl;
	TEST_EXP(duplicateExceptionRaised);

	return true;
}




