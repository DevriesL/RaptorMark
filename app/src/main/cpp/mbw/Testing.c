/*============================================================================
  bandwidth, a benchmark to estimate memory transfer bandwidth.
  Copyright (C) 2005-2021 by Zack T Smith.

  This program is free software; you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation; either version 2 of the License, or
  (at your option) any later version.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

  The author may be reached at 1@zsmith.co.
 *===========================================================================*/

#include <stdio.h>
#include <stdint.h>
#include <stdlib.h>
#include <time.h>

#include "defs.h"
#include "ObjectOriented.h"
#include "Object.h"
#include "OOC/DateTime.h"
#include "Testing.h"
#include "CPUCharacteristics.h"
#include "Console.h"
#include "routines.h"

#define REGISTER_TRANSFERS_COUNT 9997
#define VREGISTER_TRANSFERS_COUNT 9977

#define N_INC_OUTER_LOOPS 16537L
#define N_INC_INNER_LOOPS 32767L
#define N_INC_PER_INNER 32

TestingClass* _TestingClass = NULL;

extern Console* console;

extern unsigned long usec_per_test;
extern void *memset(void *b, int c, size_t len);
extern void *memcpy(void *restrict dst, const void *restrict src, size_t n);

static void print_size (unsigned long size)
{
	if (size < 1536) {
		$(console, print_int, size);
		$(console, print, " B");
	}
	else if (size < (1<<20)) {
		$(console, print_int, size >> 10);
		$(console, print, " kB");
	} else {
		$(console, print_int, size >> 20);
		switch ((size >> 18) & 3) {
		case 1: $(console, print, ".25"); break;
		case 2: $(console, print, ".5"); break;
		case 3: $(console, print, ".75"); break;
		}
		$(console, print, " MB");
	}
}

//============================================================================
// Tests.
//============================================================================

static void Testing_destroy (Testing* self)
{
	if (!self)
		return;

	verifyCorrectClass(self,Testing);

        clearObjectSelf;
}


static void Testing_describe (Testing* self, FILE *outputFile)
{
        if (!self)
                return;
        verifyCorrectClass(self,Testing);

        if (!outputFile)
                outputFile = stdout;

        fprintf (outputFile, "%s", self->is_a->className);
}

//----------------------------------------------------------------------------
// Name:	calculate_result
// Purpose:	Calculates and prints a result.
// Returns:	10 times the number of megabytes per second.
//----------------------------------------------------------------------------
static int Testing_calculate_result (Testing* self, unsigned long chunk_size, long long total_loops, uint64_t diff)
{
	if (!diff) {
		warning (__FUNCTION__, "Zero time difference... ignoring.");
		return 0;
	}

	long double result = (long double) chunk_size;
	result *= (long double) total_loops;
	result *= 1000000.;
	result /= 1048576.;
	result /= (long double) diff;

	char string [32];
	snprintf (string, sizeof(string)-1, "%.1Lf MB/s", result);
	$(console, println, string);

	return (long) (10.0 * result);
}

