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

#include "Array.h"
#include "MutableArray.h"

ArrayClass *_ArrayClass = NULL;

size_t Array_count (Array *self) 
{
	if (!self)
		return 0;
	verifyCorrectClasses(self,Array,MutableArray);
	
	return self->n; 
}

Object *Array_objectAtIndex (Array *self, long index) 
{
	if (!self)
		return NULL;
	verifyCorrectClasses(self,Array,MutableArray);

	if (index < 0 || index >= self->n)
		return NULL;
	return self->array [index];
}

Array *Array_newWithArray (Array* array) 
{
	Array *self = Array_new ();
	if (array) {
		size_t n = self->n = self->size = Array_count (array);
		unsigned nBytes = sizeof(void*) * self->size;
		self->array = (Object**) malloc(nBytes);
		for (long i=0; i < n ; i++) {
			Object *object = Array_objectAtIndex (array, i);
			retain(object);
			self->array[i] = Array_objectAtIndex (array, i);
		}
	}
	return self;
}

Object *Array_firstObject (Array* self) 
{
	if (!self)
		return NULL;
	verifyCorrectClasses(self,Array,MutableArray);
	if (!self->n)
		return NULL;

	return self->array [0];
}

Object *Array_lastObject (Array *self) 
{
	if (!self)
		return NULL;
	verifyCorrectClasses(self,Array,MutableArray);

	if (!self->n)
		return NULL;
	return self->array [self->n - 1];
}

bool Array_containsObject (Array *self, Object *object) 
{
	if (!self)
		return NULL;
	verifyCorrectClasses(self,Array,MutableArray);
	if (!self->n || !object)
		return false;
	int n = self->n;
	for (int i=0; i < n; i++) {
		if (object == self->array [i])
			return true;
	}
	return false;
}

static void Array_destroy (Array *self)
{
	DEBUG_DESTROY;
	if (!self)
		return;
	verifyCorrectClass(self,Array);

	Object **array = self->array;
	if (self->size && array) {
		free (array);
	}

	ooc_bzero (self, sizeof(Array));

	// NOTE: The releaser frees self.
}

static void Array_describe (Array* self, FILE *outputFile) 
{ 
	if (!self)
		return;
	verifyCorrectClass(self,Array);

	if (!outputFile)
		outputFile = stdout;

	fprintf (outputFile, "%s", self->is_a->className);
	fprintf (outputFile, "(%lu)", (unsigned long) self->n);
}

ArrayClass* ArrayClass_prepare ()
{
        PREPARE_CLASS_STRUCT(Array,Object)

	// Overridden methods
	SET_OVERRIDDEN_METHOD_POINTER(Array,describe);
	SET_OVERRIDDEN_METHOD_POINTER(Array,destroy);

	// Array methods
	SET_METHOD_POINTER(Array,count);
	SET_METHOD_POINTER(Array,firstObject);
	SET_METHOD_POINTER(Array,lastObject);
	SET_METHOD_POINTER(Array,objectAtIndex);
	SET_METHOD_POINTER(Array,containsObject);

	VALIDATE_CLASS_STRUCT(_ArrayClass);

        return _ArrayClass;
}

Array* Array_init (Array *self)
{
	if (!_ArrayClass)
		ArrayClass_prepare();

	Object_init ((Object*) self);

	self->is_a = _ArrayClass;
	self->size = 0;
	self->n = 0;
	self->array = NULL;

	return self;
}

Array *Array_new () 
{
	Array *self = (Array*) malloc(sizeof(Array));
	Array_init (self);
	return self;
}

Array *Array_newWithObject (Object *object) 
{
	Array *self = Array_new ();
	self->size = 1;
	self->n = 1;

	unsigned long nBytes = sizeof(void*);
	self->array = (Object**) malloc(nBytes);
	ooc_bzero ((void*) self->array, nBytes);
	self->array[0] = object;
	retain(object);
	return self;
}

