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

    l = ""
    while l.strip() == "":
        l = f.readline()

    height = int(l)

    l = ""
    while l.strip() == "":
        l = f.readline()
    width = int(l)
    
    lines=[]
    new_line = []
    for line in f:
        l = line.strip().split(" ")
        for s in l:
            new_line.append(s)
            if len(new_line) == width:
                lines.append(new_line)
                new_line = []
        
    
    # write output
#    print height
    print len(lines)
    print width
    for l in lines:
        print " ".join(l)
        
        
if __name__ == "__main__":
    main()