/*
 * Configuration.h
 *
 *  Created on: Nov 19, 2017
 *      Author: admin
 */

#ifndef CONFIGURATION_H_
#define CONFIGURATION_H_

#include <oslc/lul/ConfigOptions.h>
#include <oslc/lul/CmdLineOptions.h>
#include <oslc/lul/Version.h>

#define INPUT_FILE_CMD_OPT_STR "-i"
#define OUTPUT_FILE_CMD_OPT_STR "-o"
#define COLUMN_CMD_OPT_STR "-c"
#define MIN_SUPPORT_CMD_OPT_STR "-mins"
#define TEST_CMD_OPT_STR "-test"
#define ORD_CMD_OPT_STR "-ord"
#define REDUCE_CMD_OPT_STR "-r"
#define COMB_GROUP_CMD_OPT_STR "-cg"
#define COMB_GROUP_SIZE_CMD_OPT_STR "-cgs"
#define COMB_GROUP_COUNT_CMD_OPT_STR "-cgc"
#define VERSION_CMD_OPT_STR "--version"
#define ZERO_BASED_CMD_OPT_STR "-zb"

//
// configuration class for fast access to configuration values
//
// TODO: needs more work, auto load all values???
class DBHDAConfig
{
public:
	DBHDAConfig(oslc::lul::CmdLineOptions &cmdLineOpts): m_cmdLineOptions(cmdLineOpts)
    {

    }

	bool isZeroBasedMode()
	{
		static bool isZeroBased = m_cmdLineOptions.getOpt<bool>(ZERO_BASED_CMD_OPT_STR)->isAvailable;
		return isZeroBased;
	}

	int colRowNumToOutput(int colRowNum)
	{
	  if(isZeroBasedMode())
		  return colRowNum;
	  else
		  return colRowNum + 1;
	}

	// Get configuration -> !!! Note: first time we call must specify cmdLineOpts !!!
	static DBHDAConfig &get(oslc::lul::CmdLineOptions *cmdLineOpts = 0)
	{
		//TODO: very simple construct. Should we make this more sophisticated - re-initialize, check for errors ...
	    static DBHDAConfig *theOne = new DBHDAConfig(*cmdLineOpts);
//		static DBHDAConfig *theOne = 0;
//		if(cmdLineOpts!=NULL)
//		{
//			if(theOne)
//				delete theOne;
//			theOne = new DBHDAConfig(*cmdLineOpts);
//		}

		return *theOne;
	}

	// data
	oslc::lul::CmdLineOptions &m_cmdLineOptions;

};

// column row to output shortcut macro
#define CRN2OUT(crNum) DBHDAConfig::get().colRowNumToOutput(crNum)

//
// Version information for application
//

DECLARE_VERSION_INFO(DBasisHDA);

#endif /* CONFIGURATION_H_ */
