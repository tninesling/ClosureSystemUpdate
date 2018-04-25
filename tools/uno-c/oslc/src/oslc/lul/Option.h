/*
 * Option.h
 *
 *  Created on: Dec 3, 2017
 *      Author: admin
 */

#ifndef OSLC_LUL_OPTION_H_
#define OSLC_LUL_OPTION_H_

#include <map>
#include <sstream>
#include <string>
#include <memory>
//#include <vector>

#include <oslc/lul/StringParser.h>
#include <oslc/lul/Log.h>

#include <oslc/lul/Misc.h>


namespace oslc {
namespace lul {


//template<class S>
class OptionBase
{
public:
    //typedef std::shared_ptr<OptionBase<S> > Ptr;
    typedef std::shared_ptr<OptionBase> Ptr;

//	OptionBase(std::string name, std::string desc):  name(name), desc(desc) {}
//	OptionBase(const OptionBase &optBase)
//	{
//		name = optBase.name;
//		desc = optBase.desc;
//	}
//    OptionBase(const S &s): m_sData(s)
//    {
//
//    }
//
//    OptionBase(const OptionBase<S> &optBase): m_sData(optBase.m_sData)
//    {
//
//    }
//    OptionBase &operator =(const OptionBase<S> &optBase)
//    {
//    	m_sData = optBase.m_sData;
//    	return *this;
//    }

	//virtual OptionBase<S>::Ptr clone() = 0;
	virtual OptionBase::Ptr clone() { return NULL; }//= 0;
	virtual bool setValueFromString(std::string strVal) { return false; } //= 0;
	//virtual bool setValueFromString(const char *strVal) = 0;
	virtual ~OptionBase() {}

	//S &getSData() { return m_sData; }
	// data
//	std::string name;
//	std::string desc;
	//S m_sData;
};

class DefaultOptionData: public OptionBase
{
public:
	DefaultOptionData(std::string name,  std::string desc): name(name), desc(desc) {}
	DefaultOptionData(const DefaultOptionData &other)
    {
	  name = other.name;
	  desc = other.desc;
    }
	// data
	std::string name;
	std::string desc;
};

//template< class T, class S=DefaultOptionData >
template< class T, class S>
class Option : public S // S -> OptionBase
{

//typename std::enable_if<std::is_base_of<OptionBase, T>::value, void>::type;

static_assert(
	        std::is_base_of<OptionBase, S>::value,
	        "S must be a descendant of OptionBase"
	    );

public:

      //typename OptionBase<S>::Ptr;
      //typedef typename OptionBase<S>::Ptr Ptr;
      //typedef typename S::Ptr Ptr;


	  //Option(bool isAvailable=false): isAvailable(isAvailable) {}
//	  Option(std::string name,  std::string desc,  T value):
//		  S(name,desc), value(value) //, optType(optType), isAvailable(isAvailable)
//	  {
//	  }
//
	  Option(const Option &opt): S(opt), value(opt.value)
	  {
			//value = opt.value;
	  }

	  Option(const S &s,  T value):
		  S(s), value(value) //, optType(optType), isAvailable(isAvailable)
	  {
	  }
//
//      Option(const Option &opt): OptionBase<S>(opt), value(opt.value)
//      {
//      			//value = opt.value;
//      }

	  virtual typename OptionBase::Ptr clone() { return (typename OptionBase::Ptr)new Option<T,S>(*this); }

	  T getVal()
	  {
		  return value;
	  }

	  bool setValueFromString(std::string strVal)
	  {
		  return setValueFromString<T>(strVal);
	  }

	  template<class Q>
	  typename std::enable_if<!is_vector<Q>::value,bool>::type
	  setValueFromString(std::string strVal)
	  //bool setValueFromString(std::string strVal)
	  //bool setValueFromString(const char *strVal)
	  {
		  std::stringstream strStream(strVal);
		  strStream>>value;
		  return !strStream.fail();
	  }

