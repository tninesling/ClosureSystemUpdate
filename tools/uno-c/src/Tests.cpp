/*
 * Tests.cpp
 *
 *  Created on: Dec 12, 2017
 *      Author: admin
 */

#include <iostream>
#include <vector>
using namespace std;

#include <oslc/lul/TestHarness.h>
#include <oslc/lul/CmdLineOptions.h>
#include <oslc/lul/ConfigOptions.h>
using namespace oslc::lul;


#include "Tests.h"
#include "Table.h"
#include "Util.h"

#include "Configuration.h"





void testReadFile()
{
    std::vector<std::vector<char> > * matrix = readTable("table1.txt");
    printMatrix(matrix);
    delete matrix;
}

void testColumnComparisonTable()
{
    std::vector<std::vector<char> > * m = readTable("tablereducetest3.txt");
    std::vector<std::vector<char> > matrix = *m;
    Table test1(matrix);
    std::vector<std::vector<int> > comparisonTable = test1.getColumnComparisonTable();
    printMatrix(&comparisonTable);
    delete m;
}

void testRowComparisonTable()
{
    std::vector<std::vector<char> > * m = readTable("tablereducetest3.txt");
    std::vector<std::vector<char> > matrix = *m;
    Table test1(matrix);
    std::vector<std::vector<int> > comparisonTable = test1.getRowComparisonTable();
    printMatrix(&comparisonTable);
    delete m;
}

void testReduceTable()
{
    std::vector<std::vector<char> > * m = readTable("tablereducetest2.txt");
    printMatrix(m);

    std::vector<std::vector<char> > matrix = *m;
    Table test(matrix);
    std::vector<std::vector<char> > matrix3 = test.get_matrix();
    printMatrix(&matrix3);
    delete m;
}

void testGetBinaryBasis()
{
    std::vector<std::vector<char> > * m = readTable("tablereducetest2.txt");
    std::vector<std::vector<char> > matrix = *m;
    Table test(matrix);
    std::vector<Implication> implications = test.getFullBinaryBasis();
    printImplications(implications);
    delete m;
}

void testGetNonBinaryBasis()
{
    std::vector<std::vector<char> > * m = readTable("table1.txt");
    std::vector<std::vector<char> > matrix = *m;
    Table test(matrix);
    /*
    for (int i; i < matrix.size(); i++){
        std::cout << "nonbinary basis for" << i;
        std::vector<Implication> implications = test.getNonBinaryBasis(i);
        printImplications(implications);
    }
     */
	timestamp_print();
    std::cout << "nonbinary basis for column" << 1 << "\n";
    std::vector<Implication> implications = test.getNonBinaryBasis(1);
    printImplications(implications);
    delete m;
}

void testGetFullNonBinaryBasis()
{
    std::vector<std::vector<char> > * m = readTable("table1.txt");
    std::vector<std::vector<char> > matrix = *m;
    Table test(matrix);
    std::vector<Implication> implications = test.getFullNonBinaryBasis();
    std::cout << "numImplications " << implications.size();
    printImplications(implications);
    delete m;
}

void testWriteComplementedFamilies()
{
    std::vector<std::vector<char> > * m = readTable("table1.txt");
    std::vector<std::vector<char> > matrix = *m;
    Table test(matrix);
    /*
    for (int i = 0; i < matrix.size(); i++) {
        std::cout << "families for column" << i << "\n";
        test.writeComplementedFamilies(test.getComplementedFamilies(i));
    }
     */
    // ulno test.writeComplementedFamilies(test.getComplementedFamilies(1));
    delete m;
}

void testGetFullSBasis()
{
    std::vector<std::vector<char> > * m = readTable("largetestcase.txt");
    std::vector<std::vector<char> > matrix = *m;
    Table test(matrix);
    std::vector<Implication> implications = test.FindSBasis();
    printImplications(implications);
	timestamp_print();
    std::cout << "original table\n";
    test.prettyprintImplications(implications);
    delete m;
}

