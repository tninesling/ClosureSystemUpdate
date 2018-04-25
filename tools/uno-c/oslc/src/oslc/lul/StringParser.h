/*
 * StringParser.h
 *
 *  Created on: Sep 25, 2017
 *      Author: oren
 */

#ifndef OSLC_LUL_STRINGPARSER_H_
#define OSLC_LUL_STRINGPARSER_H_


#include <fstream>
#include <iostream>
#include <vector>
#include <sstream>

#include "Log.h"

//const string StdDelimitersG = "\n\r:";
#define DEFAULT_DELIMITER_STR "\n\r\t: "

namespace oslc {
namespace lul {

class StringParser
{
public:
	StringParser(std::string StdDelimiters_ = DEFAULT_DELIMITER_STR): StdDelimiters(StdDelimiters_)
      //:testXXX(-1)
    {
		//string str;
		//StdDelimiters = str;
		//CommentSymbol = 'a';
		//testXXX = 1;
       //char ch = CommentSymbol;
    }
	//StringParser(int val):testXXX(val) {}

	virtual ~StringParser() {}

	const char CommentSymbol = '#';
	const std::string StdDelimiters;
	//const int testXXX = 2;

	bool isBlankLine(const std::string &textLine)
	{
		// handle empty line
		if(textLine.size()==0)
			return true;
        // else
		// look for next token if any
		size_t tokBegin = getNextTokenPos(textLine);
		if(tokBegin==std::string::npos)
			return true;
		else
			return false;
	}

	bool isCommentLine(const std::string &textLine)
	{
		// handle blank and comment line
		if(textLine.size()>0 && textLine[0]==CommentSymbol)
			return true;
		else
			return false;
	}

	bool loadDataFromFile(const char *srcFile)
	{

		//ifstream fin(srcFile);
		m_fin.open(srcFile);
		if(!m_fin.good())
		{
			LOG_ERROR("failed to load file: "<<srcFile);
			return false;
		}
		return true;

	}

	bool getNextTokens(std::stringstream &sStream)
	{
		std::string textLine;
		//stringstream sStream;
		while(getline(m_fin,textLine))
		{
			// handle blank and comment line
			if(isBlankLine(textLine) || isCommentLine(textLine))
				continue;

			//getNextToken(textLine,)
			// handle data line
			removeDelimiters(textLine);
			//stringstream sStream(textLine);
			sStream.clear();
			sStream.str(textLine);
			//sStream<<textLine;
			return true;
			//discardDelimiters(sStream);
			//DataEntry de;
			//sStream>>de.m_timeStamp;
			//unsigned int dataVal;
            //while(sStream>>dataVal)
			  //de.m_dataArray.push_back(dataVal);
            // store the array and time stamp
            //m_data.push_back(de);
		}

		return false;

        // TODO: only handles int for now...
		//while (fin >> de.m_timeStamp >> de.m_intVal)
		//{
			//m_data.push_back(de);
		//}
	}

	void removeDelimiters(std::string &rawString)
	{
		removeDelimiters(rawString,StdDelimiters);
	}

	void removeDelimiters(std::string &rawString, const std::string &delimiters, const char replaceWith=' ')
	{
		for(size_t i=0;i<rawString.size();i++)
		{
			// if we didnt find the char stop
			if(delimiters.find(rawString[i])!=std::string::npos)
				rawString[i] = replaceWith;
		}
	}

	// !!! can not have default param that is not known at complile time example: delimiters = StdDelimiters
	// https://stackoverflow.com/questions/12903724/default-arguments-have-to-be-bound-at-compiled-time-why
	int discardDelimiters(std::stringstream &sStream)
	{
		return discardDelimiters(sStream,StdDelimiters);
	}

	int discardDelimiters(std::stringstream &sStream, std::string delimiters)
	{
	  //char ch;
	  int count = 0;
	  while(sStream.good())
	  {
		char ch = sStream.peek();
		// if we didnt find the char stop
		if(delimiters.find(ch)==std::string::npos)
			break;
		else
		{
			// eat and discard of delimiter
			sStream>>ch;
			count ++;
		}
	  }
	  return count;
	}

	// delimiters
	size_t getNextToken(const std::string& rawStr, size_t beginPos, std::string delimiters, std::string &tokenStr)
	{
		std::string resStr;
		size_t tokBegin = rawStr.find_first_not_of(delimiters,beginPos);
		if(tokBegin==std::string::npos)
			return std::string::npos;
		size_t tokEnd = rawStr.find_first_of(delimiters,tokBegin);
		// if we didnt find any non token chars, rest of string was all token
		if(tokEnd==std::string::npos)
			tokEnd = rawStr.size();
		// note tokEnd is a delimiter char or eofs
		tokenStr = rawStr.substr(tokBegin,tokEnd-tokBegin);

		return tokEnd;

	}

	size_t getNextTokenPos(const std::string& textLine)
	{
		size_t tokBegin = textLine.find_first_not_of(DEFAULT_DELIMITER_STR);
		return tokBegin;
	}

	// data
	std::ifstream m_fin;

};


} // namespace oslc
} // namespace lul


#endif /* OSLC_LUL_STRINGPARSER_H_ */
