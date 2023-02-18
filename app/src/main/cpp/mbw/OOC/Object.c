/*============================================================================
  Object, an object-oriented C object base class.
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

#include <stdio.h>
#include <stdlib.h>

#include "Object.h"

ObjectClass *_ObjectClass = NULL;

void Object_destroy (Object* self)
{
        DEBUG_DESTROY;

	verifyCorrectClass(self,Object);

	clearObjectSelf;
}

static void 
Object_describe (Object* self, FILE* output)
{
	verifyCorrectClass(self,Object);
	fprintf (output ?: stdout, "%s", self->is_a->className);
}

ObjectClass* ObjectClass_prepare ()
{
	PREPARE_CLASS_STRUCT(Object,Object)

	_ObjectClass = (ObjectClass*) malloc(sizeof(ObjectClass));
	ooc_bzero (_ObjectClass, sizeof(ObjectClass));

	SET_METHOD_POINTER(Object,destroy);
	SET_METHOD_POINTER(Object,describe);

	VALIDATE_CLASS_STRUCT(_ObjectClass);
	return _ObjectClass;
}

Object* Object_init (Object *object)
{
	if (!_ObjectClass)
		ObjectClass_prepare();

	object->is_a = _ObjectClass;

	return object;
}

