/*============================================================================
  MutableArray, an object-oriented C mutable array class.
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

MutableArrayClass *_MutableArrayClass = NULL;

static void MutableArray_describe (MutableArray* self, FILE *outputFile) 
{ 
	if (!self)
		return;
	verifyCorrectClass(self, MutableArray);

	if (!outputFile)
		outputFile = stdout;

	fprintf (outputFile, "%s", self->is_a->className);
	fprintf (outputFile, "(%lu)", (unsigned long) self->n);
}

static Object *MutableArray_removeObjectAtIndex (MutableArray* self, long index) 
{
	if (!self)
		return NULL;
	verifyCorrectClass(self, MutableArray);

	size_t count = self->n;
	if (index < 0 || index >= count)
		return NULL; // XX
	if (index == count-1) {
		Object *object = self->array [count-1];
		release(object);
		self->array [count-1] = NULL;
		self->n--;
		return object;
	}

	Object *object = self->array [index];
	int i = index;
	while (i <= count-2) {
		self->array [i] = self->array [i+1];
		i++;
	}
	self->array [i+1] = NULL;
	self->n--;
	release(object);
	return object;
}

static Object *MutableArray_removeObject (MutableArray* self, Object *object) 
{ 
	if (!self)
		return NULL;
	if (!object)
		return NULL;
	verifyCorrectClass(self, MutableArray);

	size_t count = self->n;
	if (!count)
		return NULL;

	size_t where = 0;
	bool found = false;

	for (int index=0; index < count; index++) {
		if (object == self->array[index]) {
			where = index;
			found = true;
			break;
		}
	}
	if (!found) {
		warning (__FUNCTION__, "OBJECT NOT IN ARRAY\n");
		return NULL;
	}

	return MutableArray_removeObjectAtIndex (self, where);
}

static Object *MutableArray_removeFirstObject (MutableArray* self) 
{
	if (!self)
		return NULL;
	verifyCorrectClass(self, MutableArray);

	if (self->n == 0)
		return NULL;
	return MutableArray_removeObjectAtIndex (self, 0);
}

static Object *MutableArray_removeLastObject (MutableArray* self) 
{
	if (!self)
		return NULL;
	verifyCorrectClass(self, MutableArray);

	size_t count = self->n;
	if (!count)
		return NULL;
	
	return MutableArray_removeObjectAtIndex (self, count-1);
}

static void MutableArray_removeAllObjects (MutableArray* self) 
{ 
	if (!self)
		return;
	verifyCorrectClass(self, MutableArray);

	Object **array = self->array;
	if (!self->array)
		return;

	size_t count = self->n;
	if (count) {
		for (unsigned i = 0; i < count; i++) {
			Object *object = array[i];
			release(object);
			array[i] = NULL;
		}
	}
	self->n = 0; 

	size_t nBytes = sizeof(Object*) * self->size;
	if (nBytes)
		ooc_bzero ((void*) self->array, nBytes);
}

#if 0
static void MutableArray_printJSON (MutableArray *self, FILE *outputFile)
{
	if (!self)
		return;
	verifyCorrectClass(self,MutableArray);

	if (!outputFile)
		outputFile = stdout;

	fprintf (outputFile, "[ ");

	size_t count = self->n;
	for (size_t index = 0; index < count; index++) {
		Object *object = self->array[index];
		if (!object)
        		fprintf (outputFile, "null"); // XX if missing object it's maybe defective array.
		else if (!object->printJSON)
        		fprintf (outputFile, "null"); // XX if missing object->printJSON it's a defective Object
		else 
			object->printJSON (object, outputFile);
		
		fprintf (outputFile, ", ");
	}

	fprintf (outputFile, "],\n");
}

#endif

static void MutableArray_appendObject (MutableArray* self, Object* object) 
{
	if (!self)
		error_null_parameter (__FUNCTION__);
	if (!object)
		error_null_parameter (__FUNCTION__);
	verifyCorrectClass(self,MutableArray);

//printf ("appended obj %s retaincount=%d \n", object->is_a->className, object->retainCount);
	retain(object);
//printf ("-> appended obj %s retaincount=%d \n", object->is_a->className, object->retainCount);

	size_t count = self->n;
	if (count >= self->size) {
		self->size *= 2;
		self->array = realloc (self->array, self->size * sizeof(Object*));
	}

	self->array [count++] = object;
	self->n = count;
}

static void MutableArray_insertObjectAtIndex (MutableArray* self, Object *object, long index) 
{
	if (!self)
		return;
	verifyCorrectClass(self,MutableArray);
	if (!object)
		error_null_parameter (__FUNCTION__);

	size_t count = self->n;
	if (index >= count) {
		MutableArray_appendObject (self, object);
		return;
	} 
	if (index < 0) {
		index = 0;
	}

	retain(object);

	if (1+count >= self->size) {
		self->size *= 2;
		self->array = realloc (self->array, self->size * sizeof(Object*));
	}

	if (count >= self->size) {
		// ERROR: FULL
		error (__FUNCTION__, "ARRAY IS FULL"); // XX
		exit (-1);
	} 

	for (int i = count; i > index; i++) {
		self->array[i] = self->array[i-1];
	}
	self->array[index] = object;
}

static void MutableArray_destroy (MutableArray *self)
{
        DEBUG_DESTROY;

	if (!self)
		return;
	verifyCorrectClass(self,MutableArray);

	Object **array = self->array;
	if (self->size && array) {
		MutableArray_removeAllObjects (self);
		free (array);
	}

	clearObjectSelf;
	
	// NOTE: The releaser frees self.
}

MutableArrayClass* MutableArrayClass_prepare ()
{
        PREPARE_CLASS_STRUCT(MutableArray,Array);

        SET_OVERRIDDEN_METHOD_POINTER(MutableArray,describe);
        SET_OVERRIDDEN_METHOD_POINTER(MutableArray,destroy);

	SET_INHERITED_METHOD_POINTER(MutableArray,Array,count);
	SET_INHERITED_METHOD_POINTER(MutableArray,Array,containsObject);
	SET_INHERITED_METHOD_POINTER(MutableArray,Array,firstObject);
	SET_INHERITED_METHOD_POINTER(MutableArray,Array,lastObject);
	SET_INHERITED_METHOD_POINTER(MutableArray,Array,objectAtIndex);

	SET_METHOD_POINTER(MutableArray,appendObject);
	SET_METHOD_POINTER(MutableArray,removeObject);
	SET_METHOD_POINTER(MutableArray,removeObjectAtIndex);
	SET_METHOD_POINTER(MutableArray,removeAllObjects);
	SET_METHOD_POINTER(MutableArray,removeFirstObject);
	SET_METHOD_POINTER(MutableArray,removeLastObject);
	SET_METHOD_POINTER(MutableArray,insertObjectAtIndex);

	VALIDATE_CLASS_STRUCT(_MutableArrayClass);
        return _MutableArrayClass;
}

MutableArray *MutableArray_init (MutableArray* self) 
{
	if (!_MutableArrayClass)
		MutableArrayClass_prepare();
	if (!self)
		return NULL;

	ooc_bzero (self, sizeof(MutableArray));
	Object_init ((Object*) self);

	self->is_a = _MutableArrayClass;

#define DEFAULT_ARRAY_SIZE (32)
	self->size = DEFAULT_ARRAY_SIZE;
	self->n = 0;
	size_t nBytes = sizeof(Object*) * self->size;
	self->array = (Object**) malloc(nBytes);
	ooc_bzero ((void*) self->array, nBytes);

	return self;
}

MutableArray *MutableArray_new ()
{
	if (!_MutableArrayClass)
		MutableArrayClass_prepare();
        MutableArray *self = (MutableArray*) malloc(sizeof(MutableArray));
        MutableArray_init (self);
        return self;
}

