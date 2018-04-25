/*
 * ConfigOptions.h
 *
 *  Created on: Nov 25, 2017
 *      Author: admin
 */

#ifndef OSLC_LUL_CONFIGOPTIONS_H_
#define OSLC_LUL_CONFIGOPTIONS_H_

#include <map>
#include <sstream>
#include <string>
#include <memory>

//#include<iostream>
//#include <typeinfo>
#include <oslc/lul/Log.h>
#include <oslc/lul/StringParser.h>
#include <oslc/lul/Option.h>

// needed for macro functions (concat, stringfy etc.)
#include <oslc/lul/Misc.h>


namespace oslc {
namespace lul {

class ConfigOptions: public OptionContainer<DefaultOptionData>
{
public:
////	  enum OptionType
////	  {
////		  ValueOpt, // an option with a value on the cmd line. example: -p value
////		  BinaryOpt, // an option without a value, it either exist or it doesn't exist on the cmd line. example: -b
////		  PlainVal  // not an option just a value on the cmd line
////	  };
//
//	  //typedef std::unique_ptr<OptionBase> OptionPtr;
//	  //typedef std::shared_ptr<OptionBase> OptionBasePtr;
//	  typedef std::map<std::string, OptionBase::Ptr> OptionMap;
//
//	  template<class T>
//	  ConfigOptions &addOption(std::string name, std::string desc="", T defaultValue=T())
//	  {
//		  OptionBase::Ptr optBasePtr = (OptionBase::Ptr)new Option<T> (name,desc,defaultValue);
//		  m_options.insert(OptionMap::value_type(name,optBasePtr));
//		  //m_options.insert(std::pair<std::string, OptionPtr>(name,optPtr));
//		  return *this;
//	  }
//
//	  template<class T>
//	  bool addOption2(std::string name, std::string desc="", T defaultValue=T())
//	  {
//		  OptionBase::Ptr optBasePtr = (OptionBase::Ptr)new Option<T> (name,desc,defaultValue);
//		  m_options.insert(OptionMap::value_type(name,optBasePtr));
//		  return true;
//	  }

	  template<class T>
	  OptionContainer &addOption(std::string name, std::string desc="", T defaultValue=T())
	  {
		  //OptionBasePtr optBasePtr = (OptionBasePtr)new Option<T,DefaultOptionData> (DefaultOptionData(name,desc),defaultValue);
		  OptionBasePtr optBasePtr = (OptionBasePtr)new Option<T,DefaultOptionData> (DefaultOptionData(name,desc),defaultValue);
		  m_options.insert(OptionMap::value_type(name,optBasePtr));
		  //m_options.insert(std::pair<std::string, OptionPtr>(name,optPtr));
		  return *this;
	  }

	  template<class T>
	  bool addOption2(std::string name, std::string desc="", T defaultValue=T())
	  {
		  OptionBasePtr optBasePtr = (OptionBasePtr)new Option<T,DefaultOptionData> (DefaultOptionData(name,desc),defaultValue);
		  m_options.insert(OptionMap::value_type(name,optBasePtr));
		  return true;
	  }


//	  // validate that option name is valid and option type matches requested type
//	  // Note: we use RTTI to validate types match.
//	  //       Problem example: avoid a case where option value type(T) is a string but requested value type(T) is an integer
//	  template <class T> Option<T> *tryGetOptPtr(std::string name)
//	  {
//		  try
//		  {
//		  OptionBase::Ptr optBasePtr = m_options[name];
//		  // first check that option exists
//		  if(optBasePtr==NULL)
//			  throw std::invalid_argument("Unknown configuration option: " + name );
//		  // validate that cast is legal using RTTI
//		  Option<T> *optPtr= dynamic_cast<Option<T> *>(optBasePtr.get());
//		  if(optPtr==NULL)
//			  throw std::invalid_argument("Illegal configuration option type: " + name  + " is not of type " + typeid(T).name());
//		  return optPtr;
//		  }
//		  catch(...)
//		  {
//	    	  LOG_ERROR("tryGetOptPtr exception: "<<name);
//	    	  throw; // rethrow error
//	      }
//	  }
//
//	  bool isValidOption(std::string name)
//	  {
//		  OptionBase::Ptr optBasePtr = m_options[name];
//		  return optBasePtr!=NULL;
//	  }
//
//	  template <class T> T getVal(std::string name)
//	  {
//		  Option<T> *optPtr= tryGetOptPtr<T>(name);
//		  return optPtr->value;
//	  }
//
//	  template <class T> void setVal(std::string name, T value)
//	  {
//		  Option<T> *optPtr= tryGetOptPtr<T>(name);
//		  optPtr->value = value;
//	  }

