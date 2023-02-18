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

#include "OOC/Object.h"

typedef enum {
        NO_SSE2,

        // x86
        SSE2,
        SSE2_BYPASS,
        AVX,
        AVX512,
        AVX_BYPASS,

        // ARM
        NEON_64BIT,
        NEON_128BIT,
} TestingMode;

#define DECLARE_TESTING_INSTANCE_VARS(TYPE_POINTER) \
	bool use_sse2; \
	bool use_sse4; \
	bool use_avx; 

#define DECLARE_TESTING_METHODS(TYPE_POINTER) \
	long (*read) (TYPE_POINTER, unsigned long size, TestingMode mode, bool random); \
	long (*write) (TYPE_POINTER, unsigned long size, TestingMode mode, bool random); \
	long (*copy) (TYPE_POINTER, unsigned long size, TestingMode mode); \
	long (*memsetTest) (TYPE_POINTER); \
	long (*memcpyTest) (TYPE_POINTER); \
	long (*registerToRegisterTest) (TYPE_POINTER);\
	long (*registerToVectorTest) (TYPE_POINTER);\
	long (*vectorToRegisterTest) (TYPE_POINTER);\
	long (*vectorToVectorTest128) (TYPE_POINTER);\
	long (*vectorToVectorTest256) (TYPE_POINTER);\
	long (*vectorToRegister8) (TYPE_POINTER);\
	long (*vectorToRegister16) (TYPE_POINTER);\
	long (*vectorToRegister32) (TYPE_POINTER);\
	long (*vectorToRegister64) (TYPE_POINTER);\
	long (*registerToVector8) (TYPE_POINTER);\
	long (*registerToVector16) (TYPE_POINTER);\
	long (*registerToVector32) (TYPE_POINTER);\
	long (*registerToVector64) (TYPE_POINTER);\
	long (*stackPop) (TYPE_POINTER);\
	long (*stackPush) (TYPE_POINTER);\
	long (*incrementRegisters) (TYPE_POINTER);\
	long (*incrementStack) (TYPE_POINTER);\
	int (*calculate_result) (TYPE_POINTER, unsigned long chunk_size, long long total_loops, uint64_t diff);

struct testing;

typedef struct testingclass {
	DECLARE_OBJECT_CLASS_VARS
        DECLARE_OBJECT_METHODS(struct testing*)
        DECLARE_TESTING_METHODS(struct testing*)
} TestingClass;

extern TestingClass *_TestingClass;

typedef struct testing {
        TestingClass *is_a;
	DECLARE_OBJECT_INSTANCE_VARS(struct testing*)
	DECLARE_TESTING_INSTANCE_VARS(struct testing*)
} Testing;

extern Testing *Testing_new ();
extern Testing *Testing_init (Testing *self);

#endif
