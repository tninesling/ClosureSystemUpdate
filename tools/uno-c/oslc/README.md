OSLC
=========

 Light C++ Template Based Utility Library 
 
Dependencies 
------------------
 
 - C++11
 
 - STL

 - POSIX

Build instructions
------------------

1. To build in debug mode use the following command:
   make DEBUG=1
   
2. To build in release mode (optimizations on, difficult to debug, but produces faster code) use the following command:
   make DEBUG=0
   
3. To clean object files from previous build add clean to one of the previous commands. 
   for example, to clean debug build folder:
   make DEBUG=1 clean