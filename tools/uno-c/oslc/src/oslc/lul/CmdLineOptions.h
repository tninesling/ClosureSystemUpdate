#ifndef OSLC_LUL_CMDLINEOPTIONS_H
#define OSLC_LUL_CMDLINEOPTIONS_H

#include <map>
#include <sstream>
#include <string>

#include <oslc/lul/Log.h>

#include <oslc/lul/Option.h>

// needed for macro functions (concat, stringfy etc.)
#include <oslc/lul/Misc.h>

namespace oslc {
namespace lul {

//template<class T>
struct CmdLineOption: OptionBase//Option<std::string>//Option<std::string>
{
	  enum OptionType
	  {
		  Unknown, // unknown type
		  ValueOpt, // an option with a value on the cmd line. example: -p value
		  BinaryOpt, // an option without a value, it either exist or it doesn't exist on the cmd line. example: -b
		  PlainVal  // not an option just a value on the cmd line
	  };

//	  typedef std::shared_ptr<CmdLineOption> Ptr;

	  CmdLineOption(bool isAvailable=false): optType(Unknown), isAvailable(isAvailable)
	  {

	  }

	  CmdLineOption(std::string name,  std::string desc, OptionType optType, bool isAvailable=false):
		  name(name),desc(desc),optType(optType),isAvailable(isAvailable)
	  {
	  }

	  // a predefined option is one that was defined/anticipated by the code, not an unknown or value option which was provided by user as a parameter
	  bool isPredefinedOption()
	  {
        return ( (optType==ValueOpt) || (optType==BinaryOpt) );
	  }
//	  template <class T> T getVal()
//	  {
//		  T val;
//		  std::stringstream strStream(value);
//		  strStream>>val;
//		  return val;
//	  }

	  // data
	  std::string name;
	  std::string desc;
	  //std::string value;
	  OptionType optType;
	  bool isAvailable;
};

  class CmdLineOptions: public OptionContainer<CmdLineOption>
  {
  public:
	  //using namespace oslc::lul;

	  //typedef std::map<std::string,Option> OptionMap;
	  //typedef std::map<std::string, CmdLineOption::Ptr> OptionMap;
	  // C++11 style template specialization
	  //template<class T> using  CmdLineOptionT = Option<T,CmdLineOption>;

	  //template<class T> using  CmdLineOptionPtr =  std::shared_ptr<Option<T,CmdLineOption> >;
	  //typedef std::shared_ptr<CmdLineOptionT> Ptr;


	  // default constructor
	  CmdLineOptions() {}

	  CmdLineOptions(std::string title, std::string desc): m_title(title), m_desc(desc)
	  {

	  }

	  bool init(std::string title, std::string desc)
	  {
		  m_title = title;
		  m_desc  = desc;
		  return true;
	  }

	  template<class T>
	  CmdLineOptions &addOption(std::string name, std::string desc, T defaultValue, CmdLineOption::OptionType optType=CmdLineOption::OptionType::ValueOpt, bool isAvailable=false)
	  {
		  //Option opt = {name,desc,defaultValue,optType};
		  //m_options.insert(OptionMap::value_type(name,opt));
		  OptionBasePtr optBasePtr = (OptionBasePtr)new Option<T,CmdLineOption>(CmdLineOption(name,desc,optType,isAvailable),defaultValue);
		  OptionContainer::addOption(name,optBasePtr);

		  return *this;
	  }

	  template<class T>
	  bool addOption2(std::string name, std::string desc, T defaultValue, CmdLineOption::OptionType optType=CmdLineOption::OptionType::ValueOpt, bool isAvailable=false)
	  {
		  OptionBasePtr optBasePtr = (OptionBasePtr)new Option<T,CmdLineOption>(CmdLineOption(name,desc,optType,isAvailable),defaultValue);
		  OptionContainer::addOption(name,optBasePtr);
		  return true;
	  }

//	  bool addOptionSynonym(std::string name, std::string synonym)
//	  {
//		  CmdLineOption::Ptr optPtr = this->operator [](name);
//		  if(optPtr==NULL)
//		  {
//			  return false;
//		  }
//		  else
//		  {
//			  m_options.insert(OptionMap::value_type(synonym,optPtr));
//		  }
//	  }
//	  std::string parseOption(std::string optStr)
//	  {
//		  //int index;
//		  for(int i=0;i<optStr.size();i++)
//		  {
//			  if(optStr[i]=='-')
//				  continue;
//			  //else
//			  return optStr.substr(0,optStr.size()-i);
//		  }
//		  //???
//		  return std::string();
//	  }
	  //
	  // for now there could be only one type of separator char ('-') (that is enough to support the common '-'  and '--' options)
	  //
      #define OPTION_STR_SEP_CHAR '-'

	  bool isOptionStr(std::string optStr)
	  {
		  return optStr[0]==OPTION_STR_SEP_CHAR;
	  }

