/*
 * Log.h
 *
 *  Created on: Jul 31, 2017
 *      Author: oren
 */

#ifndef OSLC_LUL_LOG_H_
#define OSLC_LUL_LOG_H_

#include<iostream>

namespace oslc {
namespace lul {


class MsgLog
{
public:

  enum Type { Info, Warning, Error };

  static const char *type2String(Type type)
  {
	  switch(type)
	  {
	  case Info:
		  return "Info";
	  case Warning:
		  return "Warning";
	  case Error:
		  return "Error";
	  default:
		  return "Unknown Type";
	  }
  }

  static std::ostream &log(const char *fileName, int lineNum, Type type)
  {
	  return std::clog<<fileName<<":"<<lineNum<<":"<<type2String(type)<<": ";
  }

//  static void logHelper( const char * fmt, ... )
//  {
//      va_list ap;
//      va_start( ap, fmt );
//      vfprintf( stderr, fmt, ap );
//      va_end( ap );
//  }
  //__FILE__, __LINE__,

  //#define logMsg(type,x) clog<<type<<": "<<x
  //#define LOG_MSG(level,format, ...) CLogClass::DoLogWithFileLineInfo("%s:%d " format , __FILE__, __LINE__, __VA_ARGS__)
  //#define LOG_MSG(format, ...) MessageLog::logHelper("%s:%d " format , __FILE__, __LINE__, __VA_ARGS__)
  #define LOG(type,x) oslc::lul::MsgLog::log(__FILE__,__LINE__,type)<<x<<"\n"

  // log with file/line specified, useful log an error inside a call to log from a different location
  #define LOG_LOC(file,line,type,x) oslc::lul::MsgLog::log(file,line,type)<<x<<"\n"

  #define LOG_INFO(x) LOG(oslc::lul::MsgLog::Info,x)
  #define LOG_WARNING(x) LOG(oslc::lul::MsgLog::Warning,x)
  #define LOG_ERROR(x) LOG(oslc::lul::MsgLog::Error,x)



};


} // namespace oslc
} // namespace lul



#endif /* OSLC_LUL_LOG_H */
