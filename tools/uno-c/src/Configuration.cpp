/*
 * Configuration.cpp
 *
 *  Created on: Nov 19, 2017
 *      Author: admin
 */

#include "Configuration.h"

#include <oslc/lul/CmdLineOptions.h>

#include <oslc/lul/Version.h>

//
// Version information for application
//

// TODO: bump version numbers here when appropriate or automate (using cmake/make and git version numbers etc.)
DEFINE_VERSION_INFO(DBasisHDA,0,0,14,"b");


using namespace oslc::lul;

//
// configuration options
//
// TODO: setup external configuration file - values below are just place holders
REGISTER_CONFIG_OPT("testInt","This is an int test",int,0);
REGISTER_CONFIG_OPT("testFloat","This is a float test",float,7.7);
REGISTER_CONFIG_OPT("testString","This is a string test",std::string,"default value string");

//
// command line options
//

INIT_CMD_LINE("DBasisHDA", "Usage is DBasisHDA <-i inputFile> [<-o outputFile> [<-c column>]");
REGISTER_CMDLINE_OPT(INPUT_FILE_CMD_OPT_STR,  "Input file path\n\tthis is a required argument",  std::string, "", CmdLineOption::OptionType::ValueOpt);
REGISTER_CMDLINE_OPT(OUTPUT_FILE_CMD_OPT_STR, "Output file path\n\toptional argument", std::string, "", CmdLineOption::OptionType::ValueOpt);
REGISTER_CMDLINE_OPT(COLUMN_CMD_OPT_STR, "A column to work on\n\toptional argument",  int, 0, CmdLineOption::OptionType::ValueOpt);
REGISTER_CMDLINE_OPT(ORD_CMD_OPT_STR,   "Run in one row deletion mode\n\toptional argument",    bool, false, CmdLineOption::OptionType::BinaryOpt);
REGISTER_CMDLINE_OPT(REDUCE_CMD_OPT_STR, "Run in reduce mode\n\treduce after call to shd", bool, false, CmdLineOption::OptionType::BinaryOpt);
REGISTER_CMDLINE_OPT(TEST_CMD_OPT_STR, "Run test harness\n\tmore info...", bool, false, CmdLineOption::OptionType::BinaryOpt);
//TODO: !!! use SUPPORT_LOWER_BOUND_DEFAULT from table.h?
REGISTER_CMDLINE_OPT(MIN_SUPPORT_CMD_OPT_STR, "Min support to work on\n\toptional argument", int, 1, CmdLineOption::OptionType::ValueOpt);
// combination group options
REGISTER_CMDLINE_OPT(COMB_GROUP_CMD_OPT_STR, "Combination group. Usage is: " COMB_GROUP_CMD_OPT_STR " [1,2,3]\n\toptional argument", std::vector<int>, std::vector<int>(), CmdLineOption::OptionType::ValueOpt);
REGISTER_CMDLINE_OPT(COMB_GROUP_SIZE_CMD_OPT_STR, "Combination group size\n\toptional argument", int, 0, CmdLineOption::OptionType::ValueOpt);
REGISTER_CMDLINE_OPT(COMB_GROUP_COUNT_CMD_OPT_STR, "Combination group count\n\toptional argument", int, 0, CmdLineOption::OptionType::ValueOpt);
// version information
REGISTER_CMDLINE_OPT(VERSION_CMD_OPT_STR, "Display version information", bool, false, CmdLineOption::OptionType::BinaryOpt);
REGISTER_CMDLINE_SYNONYM(VERSION_CMD_OPT_STR, "-v");
// zero based io
REGISTER_CMDLINE_OPT(ZERO_BASED_CMD_OPT_STR, "Run in zero base mode\n\tby default all column and row numbers are one based\n\tif this option is specified, all input/output column numbers will be zero based", bool, false, CmdLineOption::OptionType::BinaryOpt);





