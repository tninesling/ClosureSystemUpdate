/*
 * MathUtil.h
 *
 *  Created on: Jan 17, 2018
 *      Author: admin
 */

#ifndef SRC_OSLC_LUL_MATHUTIL_H_
#define SRC_OSLC_LUL_MATHUTIL_H_

#include <vector>
#include <algorithm>

namespace oslc {
namespace lul {
namespace math {

// calculate factorial of N
// !!! 2.22.18 note -> lookout when using this, overflows on 64 bit very fast -> 21! = 51090942171709440000
inline unsigned long fact(unsigned long N)
{
    unsigned long res = 1;
    for(unsigned long i = 1; i<=N; i++)
        res *= i;
    return res;
}

// Based on code/discussion at:
// https://stackoverflow.com/questions/1838368/calculating-the-amount-of-combinations
// calculate number of combinations N/K
inline unsigned long long comb(unsigned long long n, unsigned long long k)
{
    if (k > n)
        return 0;
    unsigned long long r = 1;
    for (unsigned long long d = 1; d <= k; ++d)
    {
        r *= n--;
        r /= d;
    }
    return r;
}


// Based on code/discussion at:
// https://stackoverflow.com/questions/12991758/creating-all-possible-k-combinations-of-n-items-in-c
// http://rosettacode.org/wiki/Rosetta_Code
typedef std::vector<std::vector <int> > Int2DVector;

inline Int2DVector genCombinationList(int N, int K)
{
	// 2.22.18 bug fix -> overflows easily!!!
	//int numOfComb = fact(N)/(fact(K)*fact(N-K));
	int numOfComb = comb(N,K);

	Int2DVector combList(numOfComb);

    std::vector<bool> bitmask(K, 1); // K leading 1's
    bitmask.resize(N, 0); // N-K trailing 0's

    // store all combinations
    for(int i=0;i<numOfComb;i++)
    {
        for (int j = 0; j < N; j++)
        {
            if (bitmask[j]) combList[i].push_back(j);
        }
        // get next permutation
        std::prev_permutation(bitmask.begin(), bitmask.end());
    }

    return combList;
}

} /* namespace lul */
} /* namespace oslc */
} /* namespace math */


#endif /* SRC_OSLC_LUL_MATHUTIL_H_ */
