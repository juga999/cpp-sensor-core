
/**
 * 123456
 */
#include "employee.h"

#include "dummy.h"

#define LOG(msg) do { \
	printf("%s\n", msg); \
	} while(false);

/**
 * constructor
 */
Employee::Employee()
: m_dptId('\0') {
	LOG("Employee()")
	m_coordinates[0].m_lat = 0;
	m_coordinates[0].m_long = 0;
}

/**
 * Employee destructor
 */
Employee::~Employee()
{
	int i = 0;

	++i;
}

bool Employee::setLocation(const Coordinates* const coordinates, const double& defaultValue)
{
	int i = 0;
	if (coordinates !=  null) {
		m_location.setDefaultCoordinates(coordinates);
		i = 1;
	} else {
		Coordinates c;
		c.m_lat = defaultValue;
		c.m_long = defaultValue;
		m_location.setDefaultCoordinates(&c);
		i = 2;
	}

	return (i > 0);
}
