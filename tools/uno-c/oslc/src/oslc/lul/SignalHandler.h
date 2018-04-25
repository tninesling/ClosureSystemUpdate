/*
 * SignalHandler.h
 *
 *  Created on: Feb 17, 2018
 *      Author: admin
 */

#ifndef SRC_OSLC_LUL_SIGNALHANDLER_H_
#define SRC_OSLC_LUL_SIGNALHANDLER_H_

#include <vector>
#include <csignal>
#include <csetjmp>

namespace oslc {
namespace lul {

//TODO: read https://stackoverflow.com/questions/8934879/how-to-handle-sigabrt-signal
//TODO: for POSIX consider this -> signal was superseded by :http://pubs.opengroup.org/onlinepubs/009604499/functions/sigaction.html
class SignalHandler
{
public:

	typedef void (*SigHandlerFunc)(int);
	struct SigHandlerData
	{
		SigHandlerData(): sigNum(0), prevHandler(0) {}
		// data
		int sigNum;
		SigHandlerFunc prevHandler;// saved for restoring
	};

	SignalHandler()//: m_handlers(SIGUNUSED)
	{
		//m_handlers.resize[]
	}

	SignalHandler &set(int sigNum)
	{
		SigHandlerData shd;
		shd.sigNum = sigNum;
		shd.prevHandler = std::signal(sigNum,handler); // check thread safety issues ???
		m_handlers.push_back(shd);
        return *this;
	}

	static jmp_buf env;

	static int retPoint();
//	{
//		return setjmp (env);
//	}

	static void handler(int sigNum);
//	{
//		longjmp (env,sigNum);
//	}

	~SignalHandler()
	{
      //TODO return old handlers
	}
	// data
	std::vector<SigHandlerData> m_handlers;

};

} /* namespace lul */
} /* namespace oslc */

#endif /* SRC_OSLC_LUL_SIGNALHANDLER_H_ */
