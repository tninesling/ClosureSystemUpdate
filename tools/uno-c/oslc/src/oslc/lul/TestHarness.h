/*
 * TestHarness.h
 *
 *  Created on: Feb 15, 2018
 *      Author: admin
 */

#ifndef SRC_OSLC_LUL_TESTHARNESS_H_
#define SRC_OSLC_LUL_TESTHARNESS_H_

#include <string>
#include <map>

// needed for macro functions (concat, stringfy etc.)
#include <oslc/lul/Misc.h>
#include <oslc/lul/Log.h>

#ifdef USE_SIG_HANDLER
// handle signals
//#include <csignal>
//#include <signal.h>
//#include <functional>
#include <oslc/lul/SignalHandler.h>
#endif //USE_SIG_HANDLER

namespace oslc {
namespace lul {

class TestHarness
{
public:
	struct TestInfo;
	typedef int (*TestFunction)(TestInfo *);//(TestHarness *context);
	struct TestInfo
	{
		enum Status { Pass, Fail, Warn, Unknown };
		std::string type;
		std::string name;
		const char *file;
		int line;
		TestFunction func;
		Status status;
	};

private:
	TestHarness() {}
public:
	static TestHarness &getTestHarness()
	{
	  static TestHarness theOne;
	  return theOne;
	}

	std::string absTestName(TestInfo &ti) { return  ti.type + " - " + ti.name; }
	// test functions
	bool registerTest(std::string type, std::string name, TestFunction func, const char *file = __FILE__, int line = __LINE__)
	{
		TestInfo ti = {type,name,file,line,func, TestInfo::Unknown};
		std::string absPath = type + " - " + name;
		m_testList[absPath] = ti;
		return true;
	}

	// misc test stats
	struct Stats
	{
		Stats() { reset(); }
		void reset() { failed = 0; passed = 0; }
		// data
		int failed;
		int passed;
	};
	Stats m_stats;
	Stats &getStats() { return m_stats; }
	void displayStatsSummary()
	{
//		LOG_INFO("Tests Complete");
//		//LOG_INFO("Tests Results:");
//		LOG_INFO(m_stats.failed << " Tests Failed");
//		LOG_INFO(m_stats.passed << " Tests Passed");
		//TODO: add test run time use chrono...
		//std::cout<<" **** Tests Complete"<<std::endl;
		std::cout<<"************************"<<std::endl;
		std::cout<<"**** Tests Complete ****"<<std::endl;
		std::cout<<"************************"<<std::endl;
		std::cout<<"Tests Failed: "<<m_stats.failed<<std::endl;
		std::cout<<"Tests Passed: "<<m_stats.passed<<std::endl;
		std::cout<<"************************"<<std::endl;
	}


	int runTests()
	{
#ifdef USE_SIG_HANDLER
		SignalHandler sigHandler;
		sigHandler.set(SIGABRT).set(SIGFPE).set(SIGSEGV);
#endif
		//int failCount = 0;
		m_stats.reset();
		for(auto testItr : m_testList)
		{

            TestInfo &ti =  testItr.second;
			LOG_LOC(ti.file,ti.line,MsgLog::Info,"**** Running: " << absTestName(ti) << " ****");
#ifdef USE_SIG_HANDLER
			volatile int retVal = sigHandler.retPoint();
			if(retVal==0)
			  ti.func();
			else
			{
			  LOG_LOC(ti.file,ti.line,MsgLog::Info,"System exception "<<retVal<<" raised in test: " << ti.name);
			}
#else
		    ti.func(&ti);
		    if(ti.status==TestInfo::Fail)
		    {
			  //LOG_LOC(ti.file,ti.line,MsgLog::Info,"test failed: " << ti.name);
			  LOG_LOC(ti.file,ti.line,MsgLog::Info,"**** "<< absTestName(ti) << " Failed ****");
			  m_stats.failed++;
		    }
		    else
		    {
			  LOG_LOC(ti.file,ti.line,MsgLog::Info,"**** "<<absTestName(ti) << " Passed ****");
			  m_stats.passed++;
		    }

#endif //USE_SIG_HANDLER

		}
		// display stats
		displayStatsSummary();
		return m_stats.failed;
	}

//	void logMsg(std::string msg, const char *file = __FILE__, int line = __LINE__)
//	{
//		//LOG_ERROR(msg);
//		LOG_LOC(file,line,MsgLog::Info,msg);
//	}
//
//	void logErr(std::string msg, const char *file = __FILE__, int line = __LINE__)
//	{
//		//LOG_ERROR(msg);
//		LOG_LOC(file,line,MsgLog::Error,msg);
//	}

	std::ostream &log(MsgLog::Type type, const char *fileName = __FILE__, int lineNum = __LINE__)
	{
		 return MsgLog::log(fileName,lineNum,type);
	}

	~TestHarness() {}

	// data
	std::map<std::string,TestInfo> m_testList;
};


#define TEST_BASE_NAME test_harness_test_auto_


#define REGISTER_TEST_BASE(uniqueName,type,name) \
static int uniqueName(oslc::lul::TestHarness::TestInfo *); \
static volatile bool MACRO_CONCAT(TEST_BASE_NAME,__COUNTER__) = oslc::lul::TestHarness::getTestHarness().registerTest(type,name,uniqueName,__FILE__,__LINE__); \
static int uniqueName(oslc::lul::TestHarness::TestInfo *tiPtr)


//	static volitile bool MACRO_CONCAT(global_test_func_,__COUNTER__) = oslc::lul::TestHarness::getTestHarness().registerTest(type,name,func);

#define REGISTER_TEST(type,name) REGISTER_TEST_BASE(MACRO_CONCAT(TEST_BASE_NAME,__COUNTER__),type,name)


#define LOG_TEST_MSG(msg) oslc::lul::TestHarness::getTestHarness().log(oslc::lul::MsgLog::Info, __FILE__,__LINE__)<<msg<<"\n"

#define LOG_TEST_ERR(msg) oslc::lul::TestHarness::getTestHarness().log(oslc::lul::MsgLog::Error, __FILE__,__LINE__)<<msg<<"\n"

//#define LOG_TEST_EXP(msg) oslc::lul::TestHarness::getTestHarness().logErr(STRINGIFY(msg),__FILE__,__LINE__)

#define TEST_SET_FAIL tiPtr->status = oslc::lul::TestHarness::TestInfo::Fail

#define TEST_EXP(exp)\
	try \
    {    \
      if(!(exp)) \
	  { \
          TEST_SET_FAIL; \
    	  LOG_TEST_ERR(STRINGIFY(exp)); \
      } \
      else \
      { \
         LOG_TEST_MSG(STRINGIFY(exp)); \
      } \
    } \
    catch(...) \
    { \
      TEST_SET_FAIL; \
      LOG_TEST_ERR("Exception raised"); \
	  LOG_TEST_ERR(STRINGIFY(exp)); \
    }

#define RUN_ALL_TESTS    TestHarness::getTestHarness().runTests();

#ifdef GENERATE_TEST_MAIN
int main(int argc, char **argv)
{
	// Todo: add handle test params
	TestHarness::getTestHarness().runTests();
}
#endif



} /* namespace lul */
} /* namespace oslc */

#endif /* SRC_OSLC_LUL_TESTHARNESS_H_ */
