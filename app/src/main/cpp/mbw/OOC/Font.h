/*============================================================================
  Font, an object-oriented C font base class.
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

#ifndef _OOC_FONT_H
#define _OOC_FONT_H

#include <stdbool.h>
#include <math.h>
#include <wchar.h>

#include "Object.h"
#include "String.h"
#include "MutableString.h"

#define MAXFONTNAMELEN (64)
#define MAXCHARCODE 255

#define kDefaultIntercharacterSpace (2) 

typedef enum {
        RowUnitByte = 0,
        RowUnitWord = 1,
        RowUnitDword = 2,
} RowUnit;

#define DECLARE_FONT_INSTANCE_VARS(FOO) \
	short dpi; 	\
	short ascent; 	\
	short descent; 	\
	short height; 	\
	float pointSize; 	\
	bool isMonochrome;	\
	char *name; 	\
	wchar_t firstCharacter; 	\
	wchar_t lastCharacter; 	\
	wchar_t totalCharacters;	\
	short spaceWidth; 	\
	bool isFixedWidth; 	\
	char fixedWidth; 	\
	char family [MAXFONTNAMELEN];	\
	char fullName [MAXFONTNAMELEN];	\
	bool italic;	\
	bool bold;	\
	uint8_t weight;	\
	unsigned char bytesPerRow[MAXCHARCODE+1];	\
	unsigned short widths[MAXCHARCODE+1];	\
	unsigned short bitsHigh[MAXCHARCODE+1];	\
	unsigned short bitsWide[MAXCHARCODE+1]; /* 8, 16, or 32 */	\
	short descents[MAXCHARCODE+1];	\
	short xoffsets[MAXCHARCODE+1];	\
	uint8_t *bitmaps[MAXCHARCODE+1];	\
	uint8_t *bitmapBuffer;	\
	short underlinePosition;	\
	short underlineThickness;	\
	char rowUnit;	

#define DECLARE_FONT_METHODS(TYPE_POINTER) \
	float (*pointSize) (TYPE_POINTER);	\
	short (*ascent) (TYPE_POINTER);	\
	short (*descent) (TYPE_POINTER);	\
	short (*spaceWidth) (TYPE_POINTER);	\
	short (*height) (TYPE_POINTER);	\
	wchar_t (*firstCharacter) (TYPE_POINTER);	\
	wchar_t (*lastCharacter) (TYPE_POINTER);	\
	long (*totalCharacters) (TYPE_POINTER);\
	void* (*bitmapForCharacter) (TYPE_POINTER, wchar_t characterCode, unsigned* width, unsigned* bytesPerRow, unsigned* bitsWide, unsigned* bitsHigh, int* xoffset, int* descent); 	\
	void (*sizeOfString) (TYPE_POINTER, String *str, int* w, int* a, int* d);	\
	int (*stringWidth) (TYPE_POINTER, String *str);	

struct font;

typedef struct fontclass {
	DECLARE_OBJECT_CLASS_VARS
        DECLARE_OBJECT_METHODS(struct font*)
        DECLARE_FONT_METHODS(struct font*)
} FontClass;

extern FontClass *_FontClass;

typedef struct font {
        FontClass *is_a;
	DECLARE_OBJECT_INSTANCE_VARS(struct font*)
	DECLARE_FONT_INSTANCE_VARS(struct font*)
} Font;

extern Font *Font_new ();
extern Font *Font_init (Font *self);
extern Font* Font_newWith (const char* name, int size, bool bold, bool italic);

// Used by subclasses.
extern FontClass* FontClass_prepare ();

// These have to be listed here so that subclasses can inherit them.
void Font_sizeOfString (Font *self, String *str, int* w, int* a, int* d);
int Font_stringWidth (Font *self, String *string);
float Font_pointSize (Font *self);
short Font_ascent (Font *self);
short Font_descent (Font *self);
short Font_spaceWidth (Font *self);
short Font_height (Font *self);
wchar_t Font_firstCharacter (Font *self);
wchar_t Font_lastCharacter (Font *self);
long Font_totalCharacters (Font *self);
void* Font_bitmapForCharacter (Font *self, wchar_t characterCode, 
				unsigned* width, unsigned* bytesPerRow, 
				unsigned* bitsWide, unsigned* bitsHigh, 
				int* xoffset, int* descent);

#endif
