
//
//  Table.h
//
//
//  Created by Joshua A Blumenkopf on 4/16/13.
//
//
#define _FILE2_LOAD_FROM_MEMORY_
#include <iostream>
#include <string>
#include <fstream>
#include <sstream>
#include <vector>
#include <map>
#include <cstdlib>
//#include "shd31/shd.c"
#include "CommonTypes.h"
#include "Implication.h"
#include "timestamp.h"

using namespace std;

#ifndef ____Table__
#define ____Table__

// Default value if no value set on command line etc. Set this to 1, if you want to see the full basis
#define SUPPORT_LOWER_BOUND_DEFAULT 1

// global variable for column
//extern int Table_requested_column;

class Table {
private:
    int diffsbasisdbasis;
    std::vector<std::vector<char> > matrix;
    std::vector<std::vector<int> > matrix_original;
    std::vector<std::vector<int> > columnComparisonTable; /*an n*n matrix (n is the number of columns in the reduced
                                                           * table) whose rows and columns represent
                                                           *  the columns of the reduced table. columnComparisonTable[i][j] gives 
                                                           you the result of compareColumns(i,j). This technique is used so we don't have to 
                                                           perform a search through the table n^2 times each time we want to know which columns
                                                           are subsets of other columns*/
    std::vector<std::vector<int> > rowComparisonTable; /*
                                                        * Comparison table for rows, similar to columnComparisonTable
                                                        */
    std::vector<Implication> completeImplications;
    std::vector<int> reducedToOriginal;
    // !!! oren 2.23.18 -> refactor (refs #16)
    //std::map<int, std::vector<int> > equivalentColumns;
    EquivalenceMap equivalentColumns;
    std::vector<std::vector<int> > blacklistedHittingSets;//lhss with too small supports
    int minSup;//user parameter for minimal support for lhs of implications
    // !!! Oren 1.8.17 -> refactor design. old code design makes artificial seperation between implications and their support
    //                    problematic for new parralel extensions and in general a problematic design choice as can be seen in current Table interface
    //std::vector<int> implicationSupport;//gives the magnitude of the support for the ith implication
    int compareColumns(int column1, int column2); //more ones is smaller column; column numbering starts from 0;Returns 1 if column1 is greater, -1 if column2  is greater, 0 if equal, -2 if incomparable
    int compareRows(int row1, int row2); // more ones is larger row; row numbering starts from 0
    bool reduceTable();
    void createColumnComparisonTable();
    void createRowComparisonTable();
    void createUpandDownArrows();

	void initReducedToOriginalMap() {
		//initializing map from old to new table
		for (int i = 0; i < matrix[0].size(); i++) {
			reducedToOriginal.push_back(i);
		}
	}

public:
	void reduce(int column, std::vector<Implication>& implications);

public:

    Table(std::vector<std::vector<char> > inputtable, int minSupport_=SUPPORT_LOWER_BOUND_DEFAULT, int requestedColumn_= -1, bool useReduction_ = false, bool useInternalReduce_ = true)
    {
        // variable for column reduction
        requestedColumn = requestedColumn_; // by default no column requested
        requestedColumnNew = -1; // After reduction
        // use reduction
        useReduction= useReduction_;
        useInternalReduce = useInternalReduce_ ;

        matrix = inputtable;        
        //
        for (int i=0;i<matrix.size();i++) {
            std::vector<int> a;        
            for (int j=0;j<matrix[i].size();j++)
                if (matrix[i][j] == '0') a.push_back(0);
                else if (matrix[i][j] == '1') a.push_back(1);
            matrix_original.push_back(a);            
        }
        //

    	timestamp_print();
    	// !!! 1.25.18 -> begin hack to allow aggregation
        if (useInternalReduce)
        {
            std::cout << "Reducing table.\n";
        	if(!reduceTable())
        	{
        		std::cerr<< "The requested column was reduced.\n";
        		fflush(stdout); // make sure data gets out
        		// 1.9.17 oren -> fix error handling. Should not abort program on error. give chance to program logic to recover.
        		//exit(EXIT_FAILURE);
        		//TODO: throw something usable
        		throw 1001;
        	}
        }
        else // if we are not reducing we need to init reduced to original table
        {
            std::cout << "Not reducing table.\n";
            //initializing map from old to new table
			initReducedToOriginalMap();
        }
    	// !!! 1.25.18 -> eof hack to allow aggregation
    	timestamp_print();
        std::cout << "Creating row comparison table.\n";
        createRowComparisonTable();
    	timestamp_print();
        std::cout << "Creating column comparison table.\n";
        createColumnComparisonTable();
    	timestamp_print();
        std::cout << "Creating up and down arrows.\n";
        createUpandDownArrows();
        minSup=minSupport_;//SUPPORT_LOWER_BOUND
        diffsbasisdbasis=0;
        fflush(stdout);
    };

