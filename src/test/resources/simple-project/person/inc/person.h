#ifndef _SIMPLE_PROJECT_PERSON_H_
#define _SIMPLE_PROJECT_PERSON_H_

#include "id.h"

/**
 * Copyright
 *
 * My licence
 *
 */
// TODO: Check this
namespace mycompany {

template<typename T>
struct PersonId {
	T m_low;
	T m_high;
};

}

/**
 * FIXME: Document this class
 */
template<typename T>
class Person {
public:
	Person() {}
	virtual ~Person() {}

private:
	/**
	 * ToDO: Document this.
	 */
	mycompany::Gender m_gender;

	LegacyPersonId* m_legacyId;

	mycompany::PersonId<T> m_id;
};

class NotCopyable {
public:
	NotCopyable() {}
	virtual ~NotCopyable() {}

protected:
	NotCopyable(const NotCopyable&);
	NotCopyable& operator=(const NotCopyable&);
};

#if defined (__vax__) || defined (__ns16000__)
#endif

#endif
