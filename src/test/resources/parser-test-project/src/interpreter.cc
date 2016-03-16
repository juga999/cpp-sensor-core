#include "Python.h"

class Interpreter : public AbstractInterpreter
{

};

class PythonInterpreter : public ns::Python {
private:
	int m_state;
};



int parse(PythonInterpreter* interpreter, const char* data) {
	return 0;
}

void release(PythonInterpreter* interpreter) {
}
