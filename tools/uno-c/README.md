DBasisHDA
=========

Build instructions
------------------
Run make inside this folder (DbasisSource/uno-c).
Upon successful build this will produce: ./Debug/DBasisHDA or ./Release/DBasisHDA depending on debug/release mode build chosen (see notes below). 

Notes:

1. To build in debug mode use the following command:
   make DEBUG=1
   
2. To build in release mode (optimizations on, difficult to debug, but produces faster code) use the following command:
   make DEBUG=0
   
3. To clean object files from previous build add clean to one of the previous commands. 
   for example, to clean debug build folder:
   make DEBUG=1 clean
   

Run instructions
----------------

To run the executable use: ./Debug/DBasisHDA or ./Release/DBasisHDA with the command line options specified below.

Command line options
--------------------

Usage is DBasisHDA <-i inputFile> [<-o outputFile> [<-c column>]

Options:

  -c		A column to work on
  
  -i		Input file path
  
  -mins		Min support to work on
  
  -o		Output file path
  
  -ord		Run in one row deletion mode
  
  -r		Run in reduce mode
  
  -test		Run test harness

  
Usage Examples
----------------

Run with input file data/Large_set_10x22.txt and min support 1:

./Debug/DBasisHDA -mins 1 -i data/Large_set_10x22.txt

Run with input file data/Large_set_10x22.txt, min support 1 and reduction enabled:

./Debug/DBasisHDA -mins 1 -i data/Large_set_10x22.txt -r

Run with input file data/Large_set_10x22.txt, min support 1 and focus on coloumn 1:

./Debug/DBasisHDA -c 1 -mins 3 -i data/Large_set_10x22.txt

Run with input file data/Large_set_10x22.txt, output file Large_set_10x22-1.out and min support 1:

./Debug/DBasisHDA -mins 1 -i data/Large_set_10x22.txt -o Large_set_10x22-1.out


