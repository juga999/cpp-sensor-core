// Some file
#include "util.h"

/**
 * Some macro
 */
#define ASSERT(msg) do { \
	printf("%s\n", msg); \
	} while(false);

void foo(const char* msg) {
	ASSERT(msg);
	LOG(msg)
}
