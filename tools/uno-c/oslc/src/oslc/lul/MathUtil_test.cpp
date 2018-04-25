/*
 * MathUtil.cpp
 *
 *  Created on: Feb 22, 2018
 *      Author: admin
 */

#include <oslc/lul/TestHarness.h>

#include "MathUtil.h"

namespace oslc {
namespace lul {


REGISTER_TEST("MathUtil","test 1")
{
    TEST_EXP(math::fact(5)==120);
    LOG_TEST_MSG("math::genCombinationList(5,3)");
    math::Int2DVector vect2D = math::genCombinationList(5,3);
    LOG_TEST_MSG("Number of combinations:"<<vect2D.size());
    TEST_EXP(math::comb(5,3)==vect2D.size());
    for(auto x: vect2D)
      //for(auto y: x)
    	LOG_TEST_MSG(x[0]<<","<<x[1]<<","<<x[2]);

	int res = math::comb(10,2);
	LOG_TEST_MSG("math::comb(10,2)=="<<res);

    TEST_EXP(math::comb(21,2)==210);

	TEST_EXP(math::comb(22,2)==231);

    TEST_EXP(math::comb(22,4)==7315);

    TEST_EXP(math::comb(100,2)==4950);

    TEST_EXP(math::comb(100,4)==3921225);


	return true;

}

} /* namespace lul */
} /* namespace oslc */

