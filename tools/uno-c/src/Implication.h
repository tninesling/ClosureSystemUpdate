/*
 * Author: Toviah Moldwin
 */
#ifndef H_IMPLICATION
#define H_IMPLICATION 


#include <string>
#include <vector>
#include <algorithm>

#include "timestamp.h"

#include "CommonTypes.h"
#include <oslc/lul/Misc.h>

class Implication {
private:
	std::vector<int>  leftHandSide;
	std::vector<int>  rightHandSide;
	// !!! Oren 1.8.17 -> refactor design. old code design makes artificial separation between implications and their support
	//                    problematic for new parallel extensions and in general a problematic design choice as can be seen in current Table interface
	int m_support;//gives the magnitude of the support for the implication
public:
	// improve performance, do not create a copy of vector
	//Implication(std::vector<int>  lhs, std::vector<int>  rhs, int support);
	Implication(const std::vector<int>  &lhs, const std::vector<int>  &rhs, const int support);
	std::string toString();
//    std::vector<int> getlhs();
//    std::vector<int> getrhs();
	const std::vector<int> &getrhs() const {return rightHandSide;}
	const std::vector<int> &getlhs() const {return leftHandSide;}

	bool isBinary() const { return  (leftHandSide.size()==1) && (rightHandSide.size()==1); }

    int getSupport() { return m_support; }
    void setSupport(int support) { m_support = support; }
    // relational operators
    bool operator==(const Implication &imp) const { return isEqual(imp); }
    bool operator!=(const Implication &imp) const { return !isEqual(imp); }
    bool isEqual(const Implication &imp) const
    {
    	return (leftHandSide==imp.leftHandSide) &&
    			(rightHandSide==imp.rightHandSide);
    }
    // map implication to another row/table using a vector
    void mapTo(const std::vector<int> &map)
    {
		for (unsigned int i = 0; i < leftHandSide.size(); i++)
			leftHandSide[i] = map[leftHandSide[i]];
		for (unsigned int i = 0; i < rightHandSide.size(); i++)
			rightHandSide[i] = map[rightHandSide[i]];
    }
};

//
// !!! Oren 1.6.17 -> Old code does not treat implications as an entity, vector operations scattered all over the code base
//                    We should refactor the code. The changes to Implication and the new Implications class are a step in that direction...
//

typedef std::vector<Implication> ImplicationVector;

class Implications
{
public:
   Implications() {} // default constructor - do nothing
   Implications(const Implications &imps)
   {
	   m_data = imps.m_data;
   }
   // conversion constructor
   Implications(const ImplicationVector &impVec)
   {
	   m_data = impVec;
   }
   // conversion operator
   operator std::vector<Implication> & () {return m_data; }
   // get size
   std::size_t size() { return m_data.size(); }
   // add implication/s
   void add(const Implication &imp) { m_data.push_back(imp); }
   void add(const Implications &imps)
   {
	   m_data.insert(m_data.end(), imps.m_data.begin(), imps.m_data.end());
   }

   int selectNewFromOld(const Implications &testImps, Implications &newImps)
   {
	      timestamp_print(); std::cout <<__FILE__<<__LINE__<<std::endl;
	      int newCount = 0;
		  // add each unique implication in list
		  for(auto imp : testImps.m_data)
		  {
		      timestamp_print(); std::cout <<__FILE__<<__LINE__<<std::endl;
			  auto itr = std::find(m_data.begin(),m_data.end(),imp);
		      timestamp_print(); std::cout <<__FILE__<<__LINE__<<std::endl;
			  // if we didn't find a match, add to list
			  if(itr==m_data.end())
			  {
				  newImps.add(imp);
				  newCount++;
			  }
//			  else // we have an existing instance lets add the support
//			  { // TODO Ask Kira about correct behavior
//				  itr->setSupport(itr->getSupport() + imp.getSupport());
//			  }
		  }
	      timestamp_print(); std::cout <<__FILE__<<__LINE__<<std::endl;
		  return newCount;
   }

   typedef std::multimap<int,Implication> ImplicationMap;

   inline int makeKey(const Implication &imp)
   {
	   const std::vector<int> &keyData = imp.getlhs();
	   int key = keyData[0];
	   if(keyData.size()>1)
		   key +=  (keyData[1]<<16);
	   return key;
   }

   ImplicationMap ImplicationsToMap()
   {
	   ImplicationMap impMap;
	   for(auto imp : m_data)
	   {
		   int key = makeKey(imp);//.getlhs()[0];
		   impMap.insert(std::pair<int,Implication>(key,imp));
	   }
	   return impMap;
   }