	  template<class Q>
	  typename std::enable_if<is_vector<Q>::value,bool>::type
	  setValueFromString(std::string strVal)
	  {
			std::stringstream strStream(strVal);
			//std::stringstream::iostate state;
			char token;
			while(strStream>>token && !strStream.eof())
			{
			  //strStream>>token;
			  //state = strStream.rdstate();
			  if(!isdigit(token))
				  continue;
			  else
			  {
				  strStream.putback(token);
				  int num;
				  if(strStream>>num)
					  value.push_back(num);
				  //state = strStream.rdstate();
			  }
			}
			//state = strStream.rdstate();
			return !strStream.bad();
	  }


	  void setVal(T value_)
	  {
		  value = value_;
	  }

	  virtual ~Option() {}

	  // data
	  T value;
	  //OptionType optType;
	  //bool isAvailable;
};


//template<class S>
//class Option<std::string,S>
//{
//public:
//  bool setValueFromString(std::string strVal)
//  {
//     return true;
//  }
//};

//template<>
//bool Option<std::string>::setValueFromString(std::string strVal);

//typedef std::vector<int> IntVec;
//
//template<class S>
//bool Option<IntVec,S>::setValueFromString(std::string strVal);

//template<class B=OptionBase>
//bool Option<std::string,B>::setValueFromString(std::string strVal);

//
// Partial template specialization of class Option for T=vector<T>
//   Note: This is an alternative to using is_vector and SFINAE in setValueFromString inside the base class Option
//
//template<class T, class S>
//class Option<std::vector<T>,S>: public S//Option<std::vector<T>,CmdLineOption>
//{
//public:
//  Option(S s, std::vector<T> &value): S(s), value(value) { }
//  Option(const Option &opt): S(opt), value(opt.value) {}
//  bool setValueFromString(std::string strVal)
//  {
//		std::stringstream strStream(strVal);
//		//std::stringstream::iostate state;
//		char token;
//		while(strStream>>token && !strStream.eof())
//		{
//		  //strStream>>token;
//		  //state = strStream.rdstate();
//		  if(!isdigit(token))
//			  continue;
//		  else
//		  {
//			  strStream.putback(token);
//			  int num;
//			  if(strStream>>num)
//				  value.push_back(num);
//			  //state = strStream.rdstate();
//		  }
//		}
//		//state = strStream.rdstate();
//		return !strStream.bad();
//  }
//  // data
//  std::vector<T> value;
//
//};


//template<class S=DefaultOptionData>
template<class S>
class OptionContainer
{
	static_assert(
		        std::is_base_of<OptionBase, S>::value,
		        "S must be a descendant of OptionBase"
		    );

public:
      //typedef typename OptionBase<S>::Ptr OptionBasePtr;
      //typedef typename OptionBase::Ptr OptionBasePtr;
      typedef typename std::shared_ptr<S> OptionBasePtr;

	  typedef std::map<std::string, OptionBasePtr> OptionMap;

	  template<class T> using OptionPtr = std::shared_ptr<Option<T,S> >;

//	  template<class T>
//	  OptionContainer &addOption(std::string name, std::string desc="", T defaultValue=T())
//	  {
//		  OptionBase::Ptr optBasePtr = (OptionBase::Ptr)new Option<T,S> (name,desc,defaultValue);
//		  m_options.insert(OptionMap::value_type(name,optBasePtr));
//		  //m_options.insert(std::pair<std::string, OptionPtr>(name,optPtr));
//		  return *this;
//	  }
//
//	  template<class T>
//	  bool addOption2(std::string name, std::string desc="", T defaultValue=T())
//	  {
//		  OptionBase::Ptr optBasePtr = (OptionBase::Ptr)new Option<T,S> (name,desc,defaultValue);
//		  m_options.insert(OptionMap::value_type(name,optBasePtr));
//		  return true;
//	  }

	  //template<class T>
	  bool addOption(std::string name, OptionBasePtr optBasePtr)
	  {
		  m_options.insert(typename OptionMap::value_type(name,optBasePtr));
		  return true;
	  }

	  bool addOptionSynonym(std::string name, std::string synonym)
	  {
		  OptionBasePtr optBasePtr = m_options[name];
		  if(optBasePtr==NULL)
		  {
			  return false;
		  }
		  else
		  {
			  m_options.insert(typename OptionMap::value_type(synonym,optBasePtr));
			  return true;
		  }
	  }

	  typedef std::map< OptionBasePtr,std::vector<std::string> > SynonymGroups;