//----------------------------------------------------------------------------
// Name:	Testing_write
// Purpose:	Performs write on chunk of memory of specified size.
//----------------------------------------------------------------------------
long Testing_write (Testing *self, unsigned long size, TestingMode mode, bool random)
{
	unsigned char *chunk;
	unsigned char *chunk0;
	unsigned long loops;
	unsigned long long total_count=0;
#ifdef IS_64BIT
	unsigned long value = 0x1234567689abcdef;
#else
	unsigned long value = 0x12345678;
#endif
	unsigned long diff=0, t0;
	unsigned long **chunk_ptrs = NULL;

#ifdef x86
	if (size & 127)
		error (__FUNCTION__, "Chunk size is not multiple of 128.");
#else
	if (size & 255)
		error (__FUNCTION__, "Chunk size is not multiple of 256.");
#endif

	//-------------------------------------------------
	chunk0 = malloc (size+128);
	chunk = chunk0;
	if (!chunk)
		error (__FUNCTION__, "Out of memory");
	
	if (mode != AVX512) {
		unsigned long tmp = (unsigned long) chunk;
		if (tmp & 31) {
			tmp -= (tmp & 31);
			tmp += 32;
			chunk = (unsigned char*) tmp;
		}
	} else {
		unsigned long tmp = (unsigned long) chunk;
		if (tmp & 63) {
			tmp -= (tmp & 63);
			tmp += 64;
			chunk = (unsigned char*) tmp;
		}
	}

	//----------------------------------------
	// Set up random pointers to chunks.
	//
	if (random) {
		unsigned long nChunks = size/256;
		chunk_ptrs = (unsigned long**) malloc (sizeof (unsigned long*) * nChunks);
		if (!chunk_ptrs)
			error (__FUNCTION__, "Out of memory.");

		//-----------------------------------------
		// Store pointers to all chunks in an array.
		//
		int i;
		for (i = 0; i < nChunks; i++) {
			chunk_ptrs [i] = (unsigned long*) (((char*)chunk) + 256 * i);
		}

		//----------------------------------------
		// Randomize the array of chunk pointers.
		//
		int k = 200;
		while (k--) {
			for (i = 0; i < nChunks; i++) {
				int j = rand() % nChunks;
				if (i != j) {
					unsigned long *ptr = chunk_ptrs [i];
					chunk_ptrs [i] = chunk_ptrs [j];
					chunk_ptrs [j] = ptr;
				}
			}
		}
	}

	//-------------------------------------------------
	if (random)
		$(console, print, "Random write ");
	else
		$(console, print, "Sequential write ");

	switch (mode) {
	case NEON_64BIT:
		$(console, print, "(64-bit), size = ");
		break;
	case NEON_128BIT:
		$(console, print, "(128-bit), size = ");
		break;
	case SSE2:
		$(console, print, "(128-bit), size = ");
		break;
	case AVX:
		$(console, print, "(256-bit), size = ");
		break;
	case AVX512:
		$(console, print, "(512-bit), size = ");
		break;
	case AVX_BYPASS:
                $(console, print, "bypassing cache (256-bit), size = ");
		break;
	case SSE2_BYPASS:
                $(console, print, "bypassing cache (128-bit), size = ");
		break;
	default:
#ifdef IS_64BIT
		$(console, print, "(64-bit), size = ");
#else
		$(console, print, "(32-bit), size = ");
#endif
	}

	print_size (size);
	$(console, print, ", ");

	loops = (1 << 26) / size;// XX need to adjust for CPU MHz
	if (loops < 1)
		loops = 1;

	t0 = DateTime_getMicrosecondTime ();

	while (diff < usec_per_test) {
		total_count += loops;

		switch (mode) {
#ifdef __arm__
                case NEON_64BIT:
			// Not needed any longer.
			break;
                case NEON_128BIT:
			if (random)
				RandomWriterVector (chunk_ptrs, size/256, loops, value);
			else
                        	WriterVector (chunk, size, loops, value);
                        break;
#endif

#ifdef x86
		case SSE2:
			if (random)
				RandomWriterSSE2 (chunk_ptrs, size/256, loops, value);
			else {
				if (size & 128)
					WriterSSE2_128bytes (chunk, size, loops, value);
				else
					WriterSSE2 (chunk, size, loops, value);
			}
			break;

		case SSE2_BYPASS:
			if (random)
				RandomWriterSSE2_bypass (chunk_ptrs, size/256, loops, value);
			else {
				if (size & 128)
					WriterSSE2_128bytes_bypass (chunk, size, loops, value);
				else
					WriterSSE2_bypass (chunk, size, loops, value);
			}
			break;

		case AVX:
			if (!random) {
				WriterAVX (chunk, size, loops, value);
			} else {
#ifdef IS_64BIT
				RandomWriterAVX (chunk_ptrs, size/256, loops, value);
#endif
			}
			break;

		case AVX512:
			if (!random) {
#ifdef IS_64BIT
				WriterAVX512 (chunk, size, loops, value);
#endif
			}
			break;

		case AVX_BYPASS:
			if (!random) {
				WriterAVX_bypass (chunk, size, loops, value);
			}
			break;
#endif
		
		default:
			if (random)
				RandomWriter (chunk_ptrs, size/256, loops, value);
			else {
#ifdef x86
				if (size & 128)
					Writer_128bytes (chunk, size, loops, value);
				else
#endif
					Writer (chunk, size, loops, value);
			}
		}

		diff = DateTime_getMicrosecondTime () - t0;
	}

	$(console, print, "loops = ");
	$(console, print_unsigned, total_count);
	$(console, print, ", ");

	$(console, flush);

	int result = $(self, calculate_result, size, total_count, diff);
	$(console, flush);

	free ((void*)chunk0);

	if (chunk_ptrs)
		free (chunk_ptrs);

	return result;
}


