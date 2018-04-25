#include <iostream>
#include <string>
#include <fstream>
#include <vector>
#include <cstdlib>

#include "Table.h"
#include "Implication.h"
#include "SetOperations.h"
#include "timestamp.h"

// utility functions
#include "Util.h"

// internal application testing
#include "Tests.h"

// !!! configuration and command line options
#include <oslc/lul/CmdLineOptions.h>
#include <oslc/lul/ConfigOptions.h>
#include <oslc/lul/Version.h>
#include <oslc/lul/MathUtil.h>
using namespace oslc::lul;
////////
#include "Configuration.h"
////////
// for random numbers
#include <chrono>
#include <random>

// Not in use after version 0.0.4b
//void withConfidence(std::vector<std::vector<char> > matrix, int minSupport, int tableRequestedColumn, bool reduceMode, ostream &outputStream)
//{
//    vector< vector<int> > allResults = vector< vector<int> >();
//
//    // !!!  11.6.17 Oren temp fix: need to fix loop -> size should be a parameter????
//    int IterCount = matrix.size();
//    //for (int i=-1;i<2;i++) {
//    for (int i=-1;i<IterCount;i++) {
//        vector< vector<char> > m2 = matrix;
//        //Erase by setting all to 0
//        if (i > -1) {
//            for (int j=0;j<m2[i].size();j++) m2[i][j] = '0';
//        }
//        // create table with min support set
//        // !!!  11.7.17 Oren bug fix: m2 is not used?
//        //Table test2(matrix,minSupport);
//        Table test2(m2,minSupport,tableRequestedColumn,reduceMode);
//        vector<Implication> currImplications = test2.FindDBasis();
//        vector< vector<int> > newResults = test2.getNewResults(allResults, currImplications);
//        if (i == -1) allResults = newResults;
//
//        //print
//        if (newResults.size() > 0)
//        {
//        	outputStream << "New Rules for " << i << "\n";
//        	for (int s=0;s<newResults.size();s++)
//        	{
//        		for (int t=0;t<newResults[s].size()-1;t++)
//        			if (newResults[s][t] == -1)
//        				outputStream << "-> ";
//        			else
//        				outputStream << newResults[s][t] << " ";
//        		outputStream << "support = " << newResults[s][newResults[s].size()-1]<< "\n";
//        		// !!! !!! 10.28.17 oren change: do not buffer results for now to help debug crashes
//        		outputStream.flush();
//        	}
//        	//endOf print
//        }
//        else
//        	outputStream << "No new Rules for " << i << "\n";
//
//    }
//
//    //myfile.close();
//}

Implications withConfidenceV2(std::vector<std::vector<char> > matrix, int minSupport, int tableRequestedColumn, bool reduceMode, const vector<int> &removeRowList, ostream &outputStream)
{
    //vector< vector<int> > allResults = vector< vector<int> >();

    //vector< vector<char> > m2 = matrix;
    //Erase by setting all to 0
    for(int i=0;i<removeRowList.size();i++)
    {
    	int rowToDel = removeRowList[i];
    	for (int j=0;j<matrix[rowToDel].size();j++)
    		matrix[rowToDel][j] = '0';
    }
    try // 1.9.17 oren ->table constructor can fail !!!
    {
    // create table with min support set
    Table tbl(matrix,minSupport,tableRequestedColumn,reduceMode);
    Implications currImplications = tbl.FindDBasis();
    // convert implications to original table coordinates
    tbl.mapToOriginal(currImplications);

    return currImplications;
    }
    catch(...)
	{
    	// return empty implication set
    	return vector<Implication>();
	}
    //vector< vector<int> > newResults = test2.getNewResults(allResults, currImplications);
   //if (i == -1) allResults = newResults;
}

typedef vector<vector<int> > CombinationTable;
//static int
//math::Int2DVector vect2D = math::genCombinationList(5,3);


void generateUniqueCombinations(CombinationTable &permTbl, vector<int> numList, int combSize, int combCount)
{
	// construct a trivial random generator engine from a time-based seed:
	unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
	std::default_random_engine generator (seed);

	//std::uniform_int_distribution<int> distribution(0,numList.size()-1);
	// check if we are asked for combSize bigger then total amount of numbers
	if(combSize>numList.size())
	  combSize = numList.size();
	// check if we are asked for more combinations than available numbers or no combination count
	if( (combCount==0) || (combSize * combCount) > numList.size())
	  combCount = numList.size() / combSize + ( (numList.size() % combSize)>0 ? 1 : 0 );

	permTbl.resize(combCount);
	for(int i=0;i<combCount;i++)
	{
		// handle last comb group size -> might be smaller then requested
		if(combSize>numList.size())
		  combSize=numList.size();
		//combSize = (combSize<=numList.size()) ? combSize : numList.size();
		permTbl[i].resize(combSize);
		for(int j=0;j<combSize;j++)
		{
			int index = generator() % numList.size();//distribution(generator);
			permTbl[i][j] = numList[index];
			numList.erase(numList.begin()+index);
		}
	}
}

