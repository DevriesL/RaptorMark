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

#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <wchar.h>

#include "MutableImage.h"
#include "minifont.h"

// Narrowest possible numbers.
static char* narrow_nums [] = 
{
	" # ",
	"# #",
	"# #",
	"# #",
	"# #",
	"# #",
	" # ",

	" #",
	"##",
	" #",
	" #",
	" #",
	" #",
	" #",

	" # ",
	"# #",
	"  #",
	" ##",
	"#  ",
	"#  ",
	"###",

	"###",
	"  #",
	" # ",
	"## ",
	"  #",
	"# #",
	" # ",

	"# #",
	"# #",
	"# #",
	"###",
	"  #",
	"  #",
	"  #",

	"###",
	"#  ",
	"## ",
	"  #",
	"  #",
	"# #",
	" # ",


	" # ",
	"#  ",
	"#  ",
	"## ",
	"# #",
	"# #",
	" # ",

	"###",
	"  #",
	"  #",
	" # ",
	" # ",
	" # ",
	" # ",

	" # ",
	"# #",
	"# #",
	" # ",
	"# #",
	"# #",
	" # ",

	" # ",
	"# #",
	"# #",
	" ##",
	"  #",
	" # ",
	"#  ",

	" ",
	"",
	"",
	" ",
	"",
	"",
	"#",
};

MutableImageClass *_MutableImageClass = NULL;

static void MutableImage_destroy (MutableImage* self)
{
        DEBUG_DESTROY;

	if (!self)
		return;
	verifyCorrectClass(self,MutableImage);
	
	if (self->pixels) {
		free (self->pixels);
	}

	clearObjectSelf;
}

static void 
MutableImage_describe (MutableImage* self, FILE *file)
{
	if (!self)
		return;
	verifyCorrectClass(self,MutableImage);
	
	fprintf (file ?: stdout, "%s: %dx%d\n", self->is_a->className, self->width, self->height);
}

MutableImage* MutableImage_init (MutableImage* self)
{
	if (self) {
		if (!_MutableImageClass)
			MutableImageClass_prepare();

		self->is_a = _MutableImageClass;

		// Just to be clear, self MutableImage has no pixels.
		self->width = 0;
		self->height = 0;
		self->pixels = NULL;
	}
	return self;
}

