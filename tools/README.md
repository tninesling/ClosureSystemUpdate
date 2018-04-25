# DBasisHDA #
### Short Description ###
Algorithm produces implicational equivalent of a closure system defined on the columns of a binary table. Input is given by a table of size nxm and a string of zeros and ones of length mn, where n is interpreted as the number of rows and m as the number columns of input table. Output is the set of formulas uv…z-> b, where u,v,…,z and b are markings of columns, such that in every row of the given table,  ones in all columns u,v,…,z imply one in column b. Particular set of implications obtained in this algorithm is the D-basis of a closure system, defined in References [1]. Algorithm behind the code is described in [2]. Algorithm employs hypergraph dualization code implementation described in [3]. New phase of development aims to compute the association rules of high confidence by removing particular rows of the table.

### References ###

[1] Adaricheva K., Nation J.B. and Rand R., Ordered direct implicational basis of a finite closure system, Discrete Applied Mathematics, 161 (2013), pp. 707-723.

[2] Adaricheva K. and Nation J.B., Discovery of the D-basis in binary tables based on hypergraph dualization, Theoretical Computer Science, v.658 (2017), Part B, 307-315.

[3] Murakami K. and Uno T., Efficient algorithms for dualizing large scale hypergraphs, Disc. Appl. Math. 170 (2014), 83--94.

[4] Fredman M. and Khachiyan L., On the complexity of dualization of monotone disjunctive normal forms, J. Algorithms 21 (1996), 618--628.

### Details/complexity of the algorithm ###

Algorithm starts from table reduction that does not alter concept lattice corresponding to original table. Then the D-relation is computed for the columns of the reduced table. Based on the D-relation, a hypergraph is produced for each specified column. This hypergraph is dualized in SHD module written by Murakami-Uno. The output of dualized hypergraph provides the antecedents of the implications with the specified column in conclusion. Algorithm is repeated for each column of the table, and combined outputs give the total D-basis. Only a sector of the D-basis for the specified column may be produced. The complexity of the part of the algorithm leading to formation of a column specific hypergraph is quadratic in the size of the table. Hypergraph dualization is of at most sub-exponential complexity in the size of the hypergraph and its dual, based on result in References [4].
 Code details/authors
Initial implementation is done in C++ by undergraduates J. Blumenkopf and T. Moldwin (Yeshiva College, New York) in 2013, with assistance of T. Uno. SHD module is written in C by T. Uno and K. Murakami. Further development and debugging in 2013-16 is done by Dr. M. Sterling, A. Sailanbayev and A. Sharafudinov from Nazarbayev University (Kazakhstan). Github was maintained by T. Moldwin and A. Amanbekkyzy. Since 2017 the development and testing is done by Dr. O. Segal and J. Cabot-Miller. 
Build instructions
Run make inside this folder (DbasisSource/uno-c). Upon successful build this will produce: ./Debug/DBasisHDA or ./Release/DBasisHDA depending on debug/release mode build chosen (see notes below).

### Notes: ###

1.	To build in debug mode use the following command: make DEBUG=1
2.	To build in release mode (optimizations on, difficult to debug, but produces faster code) use the following command: make DEBUG=0
3.	To clean object files from previous build add clean to one of the previous commands. for example, to clean debug build folder: make DEBUG=1 clean

### Run instructions ###

To run the executable use: ./Debug/DBasisHDA or ./Release/DBasisHDA with the command line options specified below.

### Command line options ###

Usage is DBasisHDA <-i inputFile> [<-o outputFile> [<-c column>]

Options:

-c A column to work on

-i Input file path

-mins Min support to work on

-o Output file path

-ord Run in one row deletion mode

-r Run in reduce mode

-test Run test harness

### Usage Examples ###

Run with input file data/Large_set_10x22.txt and min support 1:
./Debug/DBasisHDA -mins 1 -i data/Large_set_10x22.txt

Run with input file data/Large_set_10x22.txt, min support 1 and reduction enabled:
./Debug/DBasisHDA -mins 1 -i data/Large_set_10x22.txt -r

Run with input file data/Large_set_10x22.txt, min support 1 and focus on coloumn 1:
./Debug/DBasisHDA -c 1 -mins 3 -i data/Large_set_10x22.txt

Run with input file data/Large_set_10x22.txt, output file Large_set_10x22-1.out and min support 1:
./Debug/DBasisHDA -mins 1 -i data/Large_set_10x22.txt -o Large_set_10x22-1.out

### More information ###

* [C++ version](uno-c)