void generateCombinations(CombinationTable &permTbl, vector<int> numList, int combSize, int combCount)
{
	// construct a trivial random generator engine from a time-based seed:
	unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
	std::default_random_engine generator (seed);

	//std::uniform_int_distribution<int> distribution(0,numList.size()-1);
	// check if we are asked for combSize bigger then total amount of numbers
	if(combSize>numList.size())
	  combSize = numList.size();

	// generate all combinations (N/GS)
    math::Int2DVector allComb = math::genCombinationList(numList.size(),combSize);

	// check if we are asked for more combinations than available numbers or no combination count
	if( (combCount==0) || combCount > allComb.size())
	  combCount = allComb.size();

	permTbl.resize(combCount);
	for(int i=0;i<combCount;i++)
	{
		permTbl[i].resize(combSize);
		int combIndex = generator() % allComb.size();//distribution(generator);
		for(int j=0;j<combSize;j++)
		{
			permTbl[i][j] = numList [ allComb[combIndex][j] ];
		}
		allComb.erase(allComb.begin()+combIndex);
	}
}

#define WARN_COMB_EXPLOSION 1024*1024

int generateCombinationsV2(CombinationTable &permTbl, vector<int> numList, int combSize, int combCount)
{
	// construct a trivial random generator engine from a time-based seed:
	unsigned seed = std::chrono::system_clock::now().time_since_epoch().count();
	std::default_random_engine generator (seed);

	//std::uniform_int_distribution<int> distribution(0,numList.size()-1);
	// check if we are asked for combSize bigger then total amount of numbers
	if(combSize>numList.size())
	  combSize = numList.size();

	// generate all combinations (N/GS)
    //math::Int2DVector allComb = math::genCombinationList(numList.size(),combSize);
	unsigned long long numOfPossibleComb = math::comb(numList.size(),combSize);
	// sanity check
	//if(numOfPossibleComb>WARN_COMB_EXPLOSION)

	// check if we are asked for more combinations than available numbers or no combination count
	if( (combCount==0) || combCount > numOfPossibleComb)
	  combCount = numOfPossibleComb;

	// count the actual amount of times we had to try in order to produce all the unique combinations
	int tryCount = 0;

	permTbl.resize(combCount);
	int i=0;
	while(i<combCount)
	{
		tryCount++;
		// make sure we have enough size in vec
		permTbl[i].resize(combSize);
		//vector<bool> pickVec(combSize,0);
		// make copy of original group
		vector<int> pickNumList = numList;
		// create a random combination
		for(int j=0;j<combSize;j++)
		{
			int pickIndex = generator() % pickNumList.size();//distribution(generator);
	        permTbl[i][j] = pickNumList[pickIndex];
	        pickNumList.erase(pickNumList.begin()+pickIndex);
		}
		// check if unique
		int j=0;
		while(j<i)
		{
			int foundCount = 0;
			for(int k=0;k<combSize;k++)
				if(std::find(permTbl[j].begin(),permTbl[j].end(),permTbl[i][k])==permTbl[j].end())
					break; // not found, we can check next comb group
				else
					foundCount++;
			// check if all elements found i.e. it is not unique
			if(foundCount==combSize)
				break;
            // next round
			j++;
		}
		// check if all clear
		if(j==i)
			i++;
	}

	return tryCount;
}


