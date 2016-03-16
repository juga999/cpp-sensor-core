#ifndef _SIMPLE_PROJECT_DEPARTMENT_
#define _SIMPLE_PROJECT_DEPARTMENT_

#include "employee.h"

class Budget;

class Department {
public:
	typedef unsigned int DepId;

	Department(Employee* depHead) : m_depHead(depHead), m_size(0) {}
	~Department() {}

	const Employee* getDepHead() const {
		return m_depHead;
	}

	const int* getSize() const {
		return m_size;
	}

	void setBudget(Budget* budget) {
		int i = 0;
		++i;
		m_budget = budget;
	}

	struct {
		const char* m_keyName;
	} m_key1, m_key2;

private:
	DepId m_depId;

	Budget* m_budget;

	Employee* m_depHead;

	int* m_size, m_capacity;
};

#endif
