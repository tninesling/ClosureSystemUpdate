/*
 * shd.h
 *
 * Providing necessary symbols from shd
 *
 *  Created on: Jun 13, 2013
 *      Author: ulno
 */

#ifndef SHD_H_
#define SHD_H_

#include <stdlib.h>
#include <stdio.h>

//extern "C"
//{


#include "stdlib2.h"

extern int SHD_main (int argc, char *argv[]);

// these are only copies for stupid eclipse indexer
extern void *__load_from_memory__, *__load_from_memory_org__;
extern char *__write_to_memory__, *__write_to_memory_org__, *__write_to_memory_next__;

//}
#endif /* SHD_H_ */