//----------------------------------------------------------------------------
// Name:	Testing_read
// Purpose:	Performs sequential read on chunk of memory of specified size.
//----------------------------------------------------------------------------
long Testing_read (Testing *self, unsigned long size, TestingMode mode, bool random)
{
	unsigned long long loops;
	unsigned long long total_count = 0;
	unsigned long t0, diff=0;
	unsigned long *chunk;
	unsigned long *chunk0;
	unsigned long **chunk_ptrs = NULL;

	if (size & 127)
		error (__FUNCTION__, "Chunk size is not multiple of 128.");

	//-------------------------------------------------
	chunk0 = chunk = malloc (size+128);
	if (!chunk)
		error (__FUNCTION__, "Out of memory");

	memset (chunk, 0, size);

	if (mode != AVX512) {
		unsigned long tmp = (unsigned long) chunk;
		if (tmp & 31) {
			tmp -= (tmp & 31);
			tmp += 32;
			chunk = (unsigned long*) tmp;
		}
	} else {
		unsigned long tmp = (unsigned long) chunk;
		if (tmp & 63) {
			tmp -= (tmp & 63);
			tmp += 64;
			chunk = (unsigned long*) tmp;
		}
	}

	//----------------------------------------
	// Set up random pointers to chunks.
	//
	if (random) {
		int nChunks = size/256;
		chunk_ptrs = (unsigned long**) malloc (sizeof (unsigned long*) * nChunks);
		if (!chunk_ptrs)
			error (__FUNCTION__, "Out of memory.");

		//----------------------------------------
		// Store pointers to all chunks into array.
		//
		int i;
		for (i = 0; i < nChunks; i++) {
			chunk_ptrs [i] = (unsigned long*) (((char*)chunk) + 256 * i);
		}

		//----------------------------------------
		// Randomize the array of chunk pointers.
		//
		int k = 200;
		while (k--) {
			for (i = 0; i < nChunks; i++) {
				int j = rand() % nChunks;
				if (i != j) {
					unsigned long *ptr = chunk_ptrs [i];
					chunk_ptrs [i] = chunk_ptrs [j];
					chunk_ptrs [j] = ptr;
				}
			}
		}
	}

	//-------------------------------------------------
	if (random)
		$(console, print, "Random read ");
	else
		$(console, print, "Sequential read ");

	switch (mode) {
	case NEON_64BIT:
		$(console, print, "(64-bit), size = ");
		break;
	case NEON_128BIT:
		$(console, print, "(128-bit), size = ");
		break;

#ifdef x86
	case SSE2:
		$(console, print, "(128-bit), size = ");
		break;
	case AVX:
		$(console, print, "(256-bit), size = ");
		break;
	case AVX512:
		$(console, print, "(512-bit), size = ");
		break;
	case AVX_BYPASS:
                $(console, print, "bypassing cache (256-bit), size = ");
		break;
	case SSE2_BYPASS:
                $(console, print, "bypassing cache (128-bit), size = ");
		break;
#endif

	default:
#ifdef IS_64BIT
		$(console, print, "(64-bit), size = ");
#else
		$(console, print, "(32-bit), size = ");
#endif
	}

	print_size (size);
	$(console, print, ", ");

	$(console, flush);

	loops = (1 << 29) / size;	// XX need to adjust for CPU MHz
	if (loops < 1)
		loops = 1;
	
	t0 = DateTime_getMicrosecondTime ();

	while (diff < usec_per_test) {
		total_count += loops;

		switch (mode) {
#ifdef __arm__
		case NEON_64BIT:
			// Not needed any longer.
			break;
		case NEON_128BIT:
			if (random)
				RandomReaderVector (chunk_ptrs, size/256, loops);
			else
				ReaderVector (chunk, size, loops);
			break;
#endif

#ifdef x86
		case SSE2:
			if (random)
				RandomReaderSSE2 (chunk_ptrs, size/256, loops);
			else {
				if (size & 128)
					ReaderSSE2_128bytes (chunk, size, loops);
				else
					ReaderSSE2 (chunk, size, loops);
			}
			break;
		
		case SSE2_BYPASS:
			// No random reader for bypass.
			//
			if (random)
				RandomReaderSSE2_bypass (chunk_ptrs, size/256, loops);
			else {
				if (size & 128)
					ReaderSSE2_128bytes_bypass (chunk, size, loops);
				else
					ReaderSSE2_bypass (chunk, size, loops);
			}
			break;

		case AVX:
			if (!random) {
				ReaderAVX (chunk, size, loops);
			} else {
#ifdef IS_64BIT
				RandomReaderAVX (chunk_ptrs, size/256, loops);
#endif
			}
			break;
		
		case AVX512:
			if (!random) {
#ifdef IS_64BIT
				ReaderAVX512 (chunk, size, loops);
#endif
			}
			break;
#endif

		default:
			if (random) {
				RandomReader (chunk_ptrs, size/256, loops);
			} else {
#ifdef x86
				if (size & 128)
					Reader_128bytes (chunk, size, loops);
				else
#endif
					Reader (chunk, size, loops);
			}
		}

		diff = DateTime_getMicrosecondTime () - t0;
	}

	$(console, print, "loops = ");
	$(console, print_unsigned, total_count);
	$(console, print, ", ");

	int result = $(self, calculate_result, size, total_count, diff);
	$(console, flush);

	free (chunk0);

	if (chunk_ptrs)
		free (chunk_ptrs);

	return result;
}

