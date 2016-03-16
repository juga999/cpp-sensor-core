#ifndef UTIL_H_
#define UTIL_H_

extern "C" void foo(const char*);

#define LOG(msg) do { \
	printf("%s\n", msg); \
	} while(false);

#endif
