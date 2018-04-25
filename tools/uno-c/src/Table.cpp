//
//  Table.cpp
//
//
//  Created by Joshua A Blumenkopf on 4/16/13.
//
//

#include "shd.h"

#include "Table.h"

#include "Configuration.h"

using namespace std;

// global variable for column
//int Table_requested_column = -1; // by default no column requested
//int Table_requested_column_new = -1; // After reduction

// reduce the Table and return True, if we can continue
// if a column was requested, which got reduced, False will be returned and
// further evaluations should be omitted
bool Table::reduceTable() {

    //initializing map from old to new table
	initReducedToOriginalMap();

    // following loop removes columns with all ones

    for (int i = 0; i < matrix[0].size(); i++) {

        for (int j = 0; j < matrix.size(); j++) {

            if ((matrix[j][i]) != '1') break;

            if (j == (matrix.size() - 1)) {

                for (int k = 0; k < matrix.size(); k++) {

                    matrix[k].erase(matrix[k].begin() + i);

                }

                equivalentColumns[reducedToOriginal[i]] = std::vector<int>(0, 0);

                for (int l = i; l < matrix[0].size(); l++) {

                    reducedToOriginal[l]++;

                }

                reducedToOriginal.pop_back();

                i--;

            }

        }

    }



    // following loop collapses indentical columns together, probably unnecessary as next loop will remove them anyway



    for (int i = 0; i < matrix[0].size() - 1; i++) {

        for (int j = i + 1; j < matrix[0].size(); j++) {

            if (compareColumns(i, j) == 0) {

                

                for (int k = 0; k < matrix.size(); k++) {

                    matrix[k].erase(matrix[k].begin() + j);

                }

                equivalentColumns[reducedToOriginal[j]] = std::vector<int>(1, reducedToOriginal[i]);

                for (int l = j; l < matrix[0].size(); l++) {

                    reducedToOriginal[l]=reducedToOriginal[l+1];

                }

                reducedToOriginal.pop_back();

                j--;

            }

        }

    }



    // following loop removes columns whose closure i\column is not closed

    for (int i = 0; i < matrix[0].size(); i++) {

        std::vector<int> closure; //contains closure i\column in form of 1s

        for (int j = 0; j < matrix[0].size(); j++)//initialize closure\column to contain set\column

        {

            if (j != i) closure.push_back(1);

            else closure.push_back(0);

        }

        for (int j = 0; j < matrix.size(); j++)//removes from closure columns with zero where column i has 1

        {

            if (matrix[j][i] == '1') {

                for (int k = 0; k < matrix[0].size(); k++) {

                    if (matrix[j][k] != '1')closure[k] = 0;

                }

            }

        }

        //see if support of closure=support of column by seeing if it is not less

        bool a = false; //a false means no difference in support has been found

        for (int j = 0; j < matrix.size() && a == false; j++) {

            if (matrix[j][i] != '1') {

                for (int k = 0; k < matrix[0].size(); k++) {

                    if (closure[k] == 1 && matrix[j][k] != '1')break;

                    if (k == matrix[0].size() - 1) a = true;

                }



            }

        }

        if (a == false) {

            for (int k = 0; k < matrix.size(); k++) {

                matrix[k].erase(matrix[k].begin() + i);

            }

            equivalentColumns[reducedToOriginal[i]] = std::vector<int>();

            for (int l = 0; l < closure.size(); l++) {

                if (closure[l] == 1) {

                    equivalentColumns[reducedToOriginal[i]].push_back(reducedToOriginal[l]);

                }

            }

            for (int l = i; l < matrix[0].size(); l++) {

                reducedToOriginal[l]=reducedToOriginal[l+1];

            }

            reducedToOriginal.pop_back();

            i--;

        }



    }

timestamp_print();

    int startPos = !DBHDAConfig::get().isZeroBasedMode();
    //std::cout << "Equivalent columns for reduced table in original table starting from 1\n";
    std::cout << "Equivalent columns for reduced table in original table starting from "<<startPos<<"\n";

    for (int i = 0; i < reducedToOriginal.size(); i++) {
    	// handle zero base
        std::cout << CRN2OUT(reducedToOriginal[i]) << " ";

    }

    std::cout << '\n';

    //std::cout << " Equivalent columns in original table starting from 1, blank means null set ";
    std::cout << " Equivalent columns in original table starting from "<<startPos<<", blank means null set ";

    std::cout << '\n';

    for (std::map<int, std::vector<int> >::iterator it = equivalentColumns.begin(); it != equivalentColumns.end(); ++it) {
    	// handle zero base
        std::cout << CRN2OUT(it->first) << "<=> ";

        for (int i = 0; i < it->second.size(); i++) {
        	// handle zero base
            std::cout << CRN2OUT(it->second[i]) << " ";

        }

        std::cout << '\n';

    }

    //if (Table_requested_column >= 0)
//    if(requestedColumn >= 0)
//    {
//
//    	bool column_found = false;
//
//    	for (int i = 0; i < reducedToOriginal.size(); i++)
//    	{
//    		//if (Table_requested_column == reducedToOriginal[i])
//    		if(requestedColumn == reducedToOriginal[i])
//    		{
//    			column_found = true; // We found it
//    			//Table_requested_column_new = i; // save for later
//    			requestedColumnNew = i; // save for later
//    			break;
//    		}
//    	}
//
//    	if (! column_found ) // We did not find it
//    	{
//    		return false; // stop here and make sure, we don't continue
//    	}
//
//    }
    // 2.2.18 oren change
    if(!handleColumnSelection())
    	return false;

    int used[matrix.size()];  // this array is used to mark what row is already

    for (int t =0; t<matrix.size(); t++)

        used[t]=0;

    // following loop removes rows with all ones

    for (int i = 0; i < matrix.size(); i++) {

        for (int j = 0; j < matrix[0].size(); j++) {

            if ((matrix[i][j]) != '1') break;

            if ((j == (matrix[0].size() - 1))&&(used[j]==0)) {

                

                

                

                for (int t=0; t< matrix[0].size(); t++) matrix[i][t] = 0 ;


            	// handle zero base
                std:: cout << "case 1: The row: " << CRN2OUT(i) << " is deleted "  << " (it has all ones)  \n" ;

                

                used[j] = 1;

                

                

               // matrix.erase(matrix.begin() + i);

            

                //i--;

             

            }



        }

    }

    // following loop collapses identical rows together, probably unnecessary as next loop will remove them anyway



    for (int i = 0; i < matrix.size() - 1; i++) {

        for (int j = i + 1; j < matrix.size(); j++) {

            if ((compareRows(i, j) == 0)&&(used[j]==0)) {

            	// !!! oren change 11.5.17 -> reusing i is not a good idea. Behavior differs depending on old/new ISO standard rules !!!
                //for (unsigned int i=0; i< matrix[0].size(); i++) matrix[j][i] = 0 ;
                for (unsigned int k=0; k< matrix[0].size(); k++) matrix[j][k] = 0 ;


               // std:: cout << "\n";



             //   matrix.erase(matrix.begin() + j);


                // handle zero base
                std:: cout << "case 2: The row: " << CRN2OUT(j) << " is deleted, it is identical to " << CRN2OUT(i) <<" row "  << "\n" ;

                used[j] = 1;

           

            }

        }

    }



    // following loop removes rows whose closure i\row is not closed

    for (int i = 0; i < matrix.size(); i++) {

        std::vector<int> closure; //contains closure i\row in form of 1s

        for (int j = 0; j < matrix.size(); j++)//initialize closure\row to contain set\row

        {

            if (j != i) closure.push_back(1);

            else closure.push_back(0);

        }

        for (int j = 0; j < matrix[0].size(); j++)//removes from closure rows with zero where row i has 1

        {

            if (matrix[i][j] == '1') {

                for (int k = 0; k < matrix.size(); k++) {

                    if (matrix[k][j] != '1') closure[k] = 0;

                }

            }

        }

        //see if support of closure=support of row by seeing if it is not less

        bool a = false; //a false means no difference in support has been found

        for (int j = 0; j < matrix[0].size() && a == false; j++) {

            if (matrix[i][j] != '1') {

                for (int k = 0; k < matrix.size(); k++) {

                    

                    if (closure[k] == 1 && matrix[k][j] != '1') break;

                    if ((k == matrix.size() - 1)) a = true;

                }



            }

        }

        if (a == false && used[i]==0) {

            for (int j=0; j< matrix[0].size(); j++) matrix[i][j] = 0  ;
        	// handle zero base
            std:: cout << "case 3: The row: " << CRN2OUT(i) << " is deleted, and equivalent to ";
            // handle zero base
            for (int t = 0; t< matrix.size(); t++) if (closure[t]==1) std :: cout <<CRN2OUT(t) << "; ";

            std:: cout << "\n" ;

            used[i] = 1;

            

            

         //   matrix.erase(matrix.begin() + i);



           

        }



    }


    return true; // everything was ok

};