void withConfidenceCombinations(const std::vector<std::vector<char> > &matrix, int minSupport, int tableRequestedColumn, bool reduceMode, const CombinationTable &removeRowTable, ostream &outputStream, bool aggregateRules)
{
  // create original table - no internal reduction, this will be used as base -> 1.25.18 -> reduce hack
  Table orgTbl(matrix,minSupport,tableRequestedColumn,reduceMode,false);
  // create aggregated table with min support set
  Table totalTbl(matrix,minSupport,tableRequestedColumn,reduceMode);
  //vector<Implication> origImplications = totalTbl.FindDBasis();
  Implications allImplications = totalTbl.FindDBasis();
  // print the combined list of implications
  outputStream<<"***"<<endl;
  outputStream<<"*** Printing all implications (original table)"<<endl;
  outputStream<<"***"<<endl;
  totalTbl.prettyprintImplications(allImplications);
  ////////////////////////////////////////////
  // convert implications to original table coordinates
  totalTbl.mapToOriginal(allImplications);

  //vector< vector<int> > allResults;
  //allResults = totalTbl.getNewResults(allResults, origImplications);

  // now for each permutation of rows test and aggregate
  for(int i=0;i<removeRowTable.size();i++)
  {
	  const vector<int> &removeRowList = removeRowTable[i];
	  auto printRulesHelper = [&](std::string msg)
	  	{
		  //outputStream<<"***"<<endl;
		  outputStream<<"*** "<<msg;
		  //for(const auto & value: removeRowList)
		  for(int i=0;i<removeRowList.size()-1;i++)
		  {
			  // output row number and add 1 to convert from zero based matrix to standard form
			  //outputStream << value + 1 << ",";
			  outputStream << removeRowList[i] + 1 << ", ";
		  }
		  // print last one
		  outputStream << removeRowList[removeRowList.size()-1] + 1;
		  outputStream<<" ***"<<"\n";
		  //outputStream<<"***"<<endl;
	  	};

	  printRulesHelper("Remove row list: ");

	  Implications currImplications = withConfidenceV2(matrix,minSupport,tableRequestedColumn,reduceMode,removeRowList,outputStream);

	  // select/filter the new implications
      timestamp_print(); std::cout <<"Begin select new implications"<<std::endl;
	  Implications newImps;
	  allImplications.selectNewFrom(currImplications,newImps);
      timestamp_print(); std::cout <<"End select new implications"<<std::endl;
	  // if we did not find any new implications
	  //if(currImplications.isEmpty() || currImplications<allImplications)
	  if(newImps.isEmpty())
	  {
		printRulesHelper("No new rules for removed row list: ");
        continue;
	  }
	  // else
	  // we have new implications
	  // print rules
	  std::stringstream strStream;
	  strStream<<"Found "<<newImps.size()<<" new rules for removed row list: ";
	  //printRulesHelper("New rules for removed row list: ");
	  printRulesHelper(strStream.str());
	  //totalTbl.prettyprintImplications(currImplications);
	  totalTbl.prettyprintImplications(newImps,false);
	  // remove the cycles from new implications
      timestamp_print(); std::cout <<"Begin cycles"<<std::endl;
	  EquivalenceMap newEquivs;
	  allImplications.handleCycles(newImps,newEquivs);
      timestamp_print(); std::cout <<"End cycles"<<std::endl;
	  // aggregate rules
	  //allImplications += currImplications;
	  // if we are in aggregateRules mode
	  if(aggregateRules)
	  {
	    allImplications.add(newImps);
	    // 4.13.18 -> print all implications to make it easier to analyze results
	    // print the combined list of implications
		outputStream<<"***"<<endl;
		outputStream<<"*** Printing all implications"<<endl;
		outputStream<<"***"<<endl;
		//totalTbl.prettyprintImplications(allImplications,false);
		orgTbl.prettyprintImplications(allImplications,false);
	  }
  }
  // if we are in aggregateRules mode
  if(aggregateRules)
  {
	  // print the combined list of implications
	  outputStream<<"***"<<endl;
	  outputStream<<"*** Printing all implications"<<endl;
	  outputStream<<"***"<<endl;
	  //totalTbl.prettyprintImplications(allImplications,false);
	  orgTbl.prettyprintImplications(allImplications,false);
	  //  outputStream<<"***"<<endl;
	  //  outputStream<<"*** Reducing all implications"<<endl;
	  //  outputStream<<"***"<<endl;
	  //totalTbl.reduce(tableRequestedColumn,allImplications);// ????
	  orgTbl.reduce(tableRequestedColumn,allImplications);
	  outputStream<<"***"<<endl;
	  outputStream<<"*** Printing all implications reduced"<<endl;
	  outputStream<<"***"<<endl;
	  //totalTbl.prettyprintImplications(allImplications,false);
	  orgTbl.prettyprintImplications(allImplications,false);
  }

}

//
// parallel tests
//

//#include <sys/wait.h>
//#include <stdio.h>
//#include <stdlib.h>
//#include <unistd.h>
//#include <string.h>
//
//void parallelTest()
//{
//    int pipefd[2];
//     pid_t cpid;
//     char buf;
//
//     pipe(pipefd); // create the pipe
//     cpid = fork(); // duplicate the current process
//     if (cpid == 0) // if I am the child then
//     {
//         close(pipefd[1]); // close the write-end of the pipe, I'm not going to use it
//         while (read(pipefd[0], &buf, 1) > 0) // read while EOF
//             write(1, &buf, 1);
//         write(1, "\n", 1);
//         close(pipefd[0]); // close the read-end of the pipe
//         exit(EXIT_SUCCESS);
//     }
//     else // if I am the parent then
//     {
//         close(pipefd[0]); // close the read-end of the pipe, I'm not going to use it
//         const char *msg = "bla";
//         write(pipefd[1], msg, strlen(msg)); // send the content of argv[1] to the reader
//         close(pipefd[1]); // close the write-end of the pipe, thus sending EOF to the reader
//         wait(NULL); // wait for the child process to exit before I do the same
//         exit(EXIT_SUCCESS);
//     }
//}

