/*
 * ConfigOptions_test.cpp
 *
 *  Created on: Feb 22, 2018
 *      Author: admin
 */


//#define TEST_CONFIG_OPTS

//#ifdef TEST_CONFIG_OPTS


#include <oslc/lul/TestHarness.h>

#include <oslc/lul/ConfigOptions.h>

using namespace oslc::lul;

const float FLOAT_GLBL_TEST_VAL = 7.7;

REGISTER_CONFIG_OPT("testInt","This is an int test",int,0);
REGISTER_CONFIG_OPT("testFloat","This is a float test",float,FLOAT_GLBL_TEST_VAL);
REGISTER_CONFIG_OPT("testString","This is a string test",std::string,"default value string");

struct TestOpt
{
	int xyz;
	float abs;
};

//#define INT_TEST_VAL_1 1

REGISTER_TEST("ConfigOptions","test 1")
{
	using namespace oslc::lul;

	// try to create an illegal type
	//OptionContainer<TestOpt> bla;
	//Option<int,TestOpt> opt;
	//CmdLineOptions::getGlblCmdLineOptions().displayUsageMsg();

    // !!! test config
	ConfigOptions configOpts(ConfigOptions::getGlblConfig());
	TEST_EXP(configOpts.loadFromFile("config/dbhda.cfg"))

	TEST_EXP(configOpts.getVal<float>("testFloat")==FLOAT_GLBL_TEST_VAL);

	const int TestIntVal = 1;
	configOpts.addOption<int>("i","int value",TestIntVal);
	TEST_EXP(configOpts.getVal<int>("i")==TestIntVal);

	const float TestFloatVal = 2.3f;
	configOpts.addOption<float>("f","float value",TestFloatVal);
	TEST_EXP(configOpts.getVal<float>("f")==TestFloatVal);

	const char TestStringVal[] = "abcd";
	configOpts.addOption<std::string>("str","string value",TestStringVal);
	TEST_EXP(configOpts.getVal<std::string>("str")==TestStringVal);
	//configOpts.addOption<const char *>("str2","string value","efg");
	configOpts.addOption<std::string>("str2","string value","efg");
	//int i0 = ConfigOptions::getGlblConfig()->getVal<int>("testInt");
	int i0 = configOpts.getVal<int>("testInt");
	int i1 = configOpts.getVal<int>("i");
	std::string  str = configOpts.getVal<std::string>("str");
	//std::string  str2 = configOpts.getVal<const char *>("str2");
	std::string  str2 = configOpts.getVal<std::string>("str2");
	float  f1 = configOpts.getVal<float>("f");

	float  f11 = configOpts.getVal<float>("testFloat");

	//float  f2 = configOpts.getVal<int>("f");
	configOpts.setVal<float>("f",10.5);
	f1 = configOpts.getVal<float>("f");
	LOG_TEST_MSG(i1<<":"<<f1<<":"<<f11<<":"<<i0);
	ConfigOptionPtr<float> optPtr1 = configOpts.getOpt<float>("testFloat");
	LOG_TEST_MSG(optPtr1->name<<":"<<optPtr1->desc<<":"<<optPtr1->getVal());
	return true;
}

//#endif // TEST_CONFIG_OPTS