bool Table::handleColumnSelection()
{
	// if no column selection, nothing to do, all is well
	if(requestedColumn < 0)
		return true;

	// sanity check, if column exists in original matrix
	if(requestedColumn>=this->matrix_original[0].size())
	{
  	    // handle zero base
		cout<<"Bad column number request: " <<CRN2OUT(requestedColumn) <<" is out of bounds!"<<endl;
		return false;
	}

	// look if requested column can be found in equivalent column map
	auto equivColItr = equivalentColumns.find(requestedColumn);
	// if non found, we can use the same requested column. The original column should not have been reduced.
    if(equivColItr==equivalentColumns.end())
    {
    	// oren bug fix #21 -> reduced column might have moved to another location
    	//requestedColumnNew = requestedColumn;
    	auto reducedPos = std::find(reducedToOriginal.begin(),reducedToOriginal.end(),requestedColumn);
    	// sanity check should never happen !!!
    	if(reducedPos==reducedToOriginal.end())
    	{
      	    // handle zero base
    		cout<<"!!! Error !!! -> Requested column "<<CRN2OUT(requestedColumn) <<" can not be found???"<<endl;
    		return false;
    	}
    	else
    	{
    	  requestedColumnNew = reducedPos - reducedToOriginal.begin();
    	  // handle zero base
    	  // only print message of case 0 if column actually changed position
    	  if(requestedColumnNew!=requestedColumn)
    	    cout<<"Requested column "<<CRN2OUT(requestedColumn) <<" was moved (case 0). Using column number "<<CRN2OUT(requestedColumnNew) <<"."<<endl;
    	  return true;
    	}
    }
    // else
    // we have a reduced column that is equivalent to one ore more other columns
    // There are two cases here according to issue #19:
    // 1. one to one equivalence   => make the switch
    // 2. one to many equivalence* => abort with message + support calculation
    //    * what about equivalence to all? -> case 2 handles that as well
    auto equivColList = equivColItr->second;
    // handle case 1:
    if(equivColList.size()==1)
    {
    	requestedColumnNew = equivColList[0];
  	    // handle zero base
		cout<<"Requested column "<<CRN2OUT(requestedColumn) <<" was reduced (case 1). Using equivalent column "<<CRN2OUT(requestedColumnNew) <<"."<<endl;
    	return true;
    }
    else // handle case 2
    {
  	    // handle zero base
		cout<<"Requested column "<<CRN2OUT(requestedColumn) <<" was reduced (case 2). Multiple equivalent columns exist. Will show support and abort."<<endl;
    	vector<int> lhs;
    	lhs.push_back(requestedColumn);
    	Implication imp(lhs,equivColList,-1);
    	Implications imps;
    	imps.add(imp);
        prettyprintEquivalences(imps);//,false);//true);
        return false;
    }




    //if (Table_requested_column >= 0)
//    if(requestedColumn >= 0)
//    {
//
//    	bool column_found = false;
//
//    	for (int i = 0; i < reducedToOriginal.size(); i++)
//    	{
//    		//if (Table_requested_column == reducedToOriginal[i])
//    		if(requestedColumn == reducedToOriginal[i])
//    		{
//    			column_found = true; // We found it
//    			//Table_requested_column_new = i; // save for later
//    			requestedColumnNew = i; // save for later
//    			break;
//    		}
//    	}
//
//    	if (! column_found ) // We did not find it
//    	{
//    		return false; // stop here and make sure, we don't continue
//    	}
//
//    }


}

