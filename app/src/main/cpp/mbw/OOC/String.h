/*============================================================================
  String, an object-oriented C string class.
  Copyright (C) 2009-2019 by Zack T Smith.

  Object-Oriented C is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
 
  Object-Oriented C is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.
 
  You should have received a copy of the GNU Lesser General Public License
  along with this software.  If not, see <http://www.gnu.org/licenses/>.

  The author may be reached at 1@zsmith.co.
 *===========================================================================*/

#ifndef _OOC_STRING_H
#define _OOC_STRING_H

#include <stdint.h>
#include <wchar.h>

#include "Object.h"

#define DECLARE_STRING_METHODS(TYPE_POINTER) \
	int (*length) (TYPE_POINTER); \
	wchar_t (*characterAt) (TYPE_POINTER, int); \
	void (*print) (TYPE_POINTER, FILE*); 

struct string;

typedef struct stringclass {
	DECLARE_OBJECT_CLASS_VARS
	DECLARE_OBJECT_METHODS(struct string*)
	DECLARE_STRING_METHODS(struct string*)
} StringClass;

extern StringClass *_StringClass;

#define DECLARE_STRING_INSTANCE_VARS(TYPE_POINTER) \
	wchar_t *_characters; \
	int _length;

typedef struct string {
	StringClass *is_a;
	DECLARE_OBJECT_INSTANCE_VARS(struct string*)
	DECLARE_STRING_INSTANCE_VARS(struct string*)
} String;

extern String* String_init (String* object);
extern String* String_newWithCString (const char*);
extern String* String_initWithCString (String* self, const char *str);
extern StringClass* StringClass_prepare ();

// Inherited by MutableString
extern int String_length (String* self);
extern void String_describe (String* self, FILE *file);
extern void String_print (String *self, FILE* file);
extern wchar_t String_characterAt (String *self, int);

#endif