//----------------------------------------------------------------------------
// Name:	Testing_copy
// Purpose:	Performs sequential memory copy.
//----------------------------------------------------------------------------
long Testing_copy (Testing *self, unsigned long size, TestingMode mode)
{
#ifdef x86
	unsigned long loops;
	unsigned long long total_count = 0;
	unsigned long t0, diff=0;
	unsigned char *chunk_src;
	unsigned char *chunk_dest;
	unsigned char *chunk_src0;
	unsigned char *chunk_dest0;

	if (size & 127)
		error (__FUNCTION__, "Chunk size is not multiple of 128.");

	//-------------------------------------------------
	chunk_src0 = chunk_src = malloc (size+64);
	if (!chunk_src)
		error (__FUNCTION__, "Out of memory");
	chunk_dest0 = chunk_dest = malloc (size+64);
	if (!chunk_dest)
		error (__FUNCTION__, "Out of memory");

	memset (chunk_src, 100, size);
	memset (chunk_dest, 200, size);
	
	unsigned long tmp = (unsigned long) chunk_src;
	if (tmp & 31) {
		tmp -= (tmp & 31);
		tmp += 32;
		chunk_src = (unsigned char*) tmp;
	}
	tmp = (unsigned long) chunk_dest;
	if (tmp & 31) {
		tmp -= (tmp & 31);
		tmp += 32;
		chunk_dest = (unsigned char*) tmp;
	}

	//-------------------------------------------------
	$(console, print, "Sequential copy ");

	if (mode == SSE2) {
		$(console, print, "(128-bit), size = ");
	}
	else if (mode == AVX) {
		$(console, print, "(256-bit), size = ");
	}
	else {
#ifdef IS_64BIT
		$(console, print, "(64-bit), size = ");
#else
		$(console, print, "(32-bit), size = ");
#endif
	}

	print_size (size);
	$(console, print, ", ");

	$(console, flush);

	loops = (1 << 26) / size;	// XX need to adjust for CPU MHz
	if (loops < 1)
		loops = 1;
	
	t0 = DateTime_getMicrosecondTime ();

	while (diff < usec_per_test) {
		total_count += loops;

		if (mode == SSE2)  {
#ifdef IS_64BIT
			if (size & 128)
				CopySSE_128bytes (chunk_dest, chunk_src, size, loops);
			else
				CopySSE (chunk_dest, chunk_src, size, loops);
#else
			CopySSE (chunk_dest, chunk_src, size, loops);
#endif
		}
		else if (mode == AVX) {
			if (!(size & 128))
				CopyAVX (chunk_dest, chunk_src, size, loops);
		}

		diff = DateTime_getMicrosecondTime () - t0;
	}

	$(console, printf, "loops = %llu, ", total_count);

	int result = $(self, calculate_result, size, total_count, diff);
	$(console, flush);

	free (chunk_src0);
	free (chunk_dest0);

	return result;
#else
	return 0;
#endif
}