int Table::compareColumns(int column1, int column2) {
    int a = 0;
    for (int i = 0; i < matrix.size(); i++) {
        if (matrix[i][column1] == '1' && matrix[i][column2] != '1') {
            if (a == 0 || a == -1)a = -1;
            else {
                a = -2;
                break;
            }
        }
        if (matrix[i][column1] != '1' && matrix[i][column2] == '1') {
            if (a == 0 || a == 1)a = 1;
            else {
                a = -2;
                break;
            }
        }
    }

    return a;
};

int Table::compareRows(int row1, int row2) {
    int a = 0;
    for (int i = 0; i < matrix[0].size(); i++) {
        if (matrix[row1][i] == '1' && matrix[row2][i] != '1') {
            if (a == 0 || a == 1)a = 1;
            else {
                a = -2;
                break;
            }
        }
        if (matrix[row1][i] != '1' && matrix[row2][i] == '1') {
            if (a == 0 || a == -1)a = -1;
            else {
                a = -2;
                break;
            }
        }
    }

    return a;
};

void Table::createUpandDownArrows() {
    for (int i = 0; i < matrix[0].size(); i++) {
        for (int j = 0; j < matrix.size(); j++) {
            if (matrix[j][i] != '1') {
                for (int k = 0; k < matrix.size(); k++) {
                    if (matrix[k][i] != '1') {
                        if (rowComparisonTable[j][k] == -1) {
                            break;

                        }
                    }

                    if (k == matrix.size() - 1)matrix[j][i] = 'u';
                }
            }
        }

    }

    //down arrows
    for (int i = 0; i < matrix.size(); i++) {
        for (int j = 0; j < matrix[0].size(); j++) {
            if (matrix[i][j] != '1') {
                for (int k = 0; k < matrix[0].size(); k++) {
                    if (matrix[i][k] != '1') {
                        if (columnComparisonTable[j][k] == 1) {
                            break;

                        }

                    }

                    if (k == matrix[0].size() - 1) {
                        if (matrix[i][j] == 'u') matrix[i][j] = 'b';
                        else matrix[i][j] = 'd';
                    }
                }
            }
        }

    }

};

