/*============================================================================
  String, an object-oriented C string class.
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
#include <wchar.h>

#include "String.h"
#include "MutableString.h"

StringClass *_StringClass = NULL;

#define kDefaultStringSize (32)

int String_length (String* self)
{
	verifyCorrectClasses(self,String,MutableString);
	return self->_length;
}

void String_describe (String* self, FILE *file)
{
	if (!self)
		return;
	verifyCorrectClasses(self,String,MutableString);
	
	fprintf (file ?: stdout, "%S\n", self->_characters);
}

void String_print (String *self, FILE* file)
{
	if (!self)
		return;
	verifyCorrectClasses(self,String,MutableString);
	if (!file)
		file = stdout;
	fprintf (file, "%S", self->_characters);
}

static void String_destroy (String* self)
{
        DEBUG_DESTROY;

	verifyCorrectClass(self,String);
	
	if (self->_characters) {
		ooc_bzero (self->_characters, sizeof(wchar_t) * (self->_length+1));
		free (self->_characters);
	}

	clearObjectSelf;
}

String* String_init (String* self)
{
	if (self) {
		if (!_StringClass)
			StringClass_prepare();

		self->is_a = _StringClass;

		self->_length = 0;
		self->_characters = NULL; // malloc (sizeof(wchar_t) * (self->_length+1)); // Empty string.
	}
	return self;
}

String* String_initWithCString (String* self, const char *str)
{
	if (self) {
		if (!_StringClass)
			StringClass_prepare();

		self->is_a = _StringClass;

		int len = str ? ooc_strlen (str) : 0;
		self->_length = len;
		self->_characters = malloc (sizeof(wchar_t) * (self->_length+1)); 
		if (str) {
			for (int i=0; i < len; i++)
				self->_characters[i] = str[i];
		}
	}
	return self;
}

wchar_t String_characterAt (String *self, int index)
{
	if (!self)
		return 0;
	verifyCorrectClasses(self,String,MutableString);
	if (index < self->_length) {
		return self->_characters[index];
	}
	return 0;
}

String* String_newWithCString (const char* str)
{
	String* obj = new(String);
	return String_initWithCString (obj, str);
}

StringClass* StringClass_prepare ()
{
	PREPARE_CLASS_STRUCT(String,Object)

	SET_OVERRIDDEN_METHOD_POINTER(String,describe);
        SET_OVERRIDDEN_METHOD_POINTER(String,destroy);

        SET_METHOD_POINTER(String,length);
        SET_METHOD_POINTER(String,print);
	SET_METHOD_POINTER(String,characterAt);
	
	VALIDATE_CLASS_STRUCT(_StringClass);
	return _StringClass;
}