    void setminSup(int min) {minSup= min;}

    std::vector<Implication> FindSBasis() {
        completeImplications = getFullBinaryBasis();
        std::vector<Implication> nonbinary = getFullNonBinaryBasis();
        completeImplications.insert(completeImplications.end(), nonbinary.begin(), nonbinary.end());
        return completeImplications;
    };
    
    std::vector<Implication> FindDBasis() {
    	timestamp_print(); std::cout << "std::vector<Implication> FindDBasis \n"; fflush(stdout);
        completeImplications = getFullBinaryBasis();
        std::vector<Implication> nonbinary = getDFullNonBinaryBasis();
        completeImplications.insert(completeImplications.end(), nonbinary.begin(), nonbinary.end());
    	timestamp_print(); std::cout << "std::vector<Implication> FindDBasis done. \n"; fflush(stdout);
        return completeImplications;
    };

    std::vector<Implication> getBinaryBasis(int column); //Gets the binary basis for a particular column

    std::vector<Implication> getFullBinaryBasis(); //Gets the binary basis for the entire table

    std::vector<int> getxD(int column); //returns xD for a particular column

    std::vector<int> getMx(int column); //returns Mx for a particular column

    std::vector<std::vector<int> >getComplementedFamilies(int column);
    
    int* runShd(std::vector< std::vector<int> > families);
    
    std::vector<Implication> getImplicationsFromDual( int column, int * buffer );
    
    void writeComplementedFamilies(std::vector< std::vector<int> > families);

    std::vector<Implication> readDualToImplication(int * buffer, int column);

    std::vector<Implication> getNonBinaryBasis(int column); //Gets the nonbinary basis for a particular column

    std::vector<Implication> getFullNonBinaryBasis(); //Gets the nonbinary basis for the entire table
    
    std::vector<Implication> getDNonBinaryBasis(int column); //Gets the << reduced D nonbinary basis for a particular column
    
    std::vector<Implication> getDFullNonBinaryBasis(); //Gets the << reduced D nonbinary basis for the entire table
    
    Implication mapImplication (Implication implication);
    //ToDo
   
    void writeOutput(std::string outputFileName);


    std::vector<std::vector<char> > get_matrix() {
        return matrix;
    };

    std::vector<std::vector<int> > getColumnComparisonTable() {
        return columnComparisonTable;
    };

    std::vector<std::vector<int> > getRowComparisonTable() {
        return rowComparisonTable;
    };
    
    vector< vector<int> > getNewResults(vector< vector<int> > allResults, vector<Implication> currImplications);

    //void prettyprintImplications(std::vector<Implication> implications);//returns implications starting from 1 using the original table
    // !!! oren change 1.21.18 -> improve efficiency (pass by ref) and robustnass (optional reduce to original)
    void prettyprintImplications(std::vector<Implication> &implications, bool reduceToOrg=true);

    void prettyprintEquivalences(std::vector<Implication> &implications);

    void mapToOriginal(Implications &imps) { imps.mapTo(reducedToOriginal); }

    // !!! oren change 2.2.18 -> handle col selection better
    bool handleColumnSelection();

    // !!! add missing destructor to investigate mem issues
    ~Table()
    {

    }

    //  !!! config values - step one change old global to member variables
    private:
    int requestedColumn;     // Column requested by user
    int requestedColumnNew;  // Column number after reduction - reduction and equivalence can change the originaly requested number
    bool useReduction;
    // !!!! this flag controls whether we should reduce the internal matrix, hack needed for rule aggregations from multiple tables
    bool useInternalReduce;
    
    public:
      void setRequestedColumn(int col) { requestedColumn = col; }
      void setReduction(bool active)   { useReduction = active; }
};

void printImplications(std::vector<Implication> implications);//returns implications starting from 0 using reduced table


#endif /* defined(____Table__) */