void Table::createColumnComparisonTable() {
    int numColumns = matrix[0].size();
    std::vector<int> row(numColumns);
    columnComparisonTable = std::vector<std::vector<int> >(numColumns, row);
    for (int i = 0; i < numColumns; i++) {
        for (int j = 0; j < numColumns; j++) {
            columnComparisonTable[i][j] = compareColumns(i, j);
        }
    }
}

void Table::createRowComparisonTable() {
    int numRows = matrix.size();
    std::vector<int> row(numRows);
    rowComparisonTable = std::vector<std::vector<int> >(numRows, row);
    for (int i = 0; i < numRows; i++) {
        for (int j = 0; j < numRows; j++) {
            rowComparisonTable[i][j] = compareRows(i, j);
        }
    }
}

std::vector<int> Table::getxD(int column) {
    std::vector<int> xD;
    for (int i = 0; i < matrix.size(); i++) {
        if (matrix[i][column] == 'u' || matrix[i][column] == 'b') {
            for (int j = 0; j < matrix[0].size(); j++) {
                if (j != column && (matrix[i][j] == 'd' || matrix[i][j] == 'b')) {
                    if (xD.size() == 0) {
                        xD.push_back(j);
                    } else {
                        for (unsigned int k = 0; k < xD.size(); k++)// making sure not to add duplicates
                        {
                            if (xD[k] == j) {
                                break;
                            }
                            if (k == (xD.size() - 1)) {
                                xD.push_back(j);
                                break;
                            }

                        }
                    }
                }
            }
        }
    }
    return xD;

} //returns xD for a particular column

std::vector<int> Table::getMx(int column) {
    std::vector<int> Mx;
    for (int i = 0; i < matrix.size(); i++) {
        if (matrix[i][column] == 'u' || matrix[i][column] == 'b')Mx.push_back(i);

    }
    return Mx;

}//returns Mx for a particular column


//prints families to the screen. For debugging only

void printFamilies(std::vector<std::vector<int> > families) {
    int numRows = families.size();
    std::cout << "Families ";
    for (int i = 0; i < numRows; i++) {
        int numColumns = families[i].size();
        for (int j = 0; j < numColumns; j++) {
            printf("%3d", families[i][j]);
        }
        std::cout << "\n";
    }
}

std::vector< std::vector<int> > Table::getComplementedFamilies(int column) {
    std::vector<int> Mx = getMx(column);
    std::vector<int> xD = getxD(column);

    std::vector<std::vector<int> > families;
    std::vector<int> temporary;
    for (int i = 0; i < Mx.size(); i++) {
        temporary = xD;
        for (int j = 0; j < temporary.size(); j++) {
            if (matrix[Mx[i]][temporary[j]] == '1') {
                temporary.erase(temporary.begin() + j);
                j--;
            }
        }
        if (temporary.size() > 0)//only adding nonempty families
        {
            families.push_back(temporary);
        }
        /*  for (unsigned int k = 0; k < temporary.size(); k++) {
              std::cout << column << " " << i << " " << Mx[i] << " " << temporary[k] << "\n";

          } for debugging*/
    }

    return families;
}

/*
//Writes the complemented families for a particular column to a table to a file, to be read by the HDA program)
void Table::writeComplementedFamilies(std::vector< std::vector<int> > families) {
    std::ofstream myfile;
    myfile.open("families.dat");
    int numFamilies = families.size();
    for (int i = 0; i < numFamilies; i++) {
        std::vector<int> family = families[i];
        int familySize = family.size();
        for (int j = 0; j < familySize; j++) {
            myfile << family[j] << " ";
        }
        if (i != numFamilies - 1) { //makes sure we don't add an extra newline
            myfile << "\n";
        }
    }
    myfile.close();
}
 */

// reads the dual from the memory buffer,
// and turns it into a set of implications for that column

