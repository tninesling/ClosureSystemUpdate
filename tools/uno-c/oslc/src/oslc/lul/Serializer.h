/*
 * Serializer.h
 *
 *  Created on: Feb 15, 2018
 *      Author: admin
 */

#ifndef SRC_OSLC_LUL_SERIALIZER_H_
#define SRC_OSLC_LUL_SERIALIZER_H_

namespace oslc {
namespace lul {

template<class Stream>
class Serializer
{
public:
	Serializer(Stream &strm): strm(strm) {  }

	~Serializer() {}

	template<class Type>
	void load(Type &data)
	{
	  strm>>data;
	}
	template<class Type>
	void store(Type &data)
	{
	  strm<<data;
	}
	// data
	Stream &strm;
};

} /* namespace lul */
} /* namespace oslc */

#endif /* SRC_OSLC_LUL_SERIALIZER_H_ */
