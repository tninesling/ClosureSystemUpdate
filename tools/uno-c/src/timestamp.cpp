/*
 * timestamp.c
 *
 *  Created on: Aug 28, 2013
 *      Author: ulno
 */

#include <stdlib.h>
#include <stdio.h>
#include <time.h>

static double timestamp_starttime = 0;


/* return seconds the program is running */
static double timestamp_cputime ( void )
{
	return (double) clock ( ) / (double) CLOCKS_PER_SEC;
}

void timestamp_init()
{
	timestamp_starttime = timestamp_cputime();
}

void timestamp_print()
{
	double new_time;

	if( timestamp_starttime == 0) timestamp_init();
	new_time = timestamp_cputime() - timestamp_starttime;
	printf( "%.2f ", new_time );
	fflush( stdout ); // Make sure we can see this
}
