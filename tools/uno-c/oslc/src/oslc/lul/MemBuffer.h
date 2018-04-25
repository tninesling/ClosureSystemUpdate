/*
 * MemBuffer.h
 *
 *  Created on: Jan 8, 2018
 *      Author: admin
 */

#ifndef SRC_OSLC_LUL_MEMBUFFER_H_
#define SRC_OSLC_LUL_MEMBUFFER_H_


#include <streambuf>
#include <istream>

namespace oslc {
namespace lul {

//
// memory buffers for serialization etc.
//
// based on ideas from: https://stackoverflow.com/questions/13059091/creating-an-input-stream-from-constant-memory


//struct membuf : std::streambuf
//{
//    membuf(char* begin, char* end) {
//        this->setg(begin, begin, end);
//    }
//};


//struct membuf: std::streambuf {
//    membuf(char const* base, size_t size) {
//        char* p(const_cast<char*>(base));
//        this->setg(p, p, p + size);
//    }
//};
//
//struct imemstream: virtual membuf, std::istream {
//    imemstream(char const* base, size_t size)
//        : membuf(base, size)
//        , std::istream(static_cast<std::streambuf*>(this)) {
//    }
//};


//struct omemstream: virtual membuf, std::ostream {
//    imemstream(char const* base, size_t size)
//        : membuf(base, size)
//        , std::istream(static_cast<std::streambuf*>(this)) {
//    }
//};

class MemBuffer : public std::streambuf
{
public:
	MemBuffer(char* begin, char* end)
    {
		setg(begin, begin, end);
	}
	~MemBuffer()
	{
		// TODO Auto-generated destructor stub
	}
};

} /* namespace lul */
} /* namespace oslc */

#endif /* SRC_OSLC_LUL_MEMBUFFER_H_ */
