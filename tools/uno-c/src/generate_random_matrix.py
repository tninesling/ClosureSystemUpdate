#!/usr/bin/env python
#
"""
Author: Ulrich Norbisrath
This creates on stdout a matrix which can be fed to DBasisHDA
"""

import random
import sys
import getopt

__doc__ = """call: generate_random_matrix height width number_of_ones"""

def main():
    
    # parse command line options
    options = sys.argv[1:]
    if len(options) != 3:
        print __doc__
        sys.exit(0)
    height = int(sys.argv[1])
    width = int(sys.argv[2])
    ones = int(sys.argv[3])
    
    dimension = height*width
    assert ones<=dimension, "Too many ones requested. Dimension is smaller."

    mysequence = ["1"]*ones +["0"]*(dimension-ones)
    random.shuffle(mysequence)
    
    # write output
    print height
    print width
    for line in range(height):
        print " ".join(mysequence[line*width:(line+1)*width])
        
        
if __name__ == "__main__":
    main()