std::vector<Implication> Table::readDualToImplication(int * buffer, int column) {

    timestamp_print(); std::cout << "readDualToImplication start.\n"; fflush(stdout);

	std::vector<Implication> implications = std::vector<Implication>();

	// TODO: This can be commented out at one point, but let's quickly count how much data we got.
	timestamp_print(); std::cout << "Counting data points.\n"; fflush(stdout);
	int total_data = 0;
	int count_implications = 0;
	int *tmpbuffer = buffer;
	while(*tmpbuffer != INTHUGE - 1)
	{
		if(*tmpbuffer == INTHUGE)
		{
			count_implications++;
			total_data--;
		}
		tmpbuffer++;
		total_data++;
	}
	timestamp_print(); std::cout << "Count finished. Datapoints "<<total_data<<". Sets "<<count_implications<<".\n"; fflush(stdout);

	int counter = 0;
	while( *buffer != INTHUGE - 1 ) { // until end of buffer

		if( counter%1000 == 0)
		{
			timestamp_print(); std::cout << "Read back "<<counter<<" data points.\n"; fflush(stdout);
		}
		counter ++;
        std::vector<int> hittingSet = std::vector<int>();
	    while( *buffer != INTHUGE ) { // until end of line
          hittingSet.push_back(*buffer);
          //  std:: cout <<"*buffer = " << *buffer << " \n";
          buffer ++;
        }
	    buffer ++; // skip end of line

	    // TODO: Oops, the following makes the algo O(n^2). We should remove this from the
	    // outer loop and consider running it only once.
	    // on second thought, this has to be rewritten (but outside the reading loop)

	    //following loop removes lhss with too small supports
        int sup = 0;
        bool blacklist = false;
        for (int i = 0; i < blacklistedHittingSets.size(); i++) {
            if (hittingSet == blacklistedHittingSets[i])//does not get all duplicates because the order could be different
            {
                blacklist = true;

            }
        }
        if (blacklist == false) {
            for (unsigned int i = 0; i < matrix.size(); i++) {
                for (unsigned int j = 0; j < hittingSet.size(); j++) {
                    

                   
                    if (matrix[i][hittingSet[j]] != '1') {
                        break;
                    }
                    if (j == hittingSet.size() - 1) {
                        sup++;
                    }
                  //  std :: cout << sup << " " << "\n" ;
                }

                if (i == matrix.size() - 1&&sup<0) {//minSup
                    blacklistedHittingSets.push_back(hittingSet);
                    // the following can be commented out, if output takes too much time
                   for (unsigned int k = 0; k < hittingSet.size(); k++) {
                 	    // handle zero base
                        std::cout << CRN2OUT(reducedToOriginal[hittingSet[k]]) << " "; //in original table starting from zero
                    }
                    std::cout << "too small support = " << sup << "\n";
                }
            }
            if (sup >= 0) { //minSup                
                std::vector<int> rhs = std::vector<int>();
                rhs.push_back(column);
            	// !!! Oren 1.8.17 -> refactor design. old code design makes artificial separation between implications and their support
                //implicationSupport.push_back(sup);
                Implication implication = Implication(hittingSet, rhs, sup);
                implications.push_back(implication);
            }
        }

    }
    timestamp_print(); std::cout << "readDualToImplication finish.\n"; fflush(stdout);
	return implications;
}

int * Table::runShd(std::vector< std::vector<int> > families) {
    std::vector<int> v = std::vector<int>();
    int numFamilies = families.size();
    for (int i = 0; i < numFamilies; i++) {
        std::vector<int> family = families[i];
        int familySize = family.size();
        for (int j = 0; j < familySize; j++) {
//        	printf("Transferring to SHD: %d\n", family[j]); // debug
            v.push_back(family[j]);
        }
//        printf("Transferring to SHD: %d\n", INTHUGE); // debug
        v.push_back(INTHUGE); // eol
    }
    v.push_back ( INTHUGE - 1 ); //eof
    // now transform vector into memory object
    int* a = new int[v.size()];
    // copy content
    for( unsigned int i=0; i<v.size(); i++)
    	a[i] = v[i];
    __load_from_memory_org__ = a;
    timestamp_print(); std::cout << "Calling shd.\n"; fflush(stdout);
    EXECSUB(SHD_main, 0, exit, "shd _09 void void", 0); // call shd
    timestamp_print(); std::cout << "Returned from shd.\n"; fflush(stdout);
    int * buf = (int *) __write_to_memory_org__;
    /*// debug
    int i=0 ,*debug = buf;
    while (*debug != INTHUGE - 1) {
        printf("Debugresult from SHD: %d\n", *debug);
        debug++;
        i++;
    }
    printf("This implication has returned %d entries.\n", i);*/
    delete [] a; // free memory again as we have the result
    return buf;
}
/* obsolete
std::vector<Implication> Table::getImplicationsFromDual(int column, int* buffer) {
    std::vector<Implication> implications = std::vector<Implication>();
    std::vector<std::vector<int> > dual = std::vector<std::vector<int> >();
    std::vector<int> line = std::vector<int>();

    while (*buffer != INTHUGE - 1) { //While we haven't reached eof
        if (*buffer == INTHUGE) { //if we're at a newline demarcation
            std::cout << "\n"; //for testing only
            dual.push_back(line); //store the current line in the dual
            line = std::vector<int>(); //line is a new vector
        } else { //in the middle of a line
            line.push_back(*buffer); //add the number to the line
            std::cout << *buffer << " "; //for testing only
        }
        buffer ++;
    }
    return implications;
} */