int main(int argc, char* argv[])
{
	//parallelTest();

	DBasisHDAVersionInfo *verInfo = GET_VERSION_INFO(DBasisHDA);

	//CmdLineOptions::getGlblCmdLineOptions().displayUsageMsg();
	CmdLineOptions cmdLineOptions(CmdLineOptions::getGlblCmdLineOptions());

	if(argc==1 || !cmdLineOptions.parse(argc, argv))
	{
		cout<<"Error reading command line options please fix your command line options and try again!"<<endl;
		cmdLineOptions.displayUsageMsg();
		return 1;
	}

	//
	// One time configuration initialization
	// TODO: replace cmdline code with configuration class. auto read all values!
	DBHDAConfig::get(&cmdLineOptions);

	CmdLineOptionPtr<bool> versionOpt = cmdLineOptions.getOpt<bool>(VERSION_CMD_OPT_STR);

	if(versionOpt->isAvailable)
	{
		cout<<verInfo->getStdVersionString()<<endl;
		return 0;
	}

	CmdLineOptionPtr<bool> testOpt = cmdLineOptions.getOpt<bool>(TEST_CMD_OPT_STR);

	if(testOpt->isAvailable)
	{
		cout<<"Running tests..."<<endl;
		runTests();
		return 0;
	}

	CmdLineOptionPtr<bool> oneRowDelOpt = cmdLineOptions.getOpt<bool>(ORD_CMD_OPT_STR);
	if(oneRowDelOpt->isAvailable)
	{
		cout<<"Running in one row deletion mode..."<<endl;
		//return 0;
	}
	bool oneRowDelMode = oneRowDelOpt->isAvailable;

	CmdLineOptionPtr<bool> reduceCmdLineOpt = cmdLineOptions.getOpt<bool>(REDUCE_CMD_OPT_STR);
	if(reduceCmdLineOpt->isAvailable)
	{
		cout<<"Running in reduce mode..."<<endl;
		//return 0;
	}
	bool reduceMode = reduceCmdLineOpt->isAvailable;

	CmdLineOptionPtr<std::string> outputFileOpt = cmdLineOptions.getOpt<std::string>(OUTPUT_FILE_CMD_OPT_STR);
	if(outputFileOpt->isAvailable)
	{
	    std::string outputFileName = outputFileOpt->getVal();
		cout<<"Using output file: "<<outputFileName<<endl;
		freopen (outputFileName.data(),"w",stdout);
		// issue #27 -> store original cmd line for future reference
		cout<<"Using command line: "<<CmdLineOptions::cmdLineToString(argc,argv)<<endl;
	}

	CmdLineOptionPtr<std::string> inputFileOpt = cmdLineOptions.getOpt<std::string>(INPUT_FILE_CMD_OPT_STR);
	if(!inputFileOpt->isAvailable)
	{
		cout<<"Error missing input file!"<<endl;
		return 1;
	}

    std::string inputFileName = inputFileOpt->getVal();
	cout<<"Using input file: "<<inputFileName<<endl;

	int tableRequestedColumn;
	CmdLineOptionPtr<int> columnOpt = cmdLineOptions.getOpt<int>(COLUMN_CMD_OPT_STR);
	if(columnOpt->isAvailable)
	{
		int userRequestedColumn = columnOpt->getVal();
		cout<<"Using Matrix column: "<<userRequestedColumn<<endl;
  	    // handle zero base
		if(!DBHDAConfig::get().isZeroBasedMode())
		  //!!! since the code expects a C zero based column number we need to translate it by subtracting 1
		  tableRequestedColumn = userRequestedColumn - 1;
	}
	else
		tableRequestedColumn = -1;

	int minSupport;
	CmdLineOptionPtr<int> minSupportOpt = cmdLineOptions.getOpt<int>(MIN_SUPPORT_CMD_OPT_STR);
	if(minSupportOpt->isAvailable)
	{
		minSupport = minSupportOpt->getVal();
	}
	else // set to default value if not supplied by user
		minSupport = SUPPORT_LOWER_BOUND_DEFAULT;

    // always print the minimum support in use!
	cout<<"Using minSupport: "<<minSupport<<endl;

	// always print if using zero base
	cout<<"Using zero base: "<<std::boolalpha<<DBHDAConfig::get().isZeroBasedMode()<<endl;

	// print code base version
	cout<<"Using code base: "<<verInfo->getStdVersionString()<<endl;

	///////////
	CmdLineOptionPtr<vector<int> > combGrpOpt = cmdLineOptions.getOpt<vector<int> >(COMB_GROUP_CMD_OPT_STR);
    //vector<int> v = permGrpOpt->value;

	///////////////
    //std::vector<std::vector<char> > * m = readTable(inputFileName);
    std::vector<std::vector<char> > *matrixPtr = readTable(inputFileName);
    // if in combination group mode
    if(combGrpOpt->isAvailable)
    {
    	// first generate combinations according to list
    	CombinationTable removeRowTable;
    	vector<int> numList = combGrpOpt->value;
    	// 3.4.18 -> Justin request for all option #29
    	if(numList.empty())
    	{
    	  int rowNum = 0;
    	  numList.resize(matrixPtr->size());
          std::for_each(numList.begin(),numList.end(),[&rowNum](int &val) { val = rowNum++; });
    	}
    	else
    	{
      	  // handle zero base
		  //!!! since the code expects a C zero based row numbers we need to translate it by subtracting 1
   		  if(!DBHDAConfig::get().isZeroBasedMode())
            std::for_each(numList.begin(),numList.end(),[](int &val) { val--; });
    	}
        // validate input
    	int combSize = cmdLineOptions.getOpt<int>(COMB_GROUP_SIZE_CMD_OPT_STR)->value;
    	if(combSize==0)
    	{
    		cout<<"Error missing combination group size option: "<<COMB_GROUP_SIZE_CMD_OPT_STR <<endl;
    		return 1;
    	}
        int combCount = cmdLineOptions.getOpt<int>(COMB_GROUP_COUNT_CMD_OPT_STR)->value;
    	if(combCount==0)
    	{
    		cout<<"Note missing combination group count option: "<<COMB_GROUP_COUNT_CMD_OPT_STR<<". Using default count"<<endl;
    		//return 1;
    	}
    	//generateCombinations(removeRowTable,numList,combSize,combCount);
    	int res = generateCombinationsV2(removeRowTable,numList,combSize,combCount);
    	cout<<"Using combinations(requested(N:cgs:cgc)-> generated(cgsXcgc)[debug try count] "<<numList.size()<<":"<<combSize<<":"<<combCount<<" -> "<<combSize<<"X"<<removeRowTable.size()<<"["<<res<<"]"<<endl;
    	std::cout << "\n---> RunningWithRowDeletionCombinations\n";
    	withConfidenceCombinations(*matrixPtr,minSupport,tableRequestedColumn,reduceMode,removeRowTable,cout,true);
    	std::cout << "\n---> EOF RunningWithRowDeletionCombinations\n";

    }
    else
    // if we are not in one row delete mode run reduce and FindDBasis once
    if(!oneRowDelMode)
    {
    	Table test(*matrixPtr,minSupport,tableRequestedColumn,reduceMode);
    	std::vector<Implication> implications = test.FindDBasis();
    	test.prettyprintImplications(implications);
    }
    else // in one row delete mode
    {
    	// RunningWithRowDeletions
    	std::cout << "\n---> RunningWithOneRowDeletions\n";
    	// !!! oren change 1.21.18 -> move to new way of doing things
    	//withConfidence(*matrixPtr,minSupport,tableRequestedColumn,reduceMode,cout);
    	CombinationTable removeRowTable(matrixPtr->size());
    	//std::iota(
    	// generate all row numbers
    	int i= 0;
    	for(auto &x: removeRowTable)
    		x.push_back(i++);
    	// call combinations with all rows, ordered
    	try
    	{
    	  withConfidenceCombinations(*matrixPtr,minSupport,tableRequestedColumn,reduceMode,removeRowTable,cout,false);
    	}
    	catch (...)
    	{
    	  // handle crash issue #23 - do nothing for now - can happen when initial column request fails in table constructor
    	  // TODO: move Table constructor exception.
    	  // need to refactor old Table constructor code. reduce and other functions should not happen there.
    	  // not a good idea to abort in the constructor !!!
    	}
    	//endOf RunningWithRowDeletions
    	std::cout << "\n---> EOF RunningWithOneRowDeletions\n";
    }

    delete matrixPtr;

	if(outputFileOpt->isAvailable)
	{
		cout<<"Closing output file"<<endl;
		fclose (stdout);
	}
}