long Testing_registerToRegisterTest (Testing *self)
{
	long long total_count = 0;
        time_t t0 = DateTime_getMicrosecondTime ();
	
	int i;
	for (i=0; i < N_INC_OUTER_LOOPS; i++) {
		RegisterToRegister (REGISTER_TRANSFERS_COUNT);
	}
	long diff = DateTime_getMicrosecondTime () - t0;
	if (diff > 0) {
		long double d = N_INC_OUTER_LOOPS;
		d *= REGISTER_TRANSFERS_COUNT;
		d *= N_INC_PER_INNER;
		d /= diff;
		d *= 1000000; // usec->sec
		d /= 1000000000; // billions/sec
#ifdef IS_64BIT
		printf ("64-bit register-to-register transfers per second: %.2Lf billion\n", d);
#else
		printf ("32-bit register-to-register transfers per second: %.2Lf billion\n", d);
#endif
		total_count += REGISTER_TRANSFERS_COUNT;

		diff = DateTime_getMicrosecondTime () - t0;
	}

	return 0;
}

static long Testing_registerToVectorTest (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

	//--------------------------------------
#ifdef IS_64BIT
	$(console, print, "Main register to vector register transfers (64-bit) ");
#else
	$(console, print, "Main register to vector register transfers (32-bit) ");
#endif
	$(console, flush);

	t0 = DateTime_getMicrosecondTime ();
	diff = 0;
	total_count = 0;
	while (diff < usec_per_test)
	{
		RegisterToVector (VREGISTER_TRANSFERS_COUNT);
		total_count += VREGISTER_TRANSFERS_COUNT;

		diff = DateTime_getMicrosecondTime () - t0;
	}

	$(self, calculate_result, 256, total_count, diff);
	$(console, flush);
#endif
	return 0;
}

static long Testing_vectorToRegisterTest (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

	//--------------------------------------
#ifdef IS_64BIT
	$(console, print, "Vector register to main register transfers (64-bit) ");
#else
	$(console, print, "Vector register to main register transfers (32-bit) ");
#endif
	$(console, flush);

	t0 = DateTime_getMicrosecondTime ();
	diff = 0;
	total_count = 0;
	while (diff < usec_per_test)
	{
		VectorToRegister (VREGISTER_TRANSFERS_COUNT);
		total_count += VREGISTER_TRANSFERS_COUNT;

		diff = DateTime_getMicrosecondTime () - t0;
	}

	$(self, calculate_result, 256, total_count, diff);
	$(console, flush);
#endif
	return 0;
}

static long Testing_vectorToVectorTest128 (Testing *self)
{
	long long total_count = 0;
        time_t t0 = DateTime_getMicrosecondTime ();
	
	int i;
	for (i=0; i < N_INC_OUTER_LOOPS; i++) {
		VectorToVector128 (VREGISTER_TRANSFERS_COUNT);
		total_count += VREGISTER_TRANSFERS_COUNT;
	}
	long diff = DateTime_getMicrosecondTime () - t0;
	if (diff > 0) {
		long double d = N_INC_OUTER_LOOPS;
		d *= VREGISTER_TRANSFERS_COUNT;
		d *= N_INC_PER_INNER;
		d /= diff;
		d *= 1000000; // usec->sec
		d /= 1000000000; // billions/sec
		printf ("128-bit vector register to vector register transfers per second: %.2Lf billion\n", d);
		diff = DateTime_getMicrosecondTime () - t0;
	}

	return 0;
}

static long Testing_vectorToVectorTest256 (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

	//--------------------------------------
	if (self->use_avx) {
		$(console, print, "Vector register to vector register transfers (256-bit) ");
		$(console, flush);

		t0 = DateTime_getMicrosecondTime ();
		diff = 0;
		total_count = 0;
		while (diff < usec_per_test)
		{
			VectorToVector256 (VREGISTER_TRANSFERS_COUNT);
			total_count += VREGISTER_TRANSFERS_COUNT;

			diff = DateTime_getMicrosecondTime () - t0;
		}

		$(self, calculate_result, 256, total_count, diff);
		$(console, flush);
	}
#endif
	return 0;
}

