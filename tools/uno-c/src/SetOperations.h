/*
 * File:   SetOperations.h
 *
 * Author: Toviah Moldwin
 *
 * Created on April 18, 2013, 4:14 PM
 */

#ifndef SETOPERATIONS_H
#define	SETOPERATIONS_H
#include <vector>
#include <iostream>
#include <cstdlib>

//Finds the complement of the subset with respect to the superset
std::vector<int> & complement (std::vector<int> & subset, std::vector<int>  & superset);

//Finds the intersection of two vectors
std::vector<int> & intersection (std::vector<int> & set1, std::vector<int>  & set2);

//Prints any vector, used for testing.
void printVector(std::vector<int> & path);

//
void testComplement();

void testIntersection();


#endif	/* SETOPERATIONS_H */

