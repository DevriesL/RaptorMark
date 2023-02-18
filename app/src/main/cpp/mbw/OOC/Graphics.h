
#ifndef _GRAPHICS_H
#define _GRAPHICS_H

#include <stdint.h>

typedef uint32_t RGBA;

typedef enum {
	HorizontalAlignmentLeft = 0,
	HorizontalAlignmentRight = 1,
	HorizontalAlignmentCenter = 2,
	HorizontalAlignmentJustified = 3, // <- text only.
} HorizontalAlignment;

typedef struct size {
	short width;
	short height;
} Size;

typedef struct poshort {
	short x;
	short y;
} Point;

typedef struct rect {
	Size size;
	Point origin;
} Rect;

typedef struct margins {
	int left, right, top, bottom;
} Margins;

#endif
