#ifndef _SIMPLE_PROJECT_PERSON_ID_H_
#define _SIMPLE_PROJECT_PERSON_ID_H_

extern "C" {

/**
 * Id of a person.
 * As a legacy struct definition.
 */
typedef struct {
	unsigned int m_low;
	unsigned int m_high;
} LegacyPersonId;

}

namespace mycompany {

// Gender of a person
enum Gender {
	MALE = 0,
	FEMALE = 1,
};

}

#endif