static long Testing_vectorToRegister8 (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

	//--------------------------------------
	if (self->use_sse4) {
		$(console, print, "Vector 8-bit datum to main register transfers ");
		$(console, flush);

		t0 = DateTime_getMicrosecondTime ();
		diff = 0;
		total_count = 0;
		while (diff < usec_per_test)
		{
			Vector8ToRegister (VREGISTER_TRANSFERS_COUNT);
			total_count += VREGISTER_TRANSFERS_COUNT;

			diff = DateTime_getMicrosecondTime () - t0;
		}

		$(self, calculate_result, 64, total_count, diff);
		$(console, flush);
	}
#endif
	return 0;
}

static long Testing_vectorToRegister16 (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

	//--------------------------------------
	$(console, print, "Vector 16-bit datum to main register transfers ");
	$(console, flush);

	t0 = DateTime_getMicrosecondTime ();
	diff = 0;
	total_count = 0;
	while (diff < usec_per_test)
	{
		Vector16ToRegister (VREGISTER_TRANSFERS_COUNT);
		total_count += VREGISTER_TRANSFERS_COUNT;

		diff = DateTime_getMicrosecondTime () - t0;
	}

	$(self, calculate_result, 128, total_count, diff);
	$(console, flush);
#endif
	return 0;
}

static long Testing_vectorToRegister32 (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

	//--------------------------------------
	if (self->use_sse4) {
		$(console, print, "Vector 32-bit datum to main register transfers ");
		$(console, flush);

		t0 = DateTime_getMicrosecondTime ();
		diff = 0;
		total_count = 0;
		while (diff < usec_per_test)
		{
			Vector32ToRegister (VREGISTER_TRANSFERS_COUNT);
			total_count += VREGISTER_TRANSFERS_COUNT;

			diff = DateTime_getMicrosecondTime () - t0;
		}

		$(self, calculate_result, 256, total_count, diff);
		$(console, flush);
	}
#endif
	return 0;
}

static long Testing_vectorToRegister64 (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

	//--------------------------------------
	if (self->use_sse4) {
		$(console, print, "Vector 64-bit datum to main register transfers ");
		$(console, flush);

		t0 = DateTime_getMicrosecondTime ();
		diff = 0;
		total_count = 0;
		while (diff < usec_per_test)
		{
			Vector64ToRegister (VREGISTER_TRANSFERS_COUNT);
			total_count += VREGISTER_TRANSFERS_COUNT;

			diff = DateTime_getMicrosecondTime () - t0;
		}

		$(self, calculate_result, 256, total_count, diff);
		$(console, flush);
	}
#endif
	return 0;
}

static long Testing_registerToVector8 (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

	//--------------------------------------
	if (self->use_sse4) {
		$(console, print, "Main register 8-bit datum to vector register transfers ");
		$(console, flush);

		t0 = DateTime_getMicrosecondTime ();
		diff = 0;
		total_count = 0;
		while (diff < usec_per_test)
		{
			Register8ToVector (VREGISTER_TRANSFERS_COUNT);
			total_count += VREGISTER_TRANSFERS_COUNT;

			diff = DateTime_getMicrosecondTime () - t0;
		}

		$(self, calculate_result, 64, total_count, diff);
		$(console, flush);
	}
#endif
	return 0;
}

static long Testing_registerToVector16 (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

	//--------------------------------------
	$(console, print, "Main register 16-bit datum to vector register transfers ");
	$(console, flush);

	t0 = DateTime_getMicrosecondTime ();
	diff = 0;
	total_count = 0;
	while (diff < usec_per_test)
	{
		Register16ToVector (VREGISTER_TRANSFERS_COUNT);
		total_count += VREGISTER_TRANSFERS_COUNT;

		diff = DateTime_getMicrosecondTime () - t0;
	}

	$(self, calculate_result, 128, total_count, diff);
	$(console, flush);
#endif
	return 0;
}

