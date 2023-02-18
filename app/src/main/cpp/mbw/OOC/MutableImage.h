/*============================================================================
  MutableImage, an object-oriented C image manipulation class.
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

#ifndef _OOC_MUTABLEIMAGE_H
#define _OOC_MUTABLEIMAGE_H

#include <stdint.h>

#include "Object.h"
#include "Image.h"
#include "String.h"
#include "Font.h"

typedef uint32_t RGB;
typedef uint32_t RGBA;

#define MINIFONT_HEIGHT (8)

#define DECLARE_MUTABLEIMAGE_METHODS(TYPE_POINTER) \
	int (*drawMiniString) (TYPE_POINTER, const char *, int x, int y, RGB); \
	int (*drawNarrowNumbers) (TYPE_POINTER, const char *string, int x, int y, RGB color); \
	int (*miniStringWidth) (TYPE_POINTER, const char *); \
	int (*drawString) (TYPE_POINTER, String*, int x, int y, Font*, RGB); \
	void (*clear) (TYPE_POINTER, RGB); \
	void (*drawDashedLine) (TYPE_POINTER, int x0, int y0, int x1, int y1, RGB); \
	void (*drawHorizontalLine) (TYPE_POINTER, int x0, int x1, int y, RGB); \
	void (*drawLine) (TYPE_POINTER, int x0, int y0, int x1, int y1, RGB); \
	void (*drawPixel) (TYPE_POINTER, int, int, RGB); \
	void (*drawRect) (TYPE_POINTER, int x, int y, int w, int h, RGB); \
	void (*drawVerticalLine) (TYPE_POINTER, int x, int y0, int y1, RGB); \
	void (*fillRect) (TYPE_POINTER, int x, int y, int w, int h, RGB); 

struct mutableimage;

typedef struct mutableimageclass {
	DECLARE_OBJECT_CLASS_VARS
	DECLARE_OBJECT_METHODS(struct mutableimage*)
	DECLARE_IMAGE_METHODS(struct mutableimage*)
	DECLARE_MUTABLEIMAGE_METHODS(struct mutableimage*)
} MutableImageClass;

extern MutableImageClass *_MutableImageClass;

#define DECLARE_MUTABLEIMAGE_INSTANCE_VARS(TYPE_POINTER) /* none */

typedef struct mutableimage {
	MutableImageClass *is_a;
	DECLARE_OBJECT_INSTANCE_VARS(struct mutableimage*)
	DECLARE_IMAGE_INSTANCE_VARS(struct mutableimage*)
	DECLARE_MUTABLEIMAGE_INSTANCE_VARS(struct mutableimage*)
} MutableImage;

extern MutableImage* MutableImage_init (MutableImage* self);
extern MutableImage* MutableImage_initWithSize (MutableImage* self, int width, int height);
extern MutableImage *MutableImage_newWithSize (int width, int height);
extern MutableImageClass* MutableImageClass_prepare ();

#endif

