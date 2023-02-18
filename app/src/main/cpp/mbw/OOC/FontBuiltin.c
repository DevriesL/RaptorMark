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

//-----------------------------------------------------------------------------
// Name:	FontBuiltin
// Responsible:	For providing my built-in, handmade font.
// What:	Handmade font.
// Where:	Font subsystem.
// How: 	Piggybacks on Font methods.
//-----------------------------------------------------------------------------

#include "Font.h"
#include "FontBuiltin.h"
#include "basicfont.h" // 14pt 

extern FontClass *_FontClass;

FontBuiltin* FontBuiltin_init (FontBuiltin *self)
{
	if (!_FontClass) 
		FontClass_prepare ();

	if (self) {
		// NOTE: FontBuiltin adds no new methods, so its is_a points to Font.
		self->is_a = (void*) _FontClass;

		self->dpi = 120;
		self->pointSize = 0.f;
		self->ascent = 0;
		self->descent = 0;
		self->height = 0;
		self->firstCharacter = 33;
		self->lastCharacter = 'z';
	}
	return self;
}

void FontBuiltin_describe (FontBuiltin *self, FILE *outputFile)
{
	if (!outputFile)
		outputFile = stdout;

	fprintf (outputFile, "%s", self->is_a->className);
}

FontBuiltin* FontBuiltin_newWith (const char* name, int size, bool bold, bool italic)
{
	FontBuiltin* self = new(FontBuiltin);

	self->pointSize = 14;
	self->bold = false;
	self->italic = false;

	ooc_strncpy (self->family, "builtin", ooc_strlen(self->family));

	snprintf (self->fullName, sizeof(self->fullName)-1, "builtin %d", size);

	self->isFixedWidth = false;
	self->fixedWidth = 0;

	const char **patterns = NULL;

	if (!size || size == 14) {
		self->firstCharacter = 32; 
       		self->lastCharacter = 122;
		self->totalCharacters = self->lastCharacter - self->firstCharacter + 1;
       		self->ascent = 14;
       		self->descent = 3;
		self->spaceWidth = 5;
		patterns = basicfont_chars;
	} else {
		return NULL;
	}

	self->height = self->ascent + self->descent;

	for (int i=0 ; i < self->totalCharacters; i++) {
		self->descents[i] = self->descent;
	}

	int characterHeight = self->ascent + self->descent;

	int maxWidth = 0;
	for (int i=0; i < self->totalCharacters; i++) {
		int maxBits = 0;
		int start = i * characterHeight;

		for (int j = 0; j < characterHeight; j++) {
			int len = ooc_strlen (patterns [start+j]);
			if (maxBits < len)
				maxBits = len;
		}

		if (maxWidth < maxBits)
			maxWidth = maxBits;

		self->widths [i] = maxBits;
		self->bitsHigh [i] = characterHeight;
	}

	int bytesPerRow = 0;
	if (maxWidth > 16) {
		self->rowUnit = RowUnitDword;
		bytesPerRow = 4;
	}
	else if (maxWidth > 8) {
		self->rowUnit = RowUnitWord;
		bytesPerRow = 2;
	}
	else {
		self->rowUnit = RowUnitByte;
		bytesPerRow = 1;
	}

	for (int i=0; i < self->totalCharacters; i++) {
		self->bytesPerRow[i] = bytesPerRow;
		self->bitsWide[i] = self->bytesPerRow[i] * 8;

		int bytesNeededForPattern = characterHeight * self->bytesPerRow[i];
		uint8_t *bitmap = (uint8_t*) malloc (bytesNeededForPattern);
		self->bitmaps[i] = bitmap;

		int patternOffset = i * characterHeight;
		for (int j=0; j < characterHeight; j++) {
			uint32_t row = 0;
			const char *rowString = patterns [patternOffset + j];
			uint32_t bit = 0x80000000;
			char ch;
			while ((ch = *rowString)) {
				if (ch != ' ')
					row |= bit;
				bit >>= 1;
				rowString++;
			}

			switch (self->rowUnit) {
			case RowUnitByte: 
				row >>= 24;
				*bitmap++ = (uint8_t) row;
				break;
			case RowUnitWord: {
				uint16_t *tmp = (uint16_t*) bitmap;
				row >>= 16;
				*tmp = (uint16_t) row;
				bitmap += 2;
				break;
			 }
			case RowUnitDword: {
				uint32_t *tmp = (uint32_t*) bitmap;
				*tmp = row;
				bitmap += 4;
				break;
			 }
			}
		}
	}
	return self;
}