static long Testing_registerToVector32 (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

	//--------------------------------------
	if (self->use_sse4) {
		$(console, print, "Main register 32-bit datum to vector register transfers ");
		$(console, flush);

		t0 = DateTime_getMicrosecondTime ();
		diff = 0;
		total_count = 0;
		while (diff < usec_per_test)
		{
			Register32ToVector (VREGISTER_TRANSFERS_COUNT);
			total_count += VREGISTER_TRANSFERS_COUNT;

			diff = DateTime_getMicrosecondTime () - t0;
		}

		$(self, calculate_result, 256, total_count, diff);
		$(console, flush);
	}
#endif
	return 0;
}

static long Testing_registerToVector64 (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

	//--------------------------------------
	if (self->use_sse4) {
		$(console, print, "Main register 64-bit datum to vector register transfers ");
		$(console, flush);

		t0 = DateTime_getMicrosecondTime ();
		diff = 0;
		total_count = 0;
		while (diff < usec_per_test)
		{
			Register64ToVector (VREGISTER_TRANSFERS_COUNT);
			total_count += VREGISTER_TRANSFERS_COUNT;

			diff = DateTime_getMicrosecondTime () - t0;
		}

		$(self, calculate_result, 256, total_count, diff);
		$(console, flush);
	}
#endif
	return 0;
}

static long Testing_stackPop (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

#ifdef IS_64BIT
	$(console, print, "Stack-to-register transfers (64-bit) ");
#else
	$(console, print, "Stack-to-register transfers (32-bit) ");
#endif
	$(console, flush);

	//--------------------------------------
	diff = 0;
	total_count = 0;
	t0 = DateTime_getMicrosecondTime ();
	while (diff < usec_per_test)
	{
		StackReader (REGISTER_TRANSFERS_COUNT);
		total_count += REGISTER_TRANSFERS_COUNT;

		diff = DateTime_getMicrosecondTime () - t0;
	}

	$(self, calculate_result, 256, total_count, diff);
	$(console, flush);
#endif
	return 0;
}

static long Testing_stackPush (Testing *self)
{
#ifdef x86
	long long total_count = 0;
	unsigned long t0;
	unsigned long diff = 0;

#ifdef IS_64BIT
	$(console, print, "Register-to-stack transfers (64-bit) ");
#else
	$(console, print, "Register-to-stack transfers (32-bit) ");
#endif
	$(console, flush);

	//--------------------------------------
	diff = 0;
	total_count = 0;
	t0 = DateTime_getMicrosecondTime ();
	while (diff < usec_per_test)
	{
		StackWriter (REGISTER_TRANSFERS_COUNT);
		total_count += REGISTER_TRANSFERS_COUNT;

		diff = DateTime_getMicrosecondTime () - t0;
	}

	$(self, calculate_result, 256, total_count, diff);
	$(console, flush);
#endif
	return 0;
}

static long Testing_memsetTest (Testing *self)
{
	char *a1;
	unsigned long t, t0;
	int i;

	#define NT_SIZE (64*1024*1024)
	#define NT_SIZE2 (100)

	a1 = malloc (NT_SIZE);
	if (!a1)
		error (__FUNCTION__, "Out of memory");
	
	//--------------------------------------
	t0 = DateTime_getMicrosecondTime ();
	for (i=0; i<NT_SIZE2; i++) {
		memset (a1, i, NT_SIZE);
	}
	t = DateTime_getMicrosecondTime ();
	uint64_t dt = t-t0;
	free (a1);

	$(console, print, "Library: memset ");
	$(self, calculate_result, NT_SIZE, NT_SIZE2, dt);
	$(console, flush);
	return 0;
}

static long Testing_memcpyTest (Testing *self)
{
	char *a1, *a2;
	unsigned long t, t0;
	int i;

	a1 = malloc (NT_SIZE);
	if (!a1)
		error (__FUNCTION__, "Out of memory");
	
	a2 = malloc (NT_SIZE);
	if (!a2)
		error (__FUNCTION__, "Out of memory");

	//--------------------------------------
	t0 = DateTime_getMicrosecondTime ();
	for (i=0; i<NT_SIZE2; i++) {
		// NOTE: Do not compile this with -O3 on the Mac, it becomes a no-op.
		memcpy (a2, a1, NT_SIZE);
		a1[i] = i & 0xff;
	}
	t = DateTime_getMicrosecondTime ();
	free (a1);
	free (a2);

	$(console, print, "Library: memcpy ");
	$(self, calculate_result, NT_SIZE, NT_SIZE2, t-t0);
	$(console, flush);
	return 0;
}