	  static ConfigOptions &getGlblConfig()
	  {
		  //static ConfigOptions *theOne = new ConfigOptions;
		  static ConfigOptions theOne;
		  return theOne;
	  }

//	  void copyFrom(const ConfigOptions &src)
//	  {
//		  for(OptionMap::const_iterator itr = src.m_options.begin(); itr!=src.m_options.end(); itr++)
//		  {
//			  OptionBase::Ptr optBasePtr = itr->second->clone();
//			  m_options.insert(OptionMap::value_type(itr->first,optBasePtr));
//		  }
//	  }


//	  //void load(std::string cfgStr)
//	  bool loadFromFile(const char *srcFile)
//	  {
//            using namespace oslc::lul;
//            using namespace std;
//
//			StringParser strParser;
//			strParser.loadDataFromFile(srcFile);
//
//			stringstream sStream;
//			while(strParser.getNextTokens(sStream))
//			{
//				//string varType;
//				//sStream>>varType;
//				string varName;
//				sStream>>varName;
//				//if(varType=="int")
//				//addOption(varType)
//
//				//Option<T> *optPtr= tryGetOptPtr<T>(name);
//
//			    OptionBase::Ptr optBasePtr = m_options[varName];
//			    if(optBasePtr==NULL)
//			    {
//		    	  LOG_ERROR("Bad option name: "<<varName);
//		    	  break;
//			    }
//
//				string varVal;
//				// we want to read spaces as well i.e. the whole line up to new line
//				//sStream>>varVal;
//				std::getline(sStream,varVal);
//
//			    if(!optBasePtr->setValueFromString(varVal))
//			    {
//		    	  LOG_ERROR("Bad option value: "<<varName<<" : "<<varVal);
//		    	  break;
//			    }
//
//
//				// read input values
////				for(unsigned int i=0;i<inputCount;i++)
////				{
////				  if(sStream>>dataVal)
////					de.m_inDataArray.push_back(dataVal);
////				  else
////				  {
////					LOG_ERROR("Bad input count"<<inputCount);
////					break;
////				  }
////				}
//
//				// store the array and time stamp
//				//m_data.push_back(de);
//			}
//
//			return true;
//	  }

	  ConfigOptions();

	  ConfigOptions(const ConfigOptions &src)
	  {
		  copyFrom(src);
	  }

	  virtual ~ConfigOptions();

	// data
//	OptionMap m_options;


};

template<class T> using  ConfigOptionPtr =  std::shared_ptr<oslc::lul::Option<T,oslc::lul::DefaultOptionData> >;

//#define REGISTER_CONFIG_OPT(name, desc, type, defVal) \
//static volatile bool global_option_##name = oslc::lul::ConfigOptions::getGlblConfig().addOption2<type>(#name,desc,defVal)

#define REGISTER_CONFIG_OPT(name, desc, type, defVal) \
static volatile bool MACRO_CONCAT(global_option_,__COUNTER__) = oslc::lul::ConfigOptions::getGlblConfig().addOption2<type>(name,desc,defVal)


} /* namespace lul */
} /* namespace oslc */


#endif /* OSLC_LUL_CONFIGOPTIONS_H_ */