std::vector<Implication> Table::getNonBinaryBasis(int column) {

    std::vector<Implication> implications = std::vector<Implication>();
    std::vector< std::vector<int> > families = getComplementedFamilies(column);

    // now we need to run hypergraph dualization
    if (families.size() != 0) {

        
        //writeComplementedFamilies(families);
        //system("shd _09 families.dat dual.dat");
        //implications = readDualToImplication(column);


        //To be implemented when subroutine implementation is complete: TODO - ulno
        int * buffer = runShd(families);
        implications = readDualToImplication( buffer, column );
        //End    

    }

    //end of temporary
    return implications;
}

std::vector<Implication> Table::getFullNonBinaryBasis() {
    std::vector<Implication> allnonbinaryImplications;
    for (int i = 0; i < matrix[0].size(); i++) {
        std::vector<Implication> nonbinarybasisi = getNonBinaryBasis(i);
        allnonbinaryImplications.insert(allnonbinaryImplications.end(), nonbinarybasisi.begin(), nonbinarybasisi.end());
    }
    return allnonbinaryImplications;
}

void Table::reduce(int column, std::vector<Implication>& implications) {
	timestamp_print();
	std::cout << "Implication size: " << implications.size()
			<< " Starting reduction.\n";
	fflush(stdout); //debug
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// After commenting reduction support is computed correctly
	for (unsigned int i = 0; i < implications.size(); i++) {
		if (i % (implications.size() / 1000 + 1))    // debug every per milli
				{
			std::cout << " Working on reduction. Percentage:"
					<< i / (implications.size() / 100) << "\n";
			fflush(stdout); //debug
		}
		std::vector<int> cover1 = implications[i].getlhs();
		for (unsigned int j = 0; j < implications.size(); j++) {
			if (i != j) {
				std::vector<int> cover2 = implications[j].getlhs();
				bool a = true;
				for (unsigned int l = 0; l < cover2.size(); l++) {
					bool b = false;
					for (unsigned int k = 0; k < cover1.size(); k++) {
						if ((cover1[k] == cover2[l])
								|| columnComparisonTable[cover1[k]][cover2[l]]
										== 1) {
							//    if(cover1[k]!=cover2[l]){
							//     std::cout<<cover2[l]<<"is implied by"<<cover1[k]<<" ";
							//     }
							b = true;
							break;
						}
					}
					if (b == false) {
						a = false;
						break;
					}
				}
				if (a == true) {
					diffsbasisdbasis++;
					for (unsigned int n = 0; n < cover2.size(); n++) {
			    	    // handle zero base
						std::cout << CRN2OUT(reducedToOriginal[cover2[n]]) << " ";
					}
					std::cout << "<< reduces ";
					for (unsigned int m = 0; m < cover1.size(); m++) {
			    	    // handle zero base
						std::cout << CRN2OUT(reducedToOriginal[cover1[m]]) << " ";
					}
					// handle zero base
					std::cout << "for column " << CRN2OUT(reducedToOriginal[column])
							<< "\n";
					implications.erase(implications.begin() + i);
	            	// !!! Oren 1.8.17 -> refactor design. old code design makes artificial seperation between implications and their support
					//implicationSupport.erase(implicationSupport.begin() + i);
					i--;
					break;
				}
			}
		}
	}
	timestamp_print();
	std::cout << "Reduction finished.\n";
	fflush(stdout);
}

std::vector<Implication> Table::getDNonBinaryBasis(int column) {
    std::vector<Implication> implications = std::vector<Implication>();
    std::vector< std::vector<int> > families = getComplementedFamilies(column);
    // now we need to run hypergraph dualization

    if (families.size() != 0) {

        //Note: the following code is temporary, while we don't have access to call the function directly
        //writeComplementedFamilies(families);
        //system("shd _09 families.dat dual.dat");
        //implications = readDualToImplication(column);
        //end of temporary

        
        //To be implemented when subroutine implementation is complete
        int * buffer = runShd(families);
        implications = readDualToImplication( buffer, column );

        // if reduction is enabled
        if(this->useReduction)
		  reduce(column, implications);
    }

    return implications;
}

std::vector<Implication> Table::getDFullNonBinaryBasis() {
	timestamp_print(); std::cout << "Table::getDFullNonBinaryBasis FindDBasis \n"; fflush(stdout);

	std::vector<Implication> allnonbinaryImplications;
    //if (Table_requested_column_new >= 0)
    if (requestedColumnNew >= 0)
    {
    	int i = requestedColumnNew;
		std::vector<Implication> nonbinarybasisi = getDNonBinaryBasis(i);
		allnonbinaryImplications.insert(allnonbinaryImplications.end(), nonbinarybasisi.begin(), nonbinarybasisi.end());
    }
    else
    {// No, not given so we want all
    	for (int i = 0; i < matrix[0].size(); i++) {
    		std::vector<Implication> nonbinarybasisi = getDNonBinaryBasis(i);
    		allnonbinaryImplications.insert(allnonbinaryImplications.end(), nonbinarybasisi.begin(), nonbinarybasisi.end());
    	}
    }

	timestamp_print();
    std::cout << "diff s d" << diffsbasisdbasis << "\n";
	timestamp_print(); std::cout << "Table::getDFullNonBinaryBasis FindDBasis done\n"; fflush(stdout);
    return allnonbinaryImplications;
}
//if column b->a, that means that b has fewer ones than a, or a < b.

