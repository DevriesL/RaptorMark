/*============================================================================
  Console, an object-oriented C console I/O class.
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
#include <stdarg.h>

#include "Console.h"

ConsoleClass *_ConsoleClass = NULL;

static Console *singleton = NULL;

Console *Console_singleton ()
{
	if (!singleton)
		singleton = Console_new ();
	return singleton;
}

static void Console_destroy (Console *self)
{
        DEBUG_DESTROY;

	if (!self)
		return;
	verifyCorrectClass(self,Console);

	clearObjectSelf;

	// NOTE: The releaser frees self.
}

static void Console_describe (Console* self, FILE *outputFile) 
{ 
	if (!self)
		return;
	verifyCorrectClass(self,Console);

	if (!outputFile)
		outputFile = stdout;

	fprintf (outputFile, "%s", self->is_a->className);
}

static void Console_flush (Console* self)
{ 
	if (!self)
		return;
	verifyCorrectClass(self,Console);

	fflush (stdout);
}

static void Console_newline (Console* self)
{ 
	if (!self)
		return;
	verifyCorrectClass(self,Console);

	putchar ('\n');
}

static void Console_printf (Console* self, const char *format, ...)
{ 
	if (!self)
		return;
	verifyCorrectClass(self,Console);

#define kMaxVarargsStringLength (8192)
	char buffer [kMaxVarargsStringLength];
	va_list args;
	va_start (args, format);
	vsnprintf (buffer, sizeof(buffer)-1, format, args);
	va_end (args);
	printf ("%s", buffer);
}

static void Console_print (Console* self, const char *string)
{ 
	if (!self)
		return;
	verifyCorrectClass(self,Console);
	printf ("%s", string);
}

static void Console_print_int (Console* self, int value)
{
	if (!self)
		return;
	verifyCorrectClass(self,Console);

	printf ("%d", value);
}

static void Console_print_unsigned (Console* self, unsigned value)
{
	if (!self)
		return;
	verifyCorrectClass(self,Console);

	printf ("%u", value);
}


static void Console_println (Console* self, const char *string)
{ 
	if (!self)
		return;
	verifyCorrectClass(self,Console);
	printf ("%s\n", string);
}

ConsoleClass* ConsoleClass_prepare ()
{
	PREPARE_CLASS_STRUCT(Console,Object)

	SET_OVERRIDDEN_METHOD_POINTER(Console,describe);
	SET_OVERRIDDEN_METHOD_POINTER(Console,destroy);

	SET_METHOD_POINTER(Console,newline);
	SET_METHOD_POINTER(Console,flush);
	SET_METHOD_POINTER(Console,printf);
	SET_METHOD_POINTER(Console,println);
	SET_METHOD_POINTER(Console,print);
	SET_METHOD_POINTER(Console,print_int);
	SET_METHOD_POINTER(Console,print_unsigned);
	
        VALIDATE_CLASS_STRUCT(_ConsoleClass);
	return _ConsoleClass;
}

Console* Console_init (Console *self)
{
	if (!_ConsoleClass)
		ConsoleClass_prepare ();

	ooc_bzero (self, sizeof(Console));
	Object_init ((Object*) self);

	self->is_a = _ConsoleClass;

	return self;
}

Console *Console_new () 
{
	Console *self = allocate(Console);
	Console_init (self);
	return self;
}

