/*============================================================================
  CPUCharacteristics, object-oriented C class providing an interface
  to assembly language routines.
  Copyright (C) 2019 by Zack T Smith.

  This class is free software: you can redistribute it and/or modify
  it under the terms of the GNU Lesser General Public License as published
  by the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
 
  This class is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU Lesser General Public License for more details.
 
  You should have received a copy of the GNU Lesser General Public License
  along with this software.  If not, see <http://www.gnu.org/licenses/>.

  The author may be reached at 1@zsmith.co.
 *===========================================================================*/

#ifndef _OOC_CPUCHARACTERISTICS_H
#define _OOC_CPUCHARACTERISTICS_H

#include <stdint.h>
#include <stdbool.h>
#include <math.h>

#include "OOC/Object.h"

#ifndef __arm__
#if defined(__i386__) || defined(__x86_64__) || defined(_WIN64) || defined(_WIN32) ||defined(__WIN32__) || defined(__WIN64__) || defined(_M_IX86) || defined(__MINGW32__) || defined(__i386) || defined(__CYGWIN__)
#define x86
#endif
#endif

#if defined(__x86_64__) || defined(_WIN64) || defined(__WIN64__) || defined(__aarch64__)
#define IS_64BIT
#endif

#define DECLARE_CPUCHARACTERISTICS_INSTANCE_VARS(TYPE_POINTER) \
	char cpu_family [32];\
	bool is_intel;\
	bool is_amd;\
	bool is_arm;\
	bool has_hyperthreading;\
	bool has_mmx;\
	bool has_mmxext;\
	bool has_sse;\
	bool has_sse2;\
	bool has_sse3;\
	bool has_ssse3;\
	bool has_sse4a;\
	bool has_sse41;\
	bool has_sse42;\
	bool has_aes;\
	bool has_sha;\
	bool has_sgx;\
	bool has_avx;\
	bool has_avx2;\
	bool has_avx512_f;\
	bool has_avx512_dq;\
	bool has_avx512_vbmi;\
	bool has_avx512_vbmi2;\
	bool has_avx512_4vnniw;\
	bool has_avx512_4fmaps;\
	bool has_avx512_vp2intersect;\
	bool has_avx512_vnni;\
	bool has_avx512_bitalg;\
	bool has_avx512_vpopcntdq;\
	bool has_avx512_ifma;\
	bool has_avx512_fp16;\
	bool has_avx512_pf;\
	bool has_avx512_er;\
	bool has_avx512_cd;\
	bool has_avx512_bw;\
	bool has_avx512_vl;\
	bool has_64bit_support;\
	bool has_nx;\
	bool has_adx;\
	bool has_bmi1;\
	bool has_bmi2;\
	bool has_cet;\
	bool running_in_hypervisor;\

#define DECLARE_CPUCHARACTERISTICS_METHODS(TYPE_POINTER) \
	void (*printCharacteristics) (TYPE_POINTER);\
	void (*printCacheInfo) (TYPE_POINTER);\
	char *(*getCPUString) (TYPE_POINTER);

struct cpucharacteristics;

typedef struct cpucharacteristicsclass {
	DECLARE_OBJECT_CLASS_VARS
        DECLARE_OBJECT_METHODS(struct cpucharacteristics*)
        DECLARE_CPUCHARACTERISTICS_METHODS(struct cpucharacteristics*)
} CPUCharacteristicsClass;

extern CPUCharacteristicsClass *_CPUCharacteristicsClass;

typedef struct cpucharacteristics {
        CPUCharacteristicsClass *is_a;
	DECLARE_OBJECT_INSTANCE_VARS(struct cpucharacteristics*)
	DECLARE_CPUCHARACTERISTICS_INSTANCE_VARS(struct cpucharacteristics*)
} CPUCharacteristics;

extern CPUCharacteristics *CPUCharacteristics_init (CPUCharacteristics *self);

#endif