std::vector<Implication> Table::getBinaryBasis(int column) {
    std::vector<Implication> implications = std::vector<Implication>();
    int numColumns = columnComparisonTable.size();
    for (int i = 0; i < numColumns; i++) {
        if (columnComparisonTable[column][i] == -1) {
            std::vector<int> rhs = std::vector<int>();
            rhs.push_back(column);
            std::vector<int> lhs = std::vector<int>();
            lhs.push_back(i);
            int sup=0;
            for (int j=0; j<matrix.size(); j++) //finding support of lhs
            { //  std :: cout << j << " " << i << " "<< matrix[j][i] << "\n";  ////for debugging
                if (matrix[j][i]=='1') {
                    sup++;
                }
              //  std :: cout << sup << "  " << "\n";
            }
        	// !!! Oren 1.8.17 -> refactor design. old code design makes artificial seperation between implications and their support
            //implicationSupport.push_back(sup);
            Implication implication = Implication(lhs, rhs, sup);
            implications.push_back(implication);
        }
    }
    return implications;
}

std::vector<Implication> Table::getFullBinaryBasis() {
	timestamp_print(); std::cout << "Table::getFullBinaryBasis \n"; fflush(stdout);
    std::vector<Implication> allImplications = std::vector<Implication>();
    int numColumns = columnComparisonTable[0].size();
    for (int i = 0; i < numColumns; i++) {
        std::vector<Implication> implications = getBinaryBasis(i);
        allImplications.insert(allImplications.end(), implications.begin(), implications.end());
    }
	timestamp_print(); std::cout << "Table::getFullBinaryBasis done.\n"; fflush(stdout);
    return allImplications;
}

void printImplications(std::vector<Implication> implications) {
    for (int i = 0; i < implications.size(); i++) {
    	timestamp_print(); std::cout << implications[i].toString() << " printImplications \n"; fflush(stdout);
    }
}

void Table::writeOutput(std::string outputFileName) {
    //TODO (This would be instead of using cout)
}

Implication Table::mapImplication(Implication implication) {
    //ToDo
	exit(1);
	//return Implication;
}

// Not in use after version 0.0.4b
//vector< vector<int> > Table::getNewResults(vector< vector<int> > allResults, vector<Implication> implications){
//    vector< vector<int> > ans;
//
//    for (int i = 0; i < implications.size(); i++) {
//        vector<int> results;
//    	// !!! Oren 1.8.17 -> refactor design. old code design makes artificial seperation between implications and their support
//        // if(implicationSupport[i]>=minSup ||
//        if(implications[i].getSupport()>=minSup ||
//           implications[i].getlhs().size() == 1 ) // still print binary part
//        {
//            std::vector<int> lhs = implications[i].getlhs();
//            for (unsigned int j = 0; j < lhs.size(); j++) {
//                results.push_back(reducedToOriginal[lhs[j]] + 1);
//               //if (implications[i].getlhs().size()!=1) a[reducedToOriginal[lhs[j]]]+=implicationSupport[i];
//            }
//            results.push_back(-1);// stands for ->
//            std::vector<int> rhs = implications[i].getrhs();
//            for (unsigned int j = 0; j < rhs.size(); j++) {
//                results.push_back(reducedToOriginal[rhs[j]] + 1);
//            }
//        	// !!! Oren 1.8.17 -> refactor design. old code design makes artificial seperation between implications and their support
//            //results.push_back(implicationSupport[i]);//last value is support
//            results.push_back(implications[i].getSupport());//last value is support
//        }
//
//        //check if new rule
//        bool found_same = false;
//
//        for (int s=0;s<allResults.size();s++)
//            if (results.size() == allResults[s].size()){
//                found_same = true;
//                for (int t=0;t < results.size()-1;t++)
//                    if (allResults[s][t] != results[t]) found_same = false;
//                if (found_same) break;
//            }
//
//        // !!! 10.28.17 oren crash/bug fix: only add result if result actually found i.e. results vector not empty
//        //if (!found_same) ans.push_back(results);
//        if (!found_same && !results.empty()) ans.push_back(results);
//        //endOf check if new rule
//
//    }
//
//    return ans;
//}