	  bool getSynonymKeyGroups(SynonymGroups &sg)
      {
		  for(typename OptionMap::const_iterator itr = m_options.begin(); itr!=m_options.end(); itr++)
		  {
			  sg[itr->second].push_back(itr->first);
		  }

		  return true;
      }


	  // validate that option name is valid and option type matches requested type
	  // Note: we use RTTI to validate types match.
	  //       Problem example: avoid a case where option value type(T) is a string but requested value type(T) is an integer
	  //template <class T> Option<T,S> *tryGetOptPtr(std::string name)
	  template <class T> OptionPtr<T> tryGetOptPtr(std::string name)
	  {
		  try
		  {
		  OptionBasePtr optBasePtr = m_options[name];
		  // first check that option exists
		  if(optBasePtr==NULL)
			  throw std::invalid_argument("Unknown configuration option: " + name );
		  // validate that cast is legal using RTTI
		  //Option<T,S> *optPtr= dynamic_cast<Option<T,S> *>(optBasePtr.get());
		  OptionPtr<T> optPtr = std::dynamic_pointer_cast<Option<T,S> > (optBasePtr);
		  if(optPtr==NULL)
			  throw std::invalid_argument("Illegal configuration option type: " + name  + " is not of type " + typeid(T).name());
		  return optPtr;
		  }
		  catch(...)
		  {
	    	  LOG_ERROR("tryGetOptPtr exception: "<<name);
	    	  throw; // rethrow error
	      }
	  }

	  bool isValidOption(std::string name)
	  {
		  OptionBasePtr optBasePtr = m_options[name];
		  return optBasePtr!=NULL;
	  }

	  //template <class T> Option<T,S> * getOpt(std::string name)
      template <class T> OptionPtr<T> getOpt(std::string name)
	  {
		  //Option<T,S> *optPtr= tryGetOptPtr<T>(name);
    	  OptionPtr<T> optPtr= tryGetOptPtr<T>(name);
		  return optPtr;
	  }

	  template <class T> T getVal(std::string name)
	  {
		  //Option<T> *optPtr= tryGetOptPtr<T>(name);
		  //return optPtr->value;
		  return getOpt<T>(name)->value;
	  }

	  template <class T> void setVal(std::string name, T value)
	  {
		  //Option<T> *optPtr= tryGetOptPtr<T>(name);
		  OptionPtr<T> optPtr= tryGetOptPtr<T>(name);
		  optPtr->value = value;
	  }

//	  template <class T> void setOpt(std::string name, Option<T> *optPtr)
//	  {
//	  }

	  void copyFrom(const OptionContainer &src)
	  {
		  for(typename OptionMap::const_iterator itr = src.m_options.begin(); itr!=src.m_options.end(); itr++)
		  {
			  OptionBasePtr optBasePtr = std::dynamic_pointer_cast<S>(itr->second->clone());
			  m_options.insert(typename OptionMap::value_type(itr->first,optBasePtr));
		  }
	  }

	  //void load(std::string cfgStr)
	  bool loadFromFile(const char *srcFile)
	  {
          using namespace oslc::lul;
          using namespace std;

			StringParser strParser;
			if(!strParser.loadDataFromFile(srcFile))
				return false;

			stringstream sStream;
			while(strParser.getNextTokens(sStream))
			{
				//string varType;
				//sStream>>varType;
				string varName;
				sStream>>varName;
				//if(varType=="int")
				//addOption(varType)

				//Option<T> *optPtr= tryGetOptPtr<T>(name);

				OptionBasePtr optBasePtr = m_options[varName];
			    if(optBasePtr==NULL)
			    {
		    	  LOG_ERROR("Bad option name: "<<varName);
		    	  break;
			    }

				string varVal;
				// we want to read spaces as well i.e. the whole line up to new line
				//sStream>>varVal;
				std::getline(sStream,varVal);

			    if(!optBasePtr->setValueFromString(varVal))
			    {
		    	  LOG_ERROR("Bad option value: "<<varName<<" : "<<varVal);
		    	  break;
			    }
			}

			return true;
	  }

	 // the map container
	 OptionMap m_options;
};



} /* namespace lul */
} /* namespace oslc */

#endif /* OSLC_LUL_OPTION_H_ */