static long Testing_incrementRegisters (Testing *self)
{
        time_t t0 = DateTime_getMicrosecondTime ();

	int i;
	for (i=0; i<N_INC_OUTER_LOOPS; i++) {
		IncrementRegisters (N_INC_INNER_LOOPS);
	}
	time_t diff = DateTime_getMicrosecondTime () - t0;
	if (diff > 0) {
		long double d = N_INC_OUTER_LOOPS;
		d *= N_INC_INNER_LOOPS;
		d *= N_INC_PER_INNER;
		d /= diff;
		d *= 1000000; // usec->sec
		d /= 1000000000; // billions/sec
#ifdef IS_64BIT
		printf ("64-bit register increments per second: %.2Lf billion\n", d);
#else
		printf ("32-bit register increments per second: %.2Lf billion\n", d);
#endif
	}
	return 0;
}

static long Testing_incrementStack (Testing *self)
{
        time_t t0 = DateTime_getMicrosecondTime ();
	
	int i;
	for (i=0; i < N_INC_OUTER_LOOPS; i++) {
		IncrementStack (N_INC_INNER_LOOPS);
	}
	long diff = DateTime_getMicrosecondTime () - t0;
	if (diff > 0) {
		long double d = N_INC_OUTER_LOOPS;
		d *= N_INC_INNER_LOOPS;
		d *= N_INC_PER_INNER;
		d /= diff;
		d *= 1000000; // usec->sec
		d /= 1000000000; // billions/sec
#ifdef IS_64BIT
		printf ("64-bit stack value increments per second: %.2Lf billion\n", d);
#else
		printf ("32-bit stack value increments per second: %.2Lf billion\n", d);
#endif
	}
	return 0;
}

TestingClass* TestingClass_prepare ()
{
	PREPARE_CLASS_STRUCT(Testing,Object)

	SET_OVERRIDDEN_METHOD_POINTER(Testing,describe);
	SET_OVERRIDDEN_METHOD_POINTER(Testing,destroy);

	SET_METHOD_POINTER(Testing,read);
	SET_METHOD_POINTER(Testing,write);
	SET_METHOD_POINTER(Testing,copy);
	SET_METHOD_POINTER(Testing,memsetTest);
	SET_METHOD_POINTER(Testing,memcpyTest);
	SET_METHOD_POINTER(Testing,calculate_result);
	SET_METHOD_POINTER(Testing,registerToRegisterTest);
	SET_METHOD_POINTER(Testing,registerToVectorTest);
	SET_METHOD_POINTER(Testing,vectorToRegisterTest);
	SET_METHOD_POINTER(Testing,vectorToVectorTest128);
	SET_METHOD_POINTER(Testing,vectorToVectorTest256);
	SET_METHOD_POINTER(Testing,vectorToRegister8);
	SET_METHOD_POINTER(Testing,vectorToRegister16);
	SET_METHOD_POINTER(Testing,vectorToRegister32);
	SET_METHOD_POINTER(Testing,vectorToRegister64);
	SET_METHOD_POINTER(Testing,registerToVector8);
	SET_METHOD_POINTER(Testing,registerToVector16);
	SET_METHOD_POINTER(Testing,registerToVector32);
	SET_METHOD_POINTER(Testing,registerToVector64);
	SET_METHOD_POINTER(Testing,stackPop);
	SET_METHOD_POINTER(Testing,stackPush);
	SET_METHOD_POINTER(Testing,incrementRegisters);
	SET_METHOD_POINTER(Testing,incrementStack);
	
        VALIDATE_CLASS_STRUCT(_TestingClass);
	return _TestingClass;
}

Testing *Testing_init (Testing *self)
{
 	if (!_TestingClass)
                TestingClass_prepare ();

        ooc_bzero (self, sizeof(Testing));
        Object_init ((Object*) self);

        self->is_a = _TestingClass;

        return self;
}