void Table::prettyprintImplications(std::vector<Implication> &implications, bool reduceToOrg)
{
    int columns_number = matrix_original[0].size();
    double total_support[columns_number];
    std::fill_n(total_support, columns_number, 0);

    for (int i = 0; i < implications.size(); i++) {
    	// !!! Oren 1.8.17 -> refactor design. old code design makes artificial seperation between implications and their support
    	//if(implicationSupport[i]>=minSup || implications[i].getlhs().size() == 1 ) // still print binary part
    	if(implications[i].getSupport()>=minSup || implications[i].getlhs().size() == 1 ) // still print binary part
    	{
            std::vector<int> a;            
        	timestamp_print();
			std::vector<int> lhs = implications[i].getlhs();
			std::cout << i << ";";
			for (unsigned int j = 0; j < lhs.size(); j++)
			{
				int index = (reduceToOrg) ? reducedToOriginal[lhs[j]] : lhs[j];
				// handle zero base
				std::cout << CRN2OUT(index) << " ";
                a.push_back(index);
			}
			std::cout << ("-> ");
			std::vector<int> rhs = implications[i].getrhs();
			for (unsigned int j = 0; j < rhs.size(); j++)
			{
				int index = (reduceToOrg) ? reducedToOriginal[rhs[j]] : rhs[j];
				// handle zero base
				std::cout << CRN2OUT(index) << " ";
                a.push_back(index);
			}
			//std::cout<<"; support = "<<implicationSupport[i] << "\n";			

            //Real Support
            int support = 0;
            std::vector<int> rows;
            for (int row=0;row<matrix_original.size();row++){
                bool all = true;
                for (int j=0;j<a.size();j++)
                    if (matrix_original[row][a[j]] == 0){
                        all = false;
                        break;
                    }
                if (all == true){
                    support++;    
                    // handle zero base
                    rows.push_back(CRN2OUT(row));
                }
            }
        	// !!! Oren 1.8.17 -> refactor design. old code design makes artificial seperation between implications and their support
            //std::cout<<"; support = "<<implicationSupport[i] <<"; RealSupport = "<<support << "; rows = ";
            std::cout<<"; support = "<<implications[i].getSupport() <<"; RealSupport = "<<support << "; rows = ";
            for (int j=0;j<rows.size();j++) std::cout<<rows[j]<<", ";
            std::cout<<"\n";            
            //Total Support Update                       
            //if (a[a.size()-1] == Table_requested_column) {
            if (a[a.size()-1] == requestedColumn) {
            // !!! Oren bug fix 1/4/18 -> total support should not include rhs !!!
            // for (int j=0;j<a.size();j++)
            for (int j=0;j<a.size()-1;j++)
                total_support[a[j]] += 1.0 * support / lhs.size();        
            }
            //endOf Real Support
    	}
    }

    //Print Total Support
    std::cout<<"Total Support(column->it's total support)"<<"\n";
    for (int i=0;i<columns_number;i++)
    	// handle zero base
        std::cout<<CRN2OUT(i)<<" -> "<<total_support[i]<<"\n";
    //endOf Print Total Support
}

// !!! oren change  2.2.18 -> temporary and quick hack to print equivalences (issue #19),
//     we need to refactor this stuff as soon as we decide on how we want to incorporate the new treatment of equivalences into the existing code base.
void Table::prettyprintEquivalences(std::vector<Implication> &implications)
{
    int columns_number = matrix_original[0].size();

    for (int i = 0; i < implications.size(); i++)
    {
            std::vector<int> a;
        	timestamp_print();
			std::vector<int> lhs = implications[i].getlhs();
			std::cout << i << ";";
			for (unsigned int j = 0; j < lhs.size(); j++)
			{
				int index = lhs[j];
				// handle zero base
				std::cout << CRN2OUT(index) << " ";
                a.push_back(index);
			}
			std::cout << ("<-> ");
			std::vector<int> rhs = implications[i].getrhs();
			for (unsigned int j = 0; j < rhs.size(); j++)
			{
				int index = rhs[j];
				// handle zero base
				std::cout << CRN2OUT(index) << " ";
                a.push_back(index);
			}

			//
            // Calculate real support on a potentially partially zeroed table *
			// * Note: if this table has one or more zeroed rows (ORD/MRD mode),
			//         support will be calculated correctly since a removed row will not contribute to the support summation process!
			//
            int support = 0;
            std::vector<int> rows;
            for (int row=0;row<matrix_original.size();row++){
                bool all = true;
                for (int j=0;j<a.size();j++)
                    if (matrix_original[row][a[j]] == 0){
                        all = false;
                        break;
                    }
                if (all == true){
                    support++;
                    // handle zero base
                    rows.push_back(CRN2OUT(row));
                }
            }
            std::cout<<"; support = "<<support << "; rows = ";
            for (int j=0;j<rows.size();j++) std::cout<<rows[j]<<", ";
            std::cout<<"\n";
    }
}

