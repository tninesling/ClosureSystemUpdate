/*
 * Serializer_test.cpp
 *
 *  Created on: Feb 15, 2018
 *      Author: admin
 */

//#include "CmdLineOptions.h"

#include <oslc/lul/TestHarness.h>

#include <oslc/lul/CmdLineOptions.h>

#include<iostream>
using namespace std;

//namespace oslc {
//namespace lul {


using namespace oslc::lul;

#define TEST_CMD_OPT_STR "-xij"

INIT_CMD_LINE("CmdLineTest", "Usage is CmdLineTest <-i inputFile> [<-o outputFile> [...]");
REGISTER_CMDLINE_OPT(TEST_CMD_OPT_STR, "Input file path\n\tmore info..", std::string,"",CmdLineOption::OptionType::ValueOpt);
REGISTER_CMDLINE_SYNONYM(TEST_CMD_OPT_STR, "-x1");
REGISTER_CMDLINE_SYNONYM(TEST_CMD_OPT_STR, "-x2");
REGISTER_CMDLINE_OPT("-yyyy", "Output file path\n\tmore info..", int, 3,CmdLineOption::OptionType::ValueOpt);

REGISTER_CMDLINE_OPT("--IntVec", "Combination group. Usage is: [1,2,3]\n\toptional argument", std::vector<int>, std::vector<int>(), CmdLineOption::OptionType::ValueOpt);
REGISTER_CMDLINE_OPT("--FloatVec", "Combination group. Usage is: [1,2,3]\n\toptional argument", std::vector<float>, std::vector<float>(), CmdLineOption::OptionType::ValueOpt);


//template<class T>
typename std::enable_if<is_vector< vector<int> >::value,bool>::type abc;

//void testCmdLineOptions(int argc, char **argv)
//{
//	//const bool res = is_vector< vector<int> >::value;
//	TEST_EXP(is_vector< vector<int> >::value==true);
//
//	std::enable_if<is_vector< vector<int> >::value,int>::type iVal = 1;
//
//	CmdLineOptions::getGlblCmdLineOptions().displayUsageMsg();
//	CmdLineOptions clOpts(CmdLineOptions::getGlblCmdLineOptions());
//
//	if(!clOpts.parse(argc, argv))
//	{
//		cout<<"Error reading command line options please fix your command line options and try again!"<<endl;
//		clOpts.displayUsageMsg();
//		//return 1;
//	}
//	clOpts.displayUsageMsg();
//
//	//clOpts[]
//	//using CmdLineOptions;
//	//CmdLineOptions::CmdLineOptionPtr<std::string> testOpt = clOpts[TEST_CMD_OPT_STR];
//	CmdLineOptions::OptionBasePtr testOpt = clOpts[TEST_CMD_OPT_STR];
//	TEST_EXP(testOpt->isAvailable);
//	//	if(testOpt->isAvailable)
//	//	{
//	//		cout<<"Running tests..."<<endl;
//	//		//runTests();
//	//		//return 0;
//	//	}
//
//}



REGISTER_TEST("CmdLineOptions","test 1")
{
	//const bool res = is_vector< vector<int> >::value;
	TEST_EXP(is_vector< vector<int> >::value==true);

	std::enable_if<is_vector< vector<int> >::value,int>::type iVal = 1;
	LOG_TEST_MSG("iVal=="<<iVal);

	CmdLineOptions::getGlblCmdLineOptions().displayUsageMsg();
	CmdLineOptions clOpts(CmdLineOptions::getGlblCmdLineOptions());

	char *argv[3];// = "-a 123 -b 456 -d ZXY";
	argv[0]="myExeFName";
	argv[1]=TEST_CMD_OPT_STR;
	argv[2]="testOptData";
	//argv[3]
	int argc = 3;

	if(!clOpts.parse(argc, argv))
	{
		TEST_SET_FAIL;
		cout<<"Error reading command line options please fix your command line options and try again!"<<endl;
	}

	clOpts.displayUsageMsg();

	LOG_TEST_MSG("recreated command line= "<<CmdLineOptions::cmdLineToString(argc,argv));
	//clOpts[]
	//using CmdLineOptions;
	//CmdLineOptions::CmdLineOptionPtr<std::string> testOpt = clOpts[TEST_CMD_OPT_STR];
	CmdLineOptions::OptionBasePtr testOpt = clOpts[TEST_CMD_OPT_STR];
	TEST_EXP(testOpt->isAvailable);
	CmdLineOptionPtr<std::string> testOptT2 = clOpts.getOpt<std::string>(TEST_CMD_OPT_STR);
	//CmdLineOptionPtr<std::string> outputFileOpt = cmdLineOptions.getOpt<std::string>(OUTPUT_FILE_CMD_OPT_STR);
	LOG_TEST_MSG("argv[2]=="<<testOptT2->getVal());
	TEST_EXP(testOptT2->getVal()==argv[2]);

	return true;
}


//} /* namespace lul */
//} /* namespace oslc */
