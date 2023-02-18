/*============================================================================
  ObjectOriented, supporting routines.
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
#include <stdint.h>

#include "ObjectOriented.h"

uint64_t g_totalObjectAllocations = 0;
uint64_t g_totalObjectDeallocations = 0;

typedef struct {
	void *next;
	char *name;
} ClassLike;

uint64_t g_totalClassAllocations = 0;
ClassLike *firstClass = NULL;
ClassLike *lastClass = NULL;

void registerClass (void *class)
{
	if (!firstClass) {
		firstClass = class;
	} else {
		lastClass->next = class;
	}
	lastClass = class;
	((ClassLike*) class)->next = NULL;
	g_totalClassAllocations++;

#ifdef GRATUITOUS_DEBUGGING
	printf ("Registering class %s\n", (((ClassLike*) class)->name));
#endif
}

void deallocateClasses ()
{
	ClassLike *class = firstClass;
	while (class) {
		ClassLike *next = class->next;
		printf ("Deallocating class %s\n", class->name);
		free (class);
		class = next;
	}
}

unsigned long ooc_strlen(const char *start)
{
	if (!start)
		return 0;
	
	char *ptr = (char*) start;
	unsigned long len = 0;
	while (*ptr) {
		len++;
		ptr++;
	}
	return len;
}

void ooc_strncpy (const char *destination, const char *source, size_t length)
{
	if (!destination || !source || !length)
		return;
	char *dst = (char*) destination;
	char *src = (char*) source;
	for (size_t i = 0; *src && i < length; i++) {
		*dst++ = *src++;
	}
	*dst = 0;
}

void* ooc_bzero (const void *start, size_t length)
{
	if (!start || !length)
		return NULL;

	char *ptr = (char*) start;
	size_t tmp = length;
	while (tmp > 0) {
		*ptr++ = 0;
		tmp--;
	}
	return (void*) start;
}
