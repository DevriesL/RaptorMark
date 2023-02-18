/*============================================================================
  Array, an object-oriented C array class.
  Copyright (C) 2019 by Zack T Smith.

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

#ifndef _OOC_ARRAY_H
#define _OOC_ARRAY_H

#include <stdlib.h>
#include <stdbool.h>

#include "Object.h"

#define DECLARE_ARRAY_INSTANCE_VARS(FOO) \
	size_t n;\
	size_t size;\
	Object **array;

#define DECLARE_ARRAY_METHODS(TYPE_POINTER) \
	size_t (*count) (TYPE_POINTER); \
	Object* (*objectAtIndex) (TYPE_POINTER, long); \
	Object* (*firstObject) (TYPE_POINTER); \
	Object* (*lastObject) (TYPE_POINTER); \
	bool (*containsObject) (TYPE_POINTER, Object *object); 

struct array;

typedef struct arrayclass {
	DECLARE_OBJECT_CLASS_VARS
        DECLARE_OBJECT_METHODS(struct array*)
        DECLARE_ARRAY_METHODS(struct array*)
} ArrayClass;

extern ArrayClass *_ArrayClass;

typedef struct array {
        ArrayClass *is_a;
	DECLARE_OBJECT_INSTANCE_VARS(struct array*)
	DECLARE_ARRAY_INSTANCE_VARS(struct array*)
} Array;

extern Array *Array_new ();
extern Array *Array_init (Array *self);
extern Array* Array_newWithObject (Object *object);
extern Array* Array_newWithArray (Array *arrayToCopy);

// These have to be listed here so that MutableArray can inherit them.
size_t Array_count (Array *);
extern Object* Array_objectAtIndex (Array *, long);
extern bool Array_containsObject (Array *self, Object *object);
extern Object* Array_firstObject (Array *);
extern Object* Array_lastObject (Array *);

#endif