MutableImage* MutableImage_initWithSize (MutableImage* self, int width, int height)
{
	if (self) {
		if (!_MutableImageClass)
			MutableImageClass_prepare();

		self->is_a = _MutableImageClass;

		// RULE: Allow 0x0 image.
		if (width<1 && height<1)
			return self;
		//----------

		// RULE: Round up dimensions to be multiple of 4.
		if (width & 3) 
			width += 4 - (width & 3);
		if (height & 3) 
			height += 4 - (height & 3);

		size_t size = width * height * sizeof (long);
		if (!(self->pixels = (RGB*) malloc (size))) {
			perror ("malloc");
			free (self);
			return NULL;
		}
		ooc_bzero (self->pixels, size);

		self->width = width;
		self->height = height;
	}
	return self;
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_drawPixel
 * Purpose:	Writes pixel into MutableImage.
 *-------------------------------------------------------------------------*/
static void
MutableImage_drawPixel (MutableImage *self, int x, int y, RGB rgb)
{
	if (!self || x<0 || y<0)
		return;
	verifyCorrectClass(self,MutableImage);
	if (x >= self->width || y >= self->height)
		return;
	if (!self->pixels)
		return;
	//----------

	self->pixels[y*self->width + x] = rgb;
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_drawHorizontalLine
 * Purpose:	Draws horizontal line.
 *-------------------------------------------------------------------------*/
static void
MutableImage_drawHorizontalLine (MutableImage *self, int x0, int x1, int y, RGB rgb)
{
	verifyCorrectClass(self,MutableImage);
	if (x0 > x1) {
		int tmp=x1;
		x1=x0;
		x0=tmp;
	}
	
	while (x0 <= x1) {
		MutableImage_drawPixel (self, x0++, y, rgb);
	}
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_drawVerticalLine
 * Purpose:	Draws vertical line.
 *-------------------------------------------------------------------------*/
static void
MutableImage_drawVerticalLine (MutableImage *self, int x, int y0, int y1, RGB rgb)
{
	verifyCorrectClass(self,MutableImage);
	if (y0 > y1) {
		int tmp=y1;
		y1=y0;
		y0=tmp;
	}
	
	while (y0 <= y1) {
		MutableImage_drawPixel (self, x, y0++, rgb);
	}
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_line_core
 * Purpose:	Draws a line in an MutableImage.
 *-------------------------------------------------------------------------*/
static void
MutableImage_line_core (MutableImage *image, int x0, int y0, int x1, int y1, RGB rgb,
			int dashed)
{
	if ((rgb >> 24) == 0xff)
		return;

	int dot_counter = 0;

	if (!dashed && x0 == x1 && y0 == y1) 
		MutableImage_drawPixel (image, x0, y0, rgb);
	else if (!dashed && x0 == x1)
		MutableImage_drawVerticalLine (image, x0, y0, y1, rgb);
	else if (!dashed && y0 == y1)
		MutableImage_drawHorizontalLine (image, x0, x1, y0, rgb);
	else {
		int j, x, y, dx, dy, e, xchange, s1, s2;

		// DDA, copied from my FramebufferUI project.

		x = x0;
		y = y0;
		s1 = 1;
		s2 = 1;

		dx = x1 - x0;
		if (dx < 0) {
			dx = -dx;
			s1 = -1;
		}

		dy = y1 - y0;
		if (dy < 0) {
			dy = -dy;
			s2 = -1;
		}

		xchange = 0;

		if (dy > dx) {
			int tmp = dx;
			dx = dy;
			dy = tmp;
			xchange = 1;
		}

		e = (dy<<1) - dx;
		j = 0;

		while (j <= dx) {
			j++;

			int draw = 1;
			if (dashed && (1 & (dot_counter >> 2))) 
				draw = 0;
			
			if (draw)
				MutableImage_drawPixel (image, x, y, rgb);

			dot_counter++;

			if (e >= 0) {
				if (xchange)
					x += s1;
				else
					y += s2;
				e -= (dx << 1);
			}
			if (xchange) 
				y += s2;
			else
				x += s1;
			e += (dy << 1);
		}
	}
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_drawLine
 * Purpose:	Draws a line in a MutableImage image.
 *-------------------------------------------------------------------------*/
static void
MutableImage_drawLine (MutableImage *self, int x0, int y0, int x1, int y1, RGB rgb)
{
	verifyCorrectClass(self,MutableImage);
	MutableImage_line_core (self, x0, y0, x1, y1, rgb, 0);
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_drawDashedLine
 * Purpose:	Draws a dashed line in a MutableImage image.
 *-------------------------------------------------------------------------*/
static void
MutableImage_drawDashedLine (MutableImage *self, int x0, int y0, int x1, int y1, RGB rgb)
{
	verifyCorrectClass(self,MutableImage);
	MutableImage_line_core (self, x0, y0, x1, y1, rgb, 1);
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_drawRect
 * Purpose:	Fills a rectangle with a color.
 *-------------------------------------------------------------------------*/
static void
MutableImage_drawRect (MutableImage *self, int x, int y, int w, int h, RGB rgb)
{
	if (!self)
		return;
	verifyCorrectClass(self,MutableImage);

	MutableImage_drawHorizontalLine (self, x, x+w-1, y, rgb);
	MutableImage_drawHorizontalLine (self, x, x+w-1, y+h-1, rgb);
	MutableImage_drawVerticalLine (self, x, y+1, y+h-2, rgb);
	MutableImage_drawVerticalLine (self, x+w-1, y+1, y+h-2, rgb);
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_fillRect
 * Purpose:	Fills a rectangle with a color.
 *-------------------------------------------------------------------------*/
static void
MutableImage_fillRect (MutableImage *self, int x, int y, int w, int h, RGB rgb)
{
	if (!self)
		return;
	verifyCorrectClass(self,MutableImage);

	int x1 = x + w - 1;
	while (h > 0) {
		MutableImage_drawHorizontalLine (self, x, x1, y, rgb);
		h--;
		y++;
	}
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_clear
 * Purpose:	Sets all pixels to specified color.
 *-------------------------------------------------------------------------*/
static void
MutableImage_clear (MutableImage *self, RGB rgb)
{
	verifyCorrectClass(self,MutableImage);
	MutableImage_fillRect (self, 0, 0, self->width, self->height, rgb);
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_drawString
 * Purpose:	Draws characters into the image.
 *-------------------------------------------------------------------------*/
static int
MutableImage_drawString (MutableImage *self, String *string, int x, int y, Font *font, RGB color)
{
	if (!self || !string || !font)
		return 0;
	verifyCorrectClass(self,MutableImage);
	verifyCorrectClass(string,String);
	int len = $(string, length);
	if (x >= self->width || y >= self->height || !len)
		return 0;
	//----------

	wchar_t firstChar = $(font,firstCharacter);
	wchar_t lastChar = $(font,lastCharacter);

	for (int index = 0; index < len; index++) {
		wchar_t ch = $(string, characterAt, index);
	
		if (ch == ' ') {
			x += $(font, spaceWidth);
			continue;
		}
	
                if (ch < firstChar || ch > lastChar)
                        continue;
		
		unsigned bitsWide, bitsHigh, width;
		unsigned bytesPerRow;
		int descent, leftoffset;
		uint8_t *bitmap = (uint8_t*) $(font, bitmapForCharacter, ch, &width, &bytesPerRow, &bitsWide, &bitsHigh, &leftoffset, &descent);
		if (!bitmap) {
			char message[128];
			snprintf (message, sizeof(message), "Character %c of font %s (size %.1f) lacks bitmap!", ch, font->name, font->pointSize);
			warning (__FUNCTION__, message);
			continue;
		}

		int charAscent = bitsHigh - descent;
		int yOffset = $(font,ascent) - charAscent;

		for (unsigned i=0; i < bitsHigh; i++) {
			uint32_t row = 0;
			switch (bytesPerRow) {
				case 1: row = *bitmap++; row <<= 24; break;
				case 2: row = *((uint16_t*) bitmap); bitmap += 2; row <<= 16; break; 
				case 4: row = *((uint32_t*) bitmap); bitmap += 4; break;
			}

			int j=0;
			uint32_t mask = 1<<31;
			while (row) {
				if (row & mask) {
					MutableImage_drawPixel (self, x+j, y+i+yOffset, color);
				}
				j++;

				row <<= 1;
			}
		}

		x += width + leftoffset;
		x += kDefaultIntercharacterSpace;
	}

	return x;
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_drawMiniString
 * Purpose:	Draws miniature 5x8 characters.
 *-------------------------------------------------------------------------*/
static int
MutableImage_drawMiniString (MutableImage *self, const char *string, int x, int y, RGB color)
{
	char ch;
	const char *s;

	if (!self || !string)
		return 0;
	verifyCorrectClass(self,MutableImage);
	if (x >= self->width || y >= self->height || !*string)
		return 0;
	//----------

#if 0
	unsigned long r,g,b;
	unsigned long light, dark;
	r = 0xff & (color >> 16);
	g = 0xff & (color >> 8);
	b = 0xff & color;
	r += 3*0xff;
	b += 3*0xff;
	g += 3*0xff;
	r /= 4;
	g /= 4;
	b /= 4;
	light = b | (g << 8) | (r << 16);

	r = 0xff & (color >> 16);
	g = 0xff & (color >> 8);
	b = 0xff & color;
	r += 0xff;
	b += 0xff;
	g += 0xff;
	r /= 2;
	g /= 2;
	b /= 2;
	dark = b | (g << 8) | (r << 16);
#endif

#define MINI_HEIGHT (8)
	s = string;
	while ((ch = *s++)) {
		int ix = -1;
		if (ch == ' ') {
			x += 5;
			continue;
		}
		if (ch > 'z')
			continue;
		if (ch > ' ' && ch <= 'z')
			ix = MINI_HEIGHT * (ch - 33);
		
		if (ix >= 0) {
			int i;

			int width = 0;
			for (i=0; i<MINI_HEIGHT; i++) {
				int j=0;
				char ch2;
				const char *s2 = mini_chars[ix + i];
				int width2 = s2 ? ooc_strlen (s2) : 0;
				if (width < width2)
					width = width2;
				while ((ch2 = *s2++)) {
					if (ch2 == '#')
						MutableImage_drawPixel (self,x+j, y+i, color);
					
					j++;
				}
			}

			x += width + 1/* kerning */;
		}
	}

	return x;
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_miniStringWidth
 * Purpose:	Gets width of miniature 5x8 characters.
 *-------------------------------------------------------------------------*/
static int
MutableImage_miniStringWidth (MutableImage *self, const char *string)
{
	char ch;
	const char *s;
	int width = 0;

	if (!self || !string)
		return 0;
	verifyCorrectClass(self,MutableImage);
	//----------

	s = string;
	while ((ch = *s++)) {
		int ix = -1;
		if (ch == ' ') {
			width += 5;
			continue;
		}
		if (ch > 'z')
			continue;
		if (ch > ' ' && ch <= 'z')
			ix = MINI_HEIGHT * (ch - 33);
		
		if (ix >= 0) {
			int max_w = 0;
			int j;
			for (j = 0; j < MINI_HEIGHT; j++) {
				const char *ptr = mini_chars [j+ix];
				int w = ptr ? ooc_strlen (ptr) : 0;
				if (max_w < w) max_w = w;
			}

			width += max_w + 1/*kerning*/;
		}
	}

	return width;
}

/*---------------------------------------------------------------------------
 * Name:	MutableImage_narrow_numbers
 * Purpose:	Draws miniature 4x7 characters.
 *-------------------------------------------------------------------------*/
static int
MutableImage_drawNarrowNumbers (MutableImage *self, const char *string, int x, int y, RGB color)
{
	char ch;
	const char *s;

	if (!self || !string)
		return 0;
	verifyCorrectClass(self,MutableImage);
	if (x >= self->width || y >= self->height || !*string)
		return 0;
	//----------

#define NARROW_HEIGHT (7)
	s = string;
	while ((ch = *s++)) {
		int ix = -1;
		if (ch == ' ') {
			x += 3;
			continue;
		}
		if (ch >= '0' && ch <= '9')
			ix = ch - '0';
		else
		if (ch == '.')
			ix = 10;
		
		ix *= NARROW_HEIGHT;
		
		if (ix >= 0) {
			int i;
			int width = ooc_strlen (narrow_nums [ix]);

			for (i=0; i<NARROW_HEIGHT; i++) {
				int j=0;
				char ch2;
				const char *s2 = narrow_nums [ix + i];
				while ((ch2 = *s2++)) {
					if (ch2 == '#') {
						MutableImage_drawPixel (self, 
							x+j, y+i, color);
					}
					j++;
				}
			}

			x += width + 1;
		}
	}

	return x;
}

MutableImage *MutableImage_newWithSize (int width, int height)
{
	MutableImage* image = new(MutableImage);
	return MutableImage_initWithSize (image, width, height);
}

MutableImageClass* MutableImageClass_prepare ()
{
	PREPARE_CLASS_STRUCT(MutableImage,Object)

	SET_OVERRIDDEN_METHOD_POINTER(MutableImage, describe);
	SET_OVERRIDDEN_METHOD_POINTER(MutableImage, destroy);

	SET_INHERITED_METHOD_POINTER(MutableImage,Image,pixelAt);
	SET_INHERITED_METHOD_POINTER(MutableImage,Image,writeBMP);
	SET_INHERITED_METHOD_POINTER(MutableImage,Image,size);

	SET_METHOD_POINTER(MutableImage, clear);
	SET_METHOD_POINTER(MutableImage, drawDashedLine);
	SET_METHOD_POINTER(MutableImage, drawHorizontalLine);
	SET_METHOD_POINTER(MutableImage, drawLine);
	SET_METHOD_POINTER(MutableImage, drawPixel);
	SET_METHOD_POINTER(MutableImage, drawRect);
	SET_METHOD_POINTER(MutableImage, drawMiniString);
	SET_METHOD_POINTER(MutableImage, drawNarrowNumbers);
	SET_METHOD_POINTER(MutableImage, drawVerticalLine);
	SET_METHOD_POINTER(MutableImage, fillRect);
	SET_METHOD_POINTER(MutableImage, miniStringWidth);
	SET_METHOD_POINTER(MutableImage, drawString);
	
	VALIDATE_CLASS_STRUCT(_MutableImageClass);
	return _MutableImageClass;
}

