#include <iostream>
#include <string>
#include <cstdlib>
#include <iostream>
#include <vector>
#include <sstream>
#include "Implication.h"
#include "timestamp.h"
#include <algorithm>

// 1.23.18 refactor, improve performance -> avoid creating superfluous copies of vectors on each call
//Implication::Implication(std::vector<int>  lhs, std::vector<int>  rhs, int support)
Implication::Implication(const std::vector<int>  &lhs, const std::vector<int>  &rhs, const int support):
leftHandSide(lhs),
rightHandSide(rhs),
m_support(support)
{

	// 1.23.18 fix issue #15 -> Any permutation of elements on the left may occur in identical rules.
	// sort implications first so implication compare will allways do the right thing.
	if(leftHandSide.size()>1)
	  std::sort(leftHandSide.begin(),leftHandSide.end());
	////////////////////////
	//leftHandSide = lhs;
	//rightHandSide = rhs;
	//m_support = support;
}

int Implications::handleCycles(Implications &testImps, EquivalenceMap &newEquivs)
{
	   // save old marker
	   //std::size_t oldSize = m_data.size();

	   CycleMap cycleMap;

	   // copy old generation
	   for(auto imp : m_data)
	   {
       // skip if not binary
		  if(imp.isBinary())
		  {
			cycleMap[imp.getlhs()[0]].canDelete = false;
		    cycleMap[imp.getlhs()[0]].add(imp.getrhs()[0]);
		  }
	   }
	   // copy new generation
	   for(auto imp : testImps.m_data)
	   {
       // skip if not binary
		  if(imp.isBinary())
		  {
			cycleMap[imp.getlhs()[0]].canDelete = true;
		    cycleMap[imp.getlhs()[0]].add(imp.getrhs()[0]);
		  }
	   }

	   timestamp_print(); std::cout<<"**** Begin Handle Cycles(7'd)(3) ****"<< std::endl;
	   CycleChecker cc(cycleMap);
	   cc.begin();

	   if(!cc.equivs.empty())
		   std::cout<<"**** Cycle Count: "<<cc.equivs.size()<<std::endl;//<<" ****"<< std::endl;

	   // print cycles
	   for(auto equiv : cc.equivs)
	   {
		  std::cout<<equiv.first + 1<<"<->";
		  for(auto x: equiv.second)
		  {
			  std::cout<<x + 1<<" ";
			   // remove cycles
		       oslc::lul::erase_if(testImps.m_data,[x](const Implication &imp)
		    	   {
		             return (imp.isBinary() && imp.getlhs()[0]==x);
		    	   }
		         );

		  }
		  std::cout<<std::endl;
	   }

	   timestamp_print(); std::cout<<"**** End Handle Cycles(7'd)(3) ****"<< std::endl;

	   return 0;
}


std::string Implication::toString() {
	std::stringstream ss;
	for (unsigned int i = 0; i < leftHandSide.size(); i++) {
		ss << leftHandSide[i] << " ";
	}
	ss << ("-> ");
	for (unsigned int i = 0; i < rightHandSide.size(); i++) {
		ss << rightHandSide[i] << " ";
	}
	return ss.str();
}

void testImplication() {
	std::vector<int>  v1 =  std::vector<int>();
	std::vector<int>  v2 =  std::vector<int>();
	v1.push_back(2);
	v1.push_back(5);
	v1.push_back(3);
	v2.push_back(7);
	v2.push_back(8);
	v2.push_back(9);
	Implication i1 = Implication(v1, v2, 0);
	std::cout << i1.toString();
}

//
// Implications::CycleChecker functions
//

void Implications::CycleChecker::recCheck(int currIndex, int orgIndex)
{
	 // if we have a cycle
	 if(currIndex==orgIndex && !visitList.empty())
	 {
		 // pop the last element in cycle -> origin index
		 visitList.pop_back();
		 // TODO: should we assert visitList not empty?, this should only happen if we have x->x in the list and we should not
		 // save new equivalence
		 equivs[orgIndex].insert(equivs[orgIndex].begin(),visitList.begin(),visitList.end());//-1);
		 // optimization: delete all in list because they are equivalent to another
		 std::for_each(visitList.begin(),visitList.end(),[&](int index) //visitList.end()-1
				 {
			        if(cycleMap[index].canDelete)
			         //cycleMap[index].rhs.clear();
			         cycleMap[index].deleted = true;
				 }
		 );
		 // erase found vector
		 visitList.clear();
		 // go back
		 return;
	 }
	 // get cycle entry info for current index
  CycleEntry &ce = cycleMap[currIndex];
  // if we reached a deleted or empty slot
  if(ce.deleted || ce.rhs.empty())
 	 return;
  // traverse rhs list recursively
	 for(int rhsIndex :ce.rhs)
	 {
		 // check if we have secondary cycles in visit list - consider changing to map for performance...
		 //if(visitList.size()>1)
		 if(std::find(visitList.begin(),visitList.end(),rhsIndex)!=visitList.end())
			 continue;
		 // its new so lets check it ..
		 visitList.push_back(rhsIndex);
		 recCheck(rhsIndex,orgIndex);
		 if(!visitList.empty()) // is this a bug in STL??? calling this on empty causes double delete
			 visitList.pop_back();
	 }
}


