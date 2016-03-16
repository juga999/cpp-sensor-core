#ifndef _SIMPLE_PROJECT_EMPLOYEE_H_
#define _SIMPLE_PROJECT_EMPLOYEE_H_

#include "person.h"

#include <assert.h>

struct Coordinates {
	double m_lat;
	double m_long;
};

typedef struct {
	int m_lat;
	int m_long;
} LegacyCoordinates;

template<typename T, unsigned int DIM>
class Location {
public:
	Location() {}
	virtual ~Location() {}

	void setDefaultCoordinates(const T* def) {
		m_coordinates[0].m_lat = def.m_lat;
		m_coordinates[0].m_long = def.m_long;
	}

private:
	T m_coordinates[DIM];
};


class Employee : public Person<int>, NotCopyable {
public:
	/**
	 * Employee constructor.
	 */
	Employee();

	virtual ~Employee();

	virtual bool isManager() const {
		return false;
	}

	friend class Company;

	bool setLocation(const Coordinates* const, const double&);

private:
	unsigned int m_dptId;

	Location<Coordinates, 2> m_location;
	Location<LegacyCoordinates, 2> m_legacyLocation;
};

#endif