void testGetFullDBasis()
{
    std::vector<std::vector<char> > * m = readTable("largetestcase.txt");
    std::vector<std::vector<char> > matrix = *m;
    Table test(matrix);
    std::vector<Implication> implications = test.FindDBasis();
    // std::vector<Implication> pimplications = test.getDNonBinaryBasis(6);
    printImplications(implications);
	timestamp_print();
    std::cout << "original table\n";
    test.prettyprintImplications(implications);
    delete m;
}

void testSubroutine()
{
    std::vector<std::vector<char> > * m = readTable("largetestcase.txt");
    std::vector<std::vector<char> > matrix = *m;
    Table test(matrix);
    test.getNonBinaryBasis(1);
}

void runTests()
{
	RUN_ALL_TESTS;
//	std::cout << "\nTesting readFile()\n";
//	testReadFile();
//	std::cout << "\nTesting Implication\n";
//	testImplication();
//	std::cout << "\nTesting Reduction\n";
//	testReduceTable();
//	std::cout << "\nTesting ColumnComparisonTable\n";
//	testColumnComparisonTable();
//	std::cout << "\nTesting RowComparisonTable\n";
//	testRowComparisonTable();
//	std::cout << "\nTesting reduceTable\n";
//	testReduceTable();
//	std::cout << "\nTesting GetBinaryBasis\n";
//	testGetBinaryBasis();
//	std::cout << "\nTesting WriteFamilies\n";
//	testWriteComplementedFamilies();
//	std::cout << "\nTesting getNonBinaryBasis\n";
//	testGetNonBinaryBasis();
//	std::cout << "\nTesting GetFullNonBinaryBasis\n";
//	testGetFullNonBinaryBasis();
//	std::cout << "\nTesting GetSBasis\n";
//	testGetFullSBasis();
//	std::cout << "\nTesting GetdBasis\n";
//	testGetFullDBasis();
//	//testSubroutine();
}

//
// !!! 2.19.18 - New testing framework ->
//

REGISTER_TEST("DBasisHDA","test 1")
{
	CmdLineOptions cmdLineOptions(CmdLineOptions::getGlblCmdLineOptions());

    //std::vector<std::vector<char> > * m = readTable(inputFileName);
	std::string inputFileName = "data/Large_set_10x22.txt";
	if(cmdLineOptions.getOpt<std::string>(INPUT_FILE_CMD_OPT_STR)->isAvailable)
		inputFileName = cmdLineOptions.getOpt<std::string>(INPUT_FILE_CMD_OPT_STR)->getVal();
    TEST_EXP(!inputFileName.empty());

	//TEST_INFO
	LOG_TEST_MSG("Using input file name: "<<inputFileName);

    std::vector<std::vector<char> > *matrixPtr = readTable(inputFileName);
    TEST_EXP(matrixPtr!=NULL);

    int minSupport = cmdLineOptions.getOpt<int>(MIN_SUPPORT_CMD_OPT_STR)->getVal();
    TEST_EXP(minSupport>=0);

    int tableRequestedColumn = cmdLineOptions.getOpt<int>(COLUMN_CMD_OPT_STR)->getVal();
    TEST_EXP(tableRequestedColumn>=-1 and tableRequestedColumn<=(*matrixPtr)[0].size());

    bool reduceMode = cmdLineOptions.getOpt<bool>(REDUCE_CMD_OPT_STR)->getVal();

	Table test(*matrixPtr,minSupport,tableRequestedColumn,reduceMode);
	std::vector<Implication> implications = test.FindDBasis();
	test.prettyprintImplications(implications);

	// test implication cycles
	Implications imps;
	std::vector<int> lhs1 = {0};
	std::vector<int> rhs1 = {1};
	Implication imp1(lhs1,rhs1,-1);
	imps.add(imp1);
	std::vector<int> lhs2 = {1};
	std::vector<int> rhs2 = {2};
	Implication imp2(lhs2,rhs2,-1);
	Implications testImps;
	testImps.add(imp2);
	std::vector<int> lhs3 = {2};
	std::vector<int> rhs3 = {0};
	Implication imp3(lhs3,rhs3,-1);
	testImps.add(imp3);

	//Implications newImps;
	EquivalenceMap newEquivs;
	imps.handleCycles(testImps,newEquivs);


    return true;
}
