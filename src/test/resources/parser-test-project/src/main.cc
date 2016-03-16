#include "id.h"
#include "employee.h"

#ifndef __DEBUG__
#define __DEBUG__
int DEBUG = 0;
#endif

int main()
{
	LegacyPersonId id;
	id.m_low = 0;
	id.m_high = 0;

	if (DEBUG == 1) {
		Employee* emp = new Employee();
		delete emp;
	}

	return 0;
}
