/*
 * CmdLineOptions.cpp
 *
 *  Created on: Dec 26, 2017
 *      Author: admin
 */

#include<string>

#include <oslc/lul/CmdLineOptions.h>

namespace oslc {
namespace lul {


//template<>
//bool Option<std::vector<int>, CmdLineOption>::setValueFromString(std::string strVal)
//{
//	std::stringstream strStream(strVal);
//	//std::stringstream::iostate state;
//	char token;
//	while(strStream>>token && !strStream.eof())
//	{
//	  //strStream>>token;
//	  //state = strStream.rdstate();
//	  if(!isdigit(token))
//		  continue;
//	  else
//	  {
//		  strStream.putback(token);
//		  int num;
//		  if(strStream>>num)
//			  value.push_back(num);
//		  //state = strStream.rdstate();
//	  }
//	}
//	//state = strStream.rdstate();
//	return !strStream.bad();
//}

//Option<std::string, CmdLineOption> testStrCmdLineOpt(CmdLineOption(),"");

}
}

