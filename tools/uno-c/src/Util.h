/*
 * Util.h
 *
 *  Created on: Dec 12, 2017
 *      Author: admin
 */

#ifndef UTIL_H_
#define UTIL_H_

#include<vector>
#include<string>

//class Util {
//};

std::vector<std::vector<char> > * readTable(std::string fileName);

void printMatrix(std::vector<std::vector<char> > * matrix);

void printMatrix(std::vector<std::vector<int> > * matrix);


#endif /* UTIL_H_ */
