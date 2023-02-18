/*============================================================================
  Double, an object-oriented C floating point class.
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

#include "Double.h"

DoubleClass *_DoubleClass = NULL;

DoubleClass* DoubleClass_prepare();

Double *Double_new () 
{
	if (!_DoubleClass)
		DoubleClass_prepare ();
	Double *self = new(Double);
	Double_init (self);
	return self;
}

Double *Double_newWithLongLong (long long value) {
	if (!_DoubleClass)
		DoubleClass_prepare ();
	Double *self = Double_new ();
	self->floatingPoint = value;
	return self;
}

Double *Double_newWithDouble (double value) {
	if (!_DoubleClass)
		DoubleClass_prepare ();
	Double *self = Double_new ();
	self->floatingPoint = value;
	return self;
}

static void Double_destroy (Double *self)
{
        DEBUG_DESTROY;

	if (!self)
		return;
	verifyCorrectClass(self,Double);
	
	clearObjectSelf;

	// NOTE: The releaser frees self.
}

static void Double_describe (Double* self, FILE *outputFile) 
{ 
	if (!self)
		return;
	verifyCorrectClass(self,Double);

	if (!outputFile)
		outputFile = stdout;

	fprintf (outputFile, "%s", self->is_a->className);
}

static long long Double_asLongLong (Double* self)
{ 
	if (!self)
		return 0;
	verifyCorrectClass(self,Double);
	return (long long) floor (self->floatingPoint);
}

static double Double_asDouble (Double* self)
{ 
	if (!self)
		return 0;
	verifyCorrectClass(self,Double);
	return self->floatingPoint;
}

static bool Double_asBool (Double* self)
{ 
	if (!self)
		return 0;
	verifyCorrectClass(self,Double);
	return 0. != self->floatingPoint;
}

Double* Double_init (Double *self)
{
	ooc_bzero (self, sizeof(Double));
	Object_init ((Object*) self);

	self->is_a = _DoubleClass;
	self->floatingPoint = 0.;

	return self;
}

DoubleClass* DoubleClass_prepare ()
{
	PREPARE_CLASS_STRUCT(Double,Object)

	SET_OVERRIDDEN_METHOD_POINTER(Double,describe);
	SET_OVERRIDDEN_METHOD_POINTER(Double,destroy);

	SET_METHOD_POINTER(Double,asLongLong);
	SET_METHOD_POINTER(Double,asDouble);
	SET_METHOD_POINTER(Double,asBool);
	
	VALIDATE_CLASS_STRUCT(_DoubleClass);
	return _DoubleClass;
}

