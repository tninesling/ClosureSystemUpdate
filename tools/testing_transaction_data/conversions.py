"""This file contains several functions for converting data
to different formats for the input to dbasis, fortran and
microsoft association rules programs"""

"""Author: Adina Amanbekkyzy
   Date: 03/05/2014"""


"""Transforms data from binary representation
to decimal (a fortran input file to an intermediate file for Microsoft AR)"""

def binary_decimal_trans(file_in, file_out):
    matrix = []
    with open(file_out, 'wb') as dec_file:
        stream = open(file_in)
        binary = stream.readlines()
        rows = int(binary[0])
        columns = int(binary[1])        
        for i in range(2, len(binary)):
            binary_mat = binary[i].split()
            for j in range(0, (columns-2)/2):
                if binary_mat[j] == '1':
                    matrix.append(j + 1)
            for j in range((columns-2)/2, columns - 2):
                if binary_mat[j] == '1':
                    matrix.append((columns - 2)/2 - j - 1)
            for j in range(columns - 2, columns):
                if binary_mat[j] == '1':
                    matrix.append(j + 1)
            for digits in matrix:
                dec_file.write("%s "% digits)
            dec_file.write("\n")
            matrix = []

            
            
"""Transforms data from decimal to binary matrix.
Takes names of input, output files, and number of items (for transaction data to dbasis file)"""

def transformation(file_in, file_out, items):
    with open(file_out, 'wb') as digitfile:
        stream = open(file_in)
        digits = stream.readlines()
        #print digits
        matrix = [0]*items
        row_num = []
        for row in digits:
            row_num = row.split()
            for i in row_num:
                if i.isdigit():
                    matrix[int(i)] = 1;
            #print matrix
            for binary in matrix:
                digitfile.write("%s "% binary)
            digitfile.write("\n")
            matrix= [0]*items


"""Transforms data for importing to a database as a table of transactions and items"""

def read_file(f, f1):
    lines = []
    filename = open(f, 'r')
    lines = [l.strip() for l in filename]
    filename2 = open(f1,'w')
    counter = 1
    for line in lines:
        for l in line.split(' '):
            filename2.write(str(counter) + ' ' + str(l) + '\n')            
        counter += 1 
    filename2.close();
    filename.close();
