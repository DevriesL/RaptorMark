/*============================================================================
  Int, an object-oriented C integer class.
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

#include <stdlib.h>

#include "Int.h"

IntClass *_IntClass = NULL;

static void Int_destroy (Int *self)
{
        DEBUG_DESTROY;

	if (!self)
		return;
	verifyCorrectClass(self,Int);

	clearObjectSelf;

	// NOTE: The releaser frees self.
}

static void Int_describe (Int* self, FILE *outputFile) 
{ 
	if (!self)
		return;
	verifyCorrectClass(self,Int);

	if (!outputFile)
		outputFile = stdout;

	fprintf (outputFile, "%s", self->is_a->className);
}

static long long Int_asLongLong (Int* self)
{ 
	if (!self)
		return 0;
	verifyCorrectClass(self,Int);
	return self->integer;
}

static double Int_asDouble (Int* self)
{ 
	if (!self)
		return 0;
	verifyCorrectClass(self,Int);
	return (double) self->integer;
}

static bool Int_asBool (Int* self)
{ 
	if (!self)
		return 0;
	verifyCorrectClass(self,Int);
	return 0 != self->integer;
}

IntClass* IntClass_prepare ()
{
	PREPARE_CLASS_STRUCT(Int,Object)

	SET_OVERRIDDEN_METHOD_POINTER(Int,describe);
	SET_OVERRIDDEN_METHOD_POINTER(Int,destroy);

	SET_METHOD_POINTER(Int,asLongLong);
	SET_METHOD_POINTER(Int,asDouble);
	SET_METHOD_POINTER(Int,asBool);
	
        VALIDATE_CLASS_STRUCT(_IntClass);
	return _IntClass;
}

Int* Int_init (Int *self)
{
	if (!_IntClass)
		IntClass_prepare ();

	ooc_bzero (self, sizeof(Int));
	Object_init ((Object*) self);

	self->is_a = _IntClass;
	self->integer = 0;

	return self;
}

Int *Int_new () 
{
	Int *self = allocate(Int);
	Int_init (self);
	return self;
}

Int* Int_newWithUnsignedLong (unsigned long value) {
	if (!_IntClass)
		IntClass_prepare ();
	Int *self = Int_new ();
	self->integer = (long long) value;
	return self;
}

Int *Int_newWithLongLong (long long value) {
	if (!_IntClass)
		IntClass_prepare ();
	Int *self = Int_new ();
	self->integer = value;
	return self;
}

Int *Int_newWithDouble (double value) {
	if (!_IntClass)
		IntClass_prepare ();
	Int *self = Int_new ();
	self->integer = (long long) value;
	return self;
}

Int* Int_newWithCString (const char* str)
{
	if (!_IntClass)
		IntClass_prepare ();
	Int *self = Int_new ();
	self->integer = str ? atoi (str) : 0;
	return self;
}

