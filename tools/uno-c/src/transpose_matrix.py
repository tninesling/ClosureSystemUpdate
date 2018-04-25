#!/usr/bin/env python
#
"""
Author: Ulrich Norbisrath
Transpose a matrix file from DBasisHD
"""

import sys

__doc__ = """call: transpose_matrix inputfile"""

def main():
    
    # parse command line options
    options = sys.argv[1:]
    if len(options) != 1:
        print __doc__
        sys.exit(0)

    f = open(sys.argv[1])

    height = int(f.readline())
    width = int(f.readline())
    
    lines=[]
    for line in range(height):
        new_line=f.readline().strip().split(" ")
        lines.append(new_line)
        
    
    # write output
    print width
    print height
    for x in range(width):
        new_line=[]
        for y in range(height):
            new_line.append(lines[y][x])
        print " ".join(new_line)
        
        
if __name__ == "__main__":
    main()