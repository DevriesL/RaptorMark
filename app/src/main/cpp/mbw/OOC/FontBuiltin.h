/*============================================================================
  FontBuiltin, an object-oriented C basic font.
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

#ifndef _OOC_FONT_BUILTIN_H
#define _OOC_FONT_BUILTIN_H

#include "Font.h"

#define DECLARE_FONT_BUILTIN_INSTANCE_VARS(TYPE_POINTER)  /* Nothing additional required */

#define DECLARE_FONT_BUILTIN_METHODS(TYPE_POINTER) /* Nothing additional required */

struct fontbuiltin;

typedef struct fontbuiltin {
	FontClass *is_a;
	DECLARE_OBJECT_INSTANCE_VARS(struct fontbuiltin*)
	DECLARE_FONT_INSTANCE_VARS(struct fontbuiltin*)
	DECLARE_FONT_BUILTIN_INSTANCE_VARS(struct fontbuiltin*)
} FontBuiltin;

extern FontBuiltin *FontBuiltin_new ();
extern FontBuiltin *FontBuiltin_init (FontBuiltin* self);
extern FontBuiltin* FontBuiltin_newWith (const char* name, int size, bool bold, bool italic);

#endif
