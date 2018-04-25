/*
 * Version.h
 *
 *  Created on: Jan 12, 2018
 *      Author: admin
 */

#ifndef VERSION_H_
#define VERSION_H_

#include <stdexcept>
#include <sstream>


namespace oslc {
namespace lul {

// templates can not except string literals
// based on solution and discussion at: https://stackoverflow.com/questions/2033110/passing-a-string-literal-as-a-parameter-to-a-c-template-class
#define GEN_TEMPLATE_STR_LITERAL_FUNC(literal) \
inline const char *GLF_##literal() { return #literal; }
//
#define GET_TEMPLATE_STR_LITERAL_FUNC(literal) GLF_##literal

typedef const char *(*GetLiteralFunc)(void);

template <GetLiteralFunc GLF>
//template<int Major, int Minor, int RevNum, const char *RevStr=0, const char *Date=0, const char *Time=0>
//template<const char *Name>
class VersionInfo
{
private:
	// private constructor to enforce singleton
	//VersionInfo() {}

public:
	// constructor to enforce singleton
	//VersionInfo()
	VersionInfo(int Major, int Minor, int RevNum, const char *RevStr=0, const char *Date=0, const char *Time=0):
		MajorNum(Major),
        MinorNum(Minor),
        RevisionNum(RevNum)
    {
		if(++m_instCount>1)
		{
			throw std::runtime_error("Only one version number allowed");
		}

		//MajorNum = Major;
		//MinorNum = Minor;
		//RevisionNum = RevNum;
		RevisionStr = RevStr;
		BuildDate = (Date==0) ? __DATE__ : Date;
		BuildTime = (Time==0) ? __TIME__ : Time;

		m_theVersion = this;
    }

	static VersionInfo *getVersionInfo()
	{
       return m_theVersion;
	}

	std::string getStdVersionString()
	{
      std::ostringstream oss;
      oss<<m_name()<<" version "<<MajorNum<<"."<<MinorNum<<"."<<RevisionNum<<RevisionStr<<" [Build Date/Time: "<<BuildDate<<", "<<BuildTime<<"]";
      return oss.str();
	}

	// data
	GetLiteralFunc m_name = GLF;
	const int MajorNum;// = Major;
	const int MinorNum;// = Minor;
	const int RevisionNum;// = RevNum;
	//Examples: alpha, beta, rc
	const char *RevisionStr;// = RevStr;
	const char *BuildDate;// = Date;
	const char *BuildTime;// = Time;
private:
	static int m_instCount;
	static VersionInfo *m_theVersion;
};

// help protect singleton
template<GetLiteralFunc GLF>
int VersionInfo<GLF>::m_instCount = 0;

template<GetLiteralFunc GLF>
VersionInfo<GLF> *VersionInfo<GLF>::m_theVersion = 0;

/*
//#define GENERATE_VERSION_INFO(name,major,minor,rev,revStr)\
//GEN_TEMPLATE_STR_LITERAL_FUNC(name);\
//oslc::lul::VersionInfo<GET_TEMPLATE_STR_LITERAL_FUNC(name)> versionInfo(major,minor,rev,revStr);\
//typedef oslc::lul::VersionInfo<GET_TEMPLATE_STR_LITERAL_FUNC(name)> name##VersionInfo;
*/

#define DECLARE_VERSION_INFO(name)\
GEN_TEMPLATE_STR_LITERAL_FUNC(name);\
typedef oslc::lul::VersionInfo<GET_TEMPLATE_STR_LITERAL_FUNC(name)> name##VersionInfo;

#define DEFINE_VERSION_INFO(name,major,minor,rev,revStr)\
oslc::lul::VersionInfo<GET_TEMPLATE_STR_LITERAL_FUNC(name)> versionInfo(major,minor,rev,revStr);

#define GET_VERSION_INFO(name)\
oslc::lul::VersionInfo<GET_TEMPLATE_STR_LITERAL_FUNC(name)>::getVersionInfo();

} /* namespace lul */
} /* namespace oslc */


#endif /* VERSION_H_ */