   int selectNewFrom(const Implications &testImps, Implications &newImps)
   {
	      //timestamp_print(); std::cout <<__FILE__<<__LINE__<<std::endl;
	      ImplicationMap impMap = ImplicationsToMap();
	      //timestamp_print(); std::cout <<__FILE__<<__LINE__<<std::endl;
	      //int printCounter = 0;
	      int newCount = 0;
		  // add each unique implication in list
		  for(auto imp : testImps.m_data)
		  {
		      //timestamp_print(); std::cout <<__FILE__<<__LINE__<<std::endl;
			  int key = makeKey(imp);//.getlhs()[0];
			  auto ret = impMap.equal_range(key);
			  // Total Elements in the range
			  //int count = std::distance(ret.first, ret.second);
			  ImplicationMap::iterator itr;
			  for (itr=ret.first; itr!=ret.second; ++itr)
				  if(itr->second==imp)
					  break;
			  // Total Elements in the range
			  //int posFound = count - std::distance(itr, ret.second) + 1;
			  //if(++printCounter%100000==0)
		        //{timestamp_print(); std::cout <<__FILE__<<__LINE__<<":"<<count<<":"<<posFound<<":"<<key<<std::endl;}
			  // if we didn't find a match, add to list
		      if(itr==ret.second)
		      {
		        //timestamp_print(); std::cout <<__FILE__<<__LINE__<<":"<<count<<":"<<posFound<<":"<<key<<std::endl;
			    newImps.add(imp);
			    newCount++;
		      }
		  }
	      //timestamp_print(); std::cout <<__FILE__<<__LINE__<<std::endl;
		  return newCount;
   }


   struct CycleEntry
   {
	   CycleEntry() : visitId(-1), canDelete(false), deleted(false) {}
	   // add
	   void add(int x) { rhs.push_back(x); }
	   void add(const std::vector<int> &v) { rhs.insert(rhs.begin(),v.begin(),v.end()); }
	   // data
	   int visitId;
	   bool canDelete;
	   bool deleted;
	   std::vector<int> rhs; //cycle pointer entries
	   //std::map<int,int>
   };
   typedef std::map<int,CycleEntry> CycleMap;

   class CycleChecker
   {
   public:
	   CycleChecker(CycleMap &cycleMap) :cycleMap(cycleMap) {}

	   bool begin()
	   {

	     // traverse and look for cycles
		 for(auto mapElement: cycleMap)
		 {
		   // only look for cycles that start in old links???
		   if(!mapElement.second.canDelete)
		   {
		     recCheck(mapElement.first,mapElement.first);

		   }
		 }
		 return true;
	   }

	   void recCheck(int currIndex, int orgIndex);

	   ~CycleChecker()
	   {

	   }
	   // data
	   CycleMap &cycleMap;
	   std::vector<int> visitList;
	   EquivalenceMap equivs;
   };


   int handleCycles(Implications &testImps, EquivalenceMap &newEquivs);

//   int addNewFrom(const Implications &imps)
//   {
//	      int newCount = 0;
//		  // add each unique implication in list
//		  for(auto imp : imps.m_data)
//		  {
//			  auto itr = std::find(m_data.begin(),m_data.end(),imp);
//			  // if we didn't find a match, add to list
//			  if(itr==m_data.end())
//			  {
//				  add(imp);
//				  newCount++;
//			  }
//			  else // we have an existing instance lets add the support
//			  { // TODO Ask Kira about correct behavior
//				  itr->setSupport(itr->getSupport() + imp.getSupport());
//			  }
//		  }
//		  return newCount;
//   }
   // syntactic sugar
//   Implications &operator += (const Implications &imps) { addNewFrom(imps); return *this;}
   // relational operators
   bool operator==(const Implications &imps) const { return isEqual(imps); }
   bool operator!=(const Implications &imps) const { return !isEqual(imps); }
   bool operator<(const Implications &imps) const { return isProperSubsetOf(imps); }
   bool operator<=(const Implications &imps) const { return isSubsetOf(imps); }

   bool isEqual(const Implications &imps) const
   {
   	  return m_data==imps.m_data;
   }
   bool isEmpty() const
   {
	   return m_data.empty();
   }

   //
   // check if all implications can be found in another list of implications
   //    Note: can be useful if we want to find out if an implication list contains any new implications relative to another list
   bool isSubsetOf(const Implications &imps) const
   {
	  // if our set is bigger then we can not be a subset
	  if(m_data.size()>imps.m_data.size())
		  return false;
	  // validate that each implication exists in list
	  //for(auto imp : imps.m_data)
	  for(auto imp : m_data)
	  {
		  //auto itr = std::find(m_data.begin(),m_data.end(),imp);
		  auto itr = std::find(imps.m_data.begin(),imps.m_data.end(),imp);
		  //if(itr==m_data.end())
		  if(itr==imps.m_data.end())
			  return false;
	  }
	  // if we are here we found all our implications inside imps
	  return true;
   }
   // is subset and not equal
   bool isProperSubsetOf(const Implications &imps) const
   {
	  // if our set is bigger or equal in size then we can not be a proper subset
	  if(m_data.size()>=imps.m_data.size())
		return false;
	  else
	    return isSubsetOf(imps);
   }

   // map implications to another row/table using a vector
   void mapTo(const std::vector<int> &map)
   {
	   std::for_each(m_data.begin(),m_data.end(),[&map](Implication &imp)
			   {
		         imp.mapTo(map);
			   }
	   );
   }

private:
   ImplicationVector m_data;
};

void testImplication();

#endif /*IMPLICATION_H_INCLUDED__  */
