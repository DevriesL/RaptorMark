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

#include <stdlib.h>

#include "Font.h"
#include "FontBuiltin.h"
#include "FontPK.h"

FontClass* FontClass_prepare ();

FontClass *_FontClass = NULL;

static void Font_destroy (Font *self)
{
        DEBUG_DESTROY;

	if (!self)
		return;
	verifyCorrectClasses(self,Font,FontPK);

        if (self->bitmapBuffer) {
                free (self->bitmapBuffer);
        } else {
                for (int i=0; i < 256; i++)
                        free (self->bitmaps[i]);
        }

	clearObjectSelf;

	// NOTE: The releaser frees self.
}

static void Font_describe (Font* self, FILE *outputFile) 
{ 
	if (!self)
		return;
	verifyCorrectClasses(self,Font,FontPK);

	if (!outputFile)
		outputFile = stdout;

	fprintf (outputFile, "%s", self->is_a->className);
}

Font *Font_new () 
{
	Font *self = allocate(Font);
	Font_init (self);
	return self;
}

Font* Font_newWith (const char* name, int size, bool bold, bool italic)
{
	if (!_FontClass)
		FontClass_prepare ();

	(void)name;
	(void)size;
	(void)bold;
	(void)italic;

	Font *self = Font_new ();
	
	return self;
}

void* Font_bitmapForCharacter (Font *self, wchar_t characterCode, 
				unsigned* width, unsigned* bytesPerRow, 
				unsigned* bitsWide, unsigned* bitsHigh, 
				int* xoffset, int* descent)  
{
	verifyCorrectClasses(self,Font,FontPK);

	if (characterCode < self->firstCharacter || characterCode > self->lastCharacter)
		return NULL;

	// NOTE: Not supporting Unicode yet.
	if (characterCode > 255)
		return NULL;

	int index = characterCode - self->firstCharacter;
	*width = self->widths [index];
	*bytesPerRow = self->bytesPerRow [index];
	*bitsWide = self->bitsWide [index];
	*bitsHigh = self->bitsHigh [index];
	*xoffset = self->xoffsets [index];
	*descent = self->descents [index];

	void *bitmaps = self->bitmaps[index];
	return bitmaps;
}

void Font_sizeOfString (Font *self, String *str, int* w, int* a, int* d)      
{
	verifyCorrectClasses(self,Font,FontPK);
	verifyCorrectClasses(str,String,MutableString);

	int totalWidth = 0;
	wchar_t ch;

	int i = 0;
	while ((ch = $(str, characterAt, i++))) {
		if (ch == ' ') {
			totalWidth += self->spaceWidth;
			continue;
		}

		int index = ch - self->firstCharacter;
		if (index >= 0 && index < self->totalCharacters) {
			totalWidth += self->widths [index]; 
			totalWidth += self->xoffsets [index];
			totalWidth += kDefaultIntercharacterSpace;
		}
	}

	*w = totalWidth;
	*a = self->ascent;
	*d = self->descent;
}

int Font_stringWidth (Font *self, String *string)
{
	verifyCorrectClasses(self,Font,FontPK);
	verifyCorrectClasses(string,String,MutableString);

	if (!string)
		return 0; // XX

	int width, ascent, descent;
	Font_sizeOfString (self, string, &width, &ascent, &descent);
	return width;
}

float Font_pointSize (Font *self)  
{
	return self->pointSize;
}

short Font_ascent (Font *self)     
{
	verifyCorrectClasses(self,Font,FontPK);
	return self->ascent;
}

short Font_descent (Font *self)    
{
	verifyCorrectClasses(self,Font,FontPK);
	return self->descent;
}

short Font_spaceWidth (Font *self) 
{
	verifyCorrectClasses(self,Font,FontPK);
	return self->spaceWidth;
}

short Font_height (Font *self)     
{
	verifyCorrectClasses(self,Font,FontPK);
	return self->height;
}

wchar_t Font_firstCharacter (Font *self)   
{
	verifyCorrectClasses(self,Font,FontPK);
        return self->firstCharacter;
}

wchar_t Font_lastCharacter (Font *self)    
{
	verifyCorrectClasses(self,Font,FontPK);
        return self->lastCharacter;
}

long Font_totalCharacters (Font *self)
{
	verifyCorrectClasses(self,Font,FontPK);
        return self->lastCharacter - self->firstCharacter;
}

FontClass* FontClass_prepare ()
{
	PREPARE_CLASS_STRUCT(Font,Object)

	SET_OVERRIDDEN_METHOD_POINTER(Font,describe);
	SET_OVERRIDDEN_METHOD_POINTER(Font,destroy);

	SET_METHOD_POINTER(Font,pointSize);
	SET_METHOD_POINTER(Font,stringWidth);
	SET_METHOD_POINTER(Font,totalCharacters);
	SET_METHOD_POINTER(Font,ascent);
	SET_METHOD_POINTER(Font,descent);
	SET_METHOD_POINTER(Font,height);
	SET_METHOD_POINTER(Font,spaceWidth);
	SET_METHOD_POINTER(Font,sizeOfString);
	SET_METHOD_POINTER(Font,bitmapForCharacter);
	SET_METHOD_POINTER(Font,firstCharacter);
	SET_METHOD_POINTER(Font,lastCharacter);
	
        VALIDATE_CLASS_STRUCT(_FontClass);
	return _FontClass;
}

Font* Font_init (Font *self)
{
	if (!_FontClass)
		FontClass_prepare ();

	ooc_bzero (self, sizeof(Font));
	Object_init ((Object*) self);

	self->is_a = _FontClass;
	
	self->isMonochrome = true;
	self->name = NULL;
	self->pointSize = 0;
	self->height = 0;
	self->firstCharacter = 0;
	self->lastCharacter = 0;
	self->ascent = 0;
	self->descent = 0;
	self->spaceWidth = 0;
	self->italic = false;
	self->bold = false;
	self->weight = 0;
	self->isFixedWidth = false;
	self->fixedWidth = 0;
	self->bitmapBuffer = NULL;
	self->family[0] = 0;
	self->fullName[0] = 0;
//	self->rowUnit = RowUnitByte;

	for (int i=0; i < 256; i++) {
		self->widths[i] = 0;
		self->xoffsets[i] = 0;
		self->bitsHigh[i] = 0;
		self->bitsWide[i] = 0;
		self->descents[i] = 0;
		self->bitmaps[i] = 0;
	}

	return self;
}

