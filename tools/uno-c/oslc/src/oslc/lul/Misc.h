/*
 * Misc.h
 *
 *  Created on: Dec 4, 2017
 *      Author: admin
 */

#ifndef SRC_OSLC_LUL_MISC_H_
#define SRC_OSLC_LUL_MISC_H_

#include <vector>
#include <algorithm>

namespace oslc {
namespace lul {

//
// create a stringify macro -> missing in GCC
// useful for #pragma message among other things
// example: #pragma message ("This variable =" STRINGIFY(_MY_VAR))
//

#define XSTRINGIFY(s) #s
#define STRINGIFY(s) XSTRINGIFY(s)

#define CONCAT_IMPL( x, y ) x##y
#define MACRO_CONCAT( x, y ) CONCAT_IMPL( x, y )

//
// usage example: GEN_STR_CALL_FUNC("hello "<<12345,LOG_FUNC);
//
#define GEN_STR_CALL_FUNC_BASE(tempStrm,s,f) \
  std::stringstream tempStrm; \
  tempStrm<<s; \
  f(tempStrm.str())

#define GEN_STR_CALL_FUNC(s,f) GEN_STR_CALL_FUNC_BASE(MACRO_CONCAT(gen_str_call_auto_var,__COUNTER__),s,f)


//
// template helpers
//

template<class T>
struct is_vector {
  static bool const value = false;
};

template<class T>
struct is_vector<std::vector<T> > {
  static bool const value = true;
};

//
// Erase container elements for which pred function returns true
// based on: http://en.cppreference.com/w/cpp/experimental/vector/erase_if
//
template <class Container, class Predicate>
void erase_if(Container& c, Predicate pred)
{
	c.erase(std::remove_if(c.begin(), c.end(), pred), c.end());
}

} /* namespace lul */
} /* namespace oslc */

#endif /* SRC_OSLC_LUL_MISC_H_ */
