/*============================================================================
  Testing, an Object-Oriented C interface to assembly language routines,
  is a part of my "bandwidth" benchmark.
  Copyright (C) 2019 by Zack T Smith.

  "bandwidth" is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
 
  "bandwidth" is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.
 
  You should have received a copy of the GNU Lesser General Public License
  along with this software.  If not, see <http://www.gnu.org/licenses/>.

  The author may be reached at 1@zsmith.co.
 *===========================================================================*/

#ifndef _OOC_TESTING_H
#define _OOC_TESTING_H

#include <stdint.h>
#include <stdbool.h>
#include <math.h>

typedef enum {
    NO_SSE2,

    // ARM
    NEON_64BIT,
    NEON_128BIT,
} TestingMode;

extern long Testing_read (unsigned long size, TestingMode mode, bool random);
extern long Testing_write (unsigned long size, TestingMode mode, bool random);
extern long Testing_registerToRegisterTest ();
extern long Testing_vectorToVectorTest128 ();
extern long Testing_memsetTest ();
extern long Testing_memcpyTest ();
extern long Testing_incrementRegisters ();
extern long Testing_incrementStack ();

#endif
