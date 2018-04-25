/*
 * SignalHandler.cpp
 *
 *  Created on: Feb 17, 2018
 *      Author: admin
 */

#include <cstring>

#include <oslc/lul/SignalHandler.h>

namespace oslc {
namespace lul {

//SignalHandler::SignalHandler() {
//	// TODO Auto-generated constructor stub
//
//}
//
//SignalHandler::~SignalHandler() {
//	// TODO Auto-generated destructor stub
//}

jmp_buf SignalHandler::env;

int SignalHandler::retPoint()
{
	std::memset(env,0,sizeof(env));
	volatile int val = setjmp (env);
	return val;
}

void SignalHandler::handler(int sigNum)
{
	longjmp (env,sigNum);
}


} /* namespace lul */
} /* namespace oslc */
