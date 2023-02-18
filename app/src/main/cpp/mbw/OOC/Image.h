/*============================================================================
  Image, an object-oriented C image manipulation class.
  Copyright (C) 2009-2019 by Zack T Smith.

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

#ifndef _OOC_IMAGE_H
#define _OOC_IMAGE_H

#include <stdint.h>

#include "Object.h"
#include "String.h"
#include "Graphics.h"
#include "Font.h"

typedef uint32_t RGB;
typedef uint32_t RGBA;

#define MINIFONT_HEIGHT (8)

#define DECLARE_IMAGE_METHODS(TYPE_POINTER) \
	RGB (*pixelAt) (TYPE_POINTER, int, int); \
	int (*writeBMP) (TYPE_POINTER, const char *path); \
	Size (*size) (TYPE_POINTER); 

struct image;

typedef struct imageclass {
	DECLARE_OBJECT_CLASS_VARS
	DECLARE_OBJECT_METHODS(struct image*)
	DECLARE_IMAGE_METHODS(struct image*)
} ImageClass;

extern ImageClass *_ImageClass;

#define DECLARE_IMAGE_INSTANCE_VARS(TYPE_POINTER) \
	int width, height; \
	RGB *pixels;

typedef struct image {
	ImageClass *is_a;
	DECLARE_OBJECT_INSTANCE_VARS(struct image*)
	DECLARE_IMAGE_INSTANCE_VARS(struct image*)
} Image;

extern Image* Image_init (Image* self);
extern Image* Image_initWithSize (Image* self, int width, int height);
extern Image *Image_newWithSize (int width, int height);
extern ImageClass* ImageClass_prepare ();

extern RGB Image_pixelAt (Image *self, int x, int y);
extern int Image_writeBMP (Image* self, const char *path);
extern Size Image_size (Image* self);

#endif

