/*
 * Types.h
 *
 *  Created on: Feb 23, 2018
 *      Author: admin
 */

#ifndef COMMONTYPES_H_
#define COMMONTYPES_H_

///////////////////////
// !!! oren 2.23.18 -> refactor old code (refs #16)
// The purpose of this file is to bring some order to the old code type system mess. there was practically no common type system, many common types were undefined!
// This file will try to patch things up, try to move unknown/undefined types that are used in multiple places here and define them for all the code to share!
///////////////////////

//#include<vector>
#include<map>


typedef std::map<int, std::vector<int> > EquivalenceMap;




#endif /* COMMONTYPES_H_ */
