/*============================================================================
  Image, an object-oriented C image class.
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

#include "Image.h"
#include "MutableImage.h"

ImageClass *_ImageClass = NULL;

static void Image_destroy (Image* self)
{
        DEBUG_DESTROY;

	if (!self)
		return;
	verifyCorrectClass(self,Image);
	
	if (self->pixels) {
		free (self->pixels);
	}

	clearObjectSelf;
}

static void 
Image_describe (Image* self, FILE *file)
{
	if (!self)
		return;
	verifyCorrectClass(self,Image);
	
	fprintf (file ?: stdout, "%s: %dx%d\n", self->is_a->className, self->width, self->height);
}

Image* Image_init (Image* self)
{
	if (self) {
		if (!_ImageClass)
			ImageClass_prepare();

		self->is_a = _ImageClass;

		// Just to be clear, self Image has no pixels.
		self->width = 0;
		self->height = 0;
		self->pixels = NULL;
	}
	return self;
}

Image* Image_initWithPath (Image* self, char *imageFilePath)
{
	if (self) {
		if (!_ImageClass)
			ImageClass_prepare();

		self->is_a = _ImageClass;

	}
	return self;
}

/*---------------------------------------------------------------------------
 * Name:	Image_pixelAt
 * Purpose:	Reads pixel out of self.
 *-------------------------------------------------------------------------*/
RGB Image_pixelAt(Image *self, int x, int y)
{
	if (!self || x<0 || y<0)
		return 0;
	verifyCorrectClass(self,Image);
	if (x >= self->width || y >= self->height)
		return 0;
	if (!self->pixels)
		return 0;
	//----------

	return self->pixels[y*self->width + x];
}

/*---------------------------------------------------------------------------
 * Name:	Image_size
 * Purpose:	Returns the image's size.
 *-------------------------------------------------------------------------*/
Size Image_size (Image *self)
{
	Size size = { 0, 0 };
	if (!self)
		return size;
	verifyCorrectClass(self,Image);

	size.width = self->width;
	size.height = self->height;

	return size;
}

/*---------------------------------------------------------------------------
 * Name:	Image_writeBMP24
 * Purpose:	Writes to BMP 24bpp file.
 *-------------------------------------------------------------------------*/
static int 
Image_writeBMP24 (const Image* self, const char *path)
{
	int i, j;

	if (!self || !path)
		return -1;
	verifyCorrectClasses(self,Image,MutableImage);
	if (!self->width) { error(__FUNCTION__, "Zero width image."); }
	if (!self->height) { error(__FUNCTION__, "Zero height image."); }
	//----------

	//--------------------
	// Create the file.
	//
	FILE *f = fopen (path, "wb");
	if (!f)
		return 0;

	//---------------------
	// Prepare 24bpp header
	//
#define BMP_HDR_LENGTH (54)
	unsigned char h[BMP_HDR_LENGTH];
	unsigned long len;
	ooc_bzero (h, BMP_HDR_LENGTH);
	len = BMP_HDR_LENGTH + 3 * self->width * self->height;
	h[0] = 'B';
	h[1] = 'M';
	h[2] = len & 0xff;
	h[3] = (len >> 8) & 0xff;
	h[4] = (len >> 16) & 0xff;
	h[5] = (len >> 24) & 0xff;
	h[10] = BMP_HDR_LENGTH;
	h[14] = 40;
	h[18] = self->width & 0xff;
	h[19] = (self->width >> 8) & 0xff;
	h[20] = (self->width >> 16) & 0xff;
	h[22] = self->height & 0xff;
	h[23] = (self->height >> 8) & 0xff;
	h[24] = (self->height >> 16) & 0xff;
	h[26] = 1;
	h[28] = 24;
	h[34] = 16;
	h[38] = 0x13; // 2835 pixels/meter
	h[39] = 0x0b;
	h[42] = 0x13; // 2835 pixels/meter
	h[43] = 0x0b;

	//--------------------
	// Write header.
	//
	if (BMP_HDR_LENGTH != fwrite (h, 1, BMP_HDR_LENGTH, f)) {
		fclose (f);
		return 0;
	}

	//----------------------------------------
	// Write pixels.
	// Note that Image has lower rows first.
	//
	for (j=self->height-1; j >= 0; j--) {
		for (i=0; i < self->width; i++) {
			unsigned char rgb[3];
			int ix = i + j * self->width;
			unsigned long pixel = self->pixels[ix];
			rgb[0] = pixel & 0xff;
			rgb[1] = (pixel >> 8) & 0xff;
			rgb[2] = (pixel >> 16) & 0xff;
			if (3 != fwrite (rgb, 1, 3, f)) {
				fclose (f);
				return 0;
			}
		}
	}

	fclose (f);
	return 1;
}

/*---------------------------------------------------------------------------
 * Name:	Image_writeBMP	
 * Purpose:	Writes to BMP 8bpp or 24bpp file.
 * Returns:	1 on success, 0 on failure.
 *-------------------------------------------------------------------------*/
int Image_writeBMP (Image* self, const char *path)
{
	return Image_writeBMP24 (self, path);
}

ImageClass* ImageClass_prepare ()
{
	PREPARE_CLASS_STRUCT(Image,Object)

	SET_OVERRIDDEN_METHOD_POINTER(Image, describe);
	SET_OVERRIDDEN_METHOD_POINTER(Image, destroy);

	SET_METHOD_POINTER(Image, pixelAt);
	SET_METHOD_POINTER(Image, writeBMP);
	SET_METHOD_POINTER(Image, size);
	
	VALIDATE_CLASS_STRUCT(_ImageClass);
	return _ImageClass;
}