	  bool parse(int argc, char **argv)
	  {
		  // reserved for exe name
		  //m_options["__ExecName__"]=Option(argv[0],"","",OptionType::PlainVal,true);
		  //m_options["__ExecName__"]= CmdLineOption::Ptr (new CmdLineOption(argv[0],"","",CmdLineOption::OptionType::PlainVal,true));
		  addOption<std::string>("__ExecName__", "executable name", argv[0],CmdLineOption::OptionType::PlainVal,true);
		  int i=1;
		  while(i<argc)
		  {
			  // first check if its a value or option
			  if(!isOptionStr(argv[i]))
			  {
				std::stringstream strStream;
				strStream<<"__val"<<i;
				addOption<std::string>(strStream.str(),"plain value type",argv[i],CmdLineOption::OptionType::PlainVal,true);
			    i++;
			    continue;
			  }
		      //std::string opt = parseOption(argv[i]);
		      std::string opt = argv[i];
			  //OptionMap::iterator itr = m_options.find(opt);
			  //if(itr==m_options.end())
				  //throw std::error("bad arguments");
			  if(!isValidOption(opt))
				  return false;
			  // else
			  OptionBasePtr optData = m_options[opt];//this->operator [](opt);
			  // mark it as available
			  optData->isAvailable = true;
			  // inc index for next param/option
			  i++;
			  // check if its a value type, which means we now need to read the value
			  if(optData->optType==CmdLineOption::OptionType::ValueOpt)
			  {
				if(i<argc)
				{
				  if(!optData->setValueFromString(argv[i]))
				  {
			    	  LOG_ERROR("Bad command line option value: "<<argv[i]);

					  return false;
				  }
				  else
			        i++;
				}
				else //error
				  return false;
			  }
		  }
		  // return success
		  return true;
	  }

//	  template <class T> T getOptVal(std::string valStr)
//	  {
//		  T val;
//		  std::stringstream strStream(valStr);
//		  strStream>>val;
//		  return val;
//	  }

//	  template<class T>
//	  CmdLineOptionPtr<T> optBasePtrToCmdLinePtr(OptionBasePtr optBasePtr)
//	  {
//		  return dynamic_pointer_cast<CmdLineOptionT<T> >(optBasePtr);
//	  }

	  OptionBasePtr operator[](std::string name)
	  {
		  //return optBasePtrToCmdLinePtr(m_options[name]);
		  return m_options[name];
	  }

	  static CmdLineOptions &getGlblCmdLineOptions()
	  {
		  //static ConfigOptions *theOne = new ConfigOptions;
		  static CmdLineOptions theOne;
		  return theOne;
	  }

	  void displayUsageMsg()
	  {
		  std::cout<<"--------"<<std::endl;
		  std::cout<<m_title<<std::endl;
		  std::cout<<"--------"<<std::endl;
		  std::cout<<m_desc<<std::endl;
		  std::cout<<"--------"<<std::endl;
		  std::cout<<"Options:"<<std::endl;
		  std::cout<<"--------"<<std::endl;

//		  for(OptionMap::iterator itr = m_options.begin();itr!=m_options.end();itr++)
//		  {
//		    CmdLineOption::Ptr opt = optBasePtrToCmdLinePtr(itr->second);
//			std::cout<<"  "<<opt->name<<"\t"<<opt->desc<<std::endl;
//		  }
		  // first get groups of keys becasue an option can have multiple names (friendly name, shortcut etc.)
		  SynonymGroups sg;
		  getSynonymKeyGroups(sg);

		  for(SynonymGroups::iterator itr = sg.begin();itr!=sg.end();itr++)
		  {
		    OptionBasePtr opt = itr->first;//optBasePtrToCmdLinePtr(itr->first);
		    // if this is not a valid or predefined option
		    if(!opt || !opt->isPredefinedOption())
		    	continue;

		    std::vector<std::string> &altNameList = itr->second;
		    for(int i=0;i<altNameList.size()-1;i++)
			  std::cout<<"  "<<altNameList[i]<<",";

		    std::cout<<"  "<<altNameList[altNameList.size()-1]<<std::endl;
			std::cout<<"\t"<<opt->desc<<std::endl;
		  }
	  }

	  //
	  // utility to convert argc/argv pair to a string to recreate original command line
	  //
	  static std::string cmdLineToString(int argc, char **argv)
	  {
  		  std::stringstream strStream;
  		  for(int i=0;i<argc-1;i++)
  		    strStream<<argv[i]<<" ";
	      strStream<<argv[argc-1];
  		  return strStream.str();
	  }

  private:
	  // data
	  //OptionMap m_options;
	  std::string m_title;
	  std::string m_desc;
};

//template<>
//bool Option<std::vector<int>, CmdLineOption>::setValueFromString(std::string strVal);

template<class T> using  CmdLineOptionPtr =  std::shared_ptr<oslc::lul::Option<T,oslc::lul::CmdLineOption> >;
//template<class T> using  CmdLineOptionPtr =  Option<T,CmdLineOption> *;

//template<class T>
//class OptionX: public Option<T,CmdLineOption> {};
//
//template<class T>
//class Option<std::vector<T>,CmdLineOption>: public OptionX< std::vector<T> >//Option<std::vector<T>,CmdLineOption>
//{
//
//};

////
//// Partial template specialization
////
//template<class T>
//class Option<std::vector<T>,CmdLineOption>: public CmdLineOption//Option<std::vector<T>,CmdLineOption>
//{
//public:
//  Option(CmdLineOption clo, std::vector<T> &value): CmdLineOption(clo), value(value) { }
//  Option(const Option &opt): CmdLineOption(opt), value(opt.value) {}
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


#define INIT_CMD_LINE(title,desc) \
static volatile bool global_cmd_line_init = oslc::lul::CmdLineOptions::getGlblCmdLineOptions().init(title,desc)

#define REGISTER_CMDLINE_OPT(name, desc, type, defVal, optType) \
static volatile bool MACRO_CONCAT(global_cmd_line_option_,__COUNTER__) = oslc::lul::CmdLineOptions::getGlblCmdLineOptions().addOption2<type>(name,desc,defVal,optType)

  #define REGISTER_CMDLINE_SYNONYM(name, synonym) \
static volatile bool MACRO_CONCAT(global_cmd_line_option_synonym_,__COUNTER__) = oslc::lul::CmdLineOptions::getGlblCmdLineOptions().addOptionSynonym(name,synonym)


} /* namespace lul */
} /* namespace oslc */

#endif // OSLC_LUL_CMDLINEOPTIONS_H
