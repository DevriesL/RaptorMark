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
#include "Testing.h"

#define REGISTER_TRANSFERS_COUNT 9997
#define VREGISTER_TRANSFERS_COUNT 9977

#define N_INC_OUTER_LOOPS 16537L
#define N_INC_INNER_LOOPS 32767L
#define N_INC_PER_INNER 32

extern int (*callback_func)(const char *);

extern unsigned long usec_per_test;

extern void *memset(void *b, int c, size_t len);

extern void *memcpy(void *restrict dst, const void *restrict src, size_t n);

uint64_t DateTime_getMicrosecondTime() {
    struct timeval tv;
    struct timezone tz;
    memset(&tv, 0, sizeof(struct timeval));
    memset(&tz, 0, sizeof(struct timezone));
    gettimeofday(&tv, &tz);
    uint64_t microseconds = tv.tv_sec;
    microseconds *= 1000000LU;
    microseconds += tv.tv_usec;
    return microseconds;
}

//----------------------------------------------------------------------------
// Name:	calculate_result
// Purpose:	Calculates and prints a result.
// Returns:	10 times the number of megabytes per second.
//----------------------------------------------------------------------------
static int
Testing_calculate_result(unsigned long chunk_size, long long total_loops, uint64_t diff) {
    if (!diff) {
        return 0;
    }

    long double result = (long double) chunk_size;
    result *= (long double) total_loops;
    result *= 1000000.;
    result /= 1048576.;
    result /= (long double) diff;

    char string[32];
    snprintf(string, sizeof(string) - 1, "%.1Lf MB/s", result);

    return (long) (10.0 * result);
}

//----------------------------------------------------------------------------
// Name:	Testing_write
// Purpose:	Performs write on chunk of memory of specified size.
//----------------------------------------------------------------------------
long Testing_write(unsigned long size, TestingMode mode, bool random) {
    unsigned char *chunk;
    unsigned char *chunk0;
    unsigned long loops;
    unsigned long long total_count = 0;
    unsigned long value = 0x1234567689abcdef;
    unsigned long diff = 0, t0;
    unsigned long **chunk_ptrs = NULL;

    if (size & 255) {
        return 0;
    }

    //-------------------------------------------------
    chunk0 = malloc(size + 128);
    chunk = chunk0;
    if (!chunk) {
        return 0;
    }

    unsigned long tmp = (unsigned long) chunk;
    if (tmp & 31) {
        tmp -= (tmp & 31);
        tmp += 32;
        chunk = (unsigned char *) tmp;
    }

    //----------------------------------------
    // Set up random pointers to chunks.
    //
    if (random) {
        unsigned long nChunks = size / 256;
        chunk_ptrs = (unsigned long **) malloc(sizeof(unsigned long *) * nChunks);
        if (!chunk_ptrs) {
            return 0;
        }

        //-----------------------------------------
        // Store pointers to all chunks in an array.
        //
        int i;
        for (i = 0; i < nChunks; i++) {
            chunk_ptrs[i] = (unsigned long *) (((char *) chunk) + 256 * i);
        }

        //----------------------------------------
        // Randomize the array of chunk pointers.
        //
        int k = 200;
        while (k--) {
            for (i = 0; i < nChunks; i++) {
                int j = rand() % nChunks;
                if (i != j) {
                    unsigned long *ptr = chunk_ptrs[i];
                    chunk_ptrs[i] = chunk_ptrs[j];
                    chunk_ptrs[j] = ptr;
                }
            }
        }
    }

    loops = (1 << 26) / size;// XX need to adjust for CPU MHz
    if (loops < 1)
        loops = 1;

    t0 = DateTime_getMicrosecondTime();

    while (diff < usec_per_test) {
        total_count += loops;

        switch (mode) {
            case NEON_64BIT:
                // Not needed any longer.
                break;
            case NEON_128BIT:
                if (random)
                    RandomWriterVector(chunk_ptrs, size / 256, loops, value);
                else
                    WriterVector(chunk, size, loops, value);
                break;
            default:
                if (random)
                    RandomWriter(chunk_ptrs, size / 256, loops, value);
                else {
                    Writer(chunk, size, loops, value);
                }
        }

        diff = DateTime_getMicrosecondTime() - t0;
    }

    int result = Testing_calculate_result(size, total_count, diff);

    free((void *) chunk0);

    if (chunk_ptrs)
        free(chunk_ptrs);

    return result;
}


//----------------------------------------------------------------------------
// Name:	Testing_read
// Purpose:	Performs sequential read on chunk of memory of specified size.
//----------------------------------------------------------------------------
long Testing_read(unsigned long size, TestingMode mode, bool random) {
    unsigned long long loops;
    unsigned long long total_count = 0;
    unsigned long t0, diff = 0;
    unsigned long *chunk;
    unsigned long *chunk0;
    unsigned long **chunk_ptrs = NULL;

    if (size & 127) {
        return 0;
    }

    //-------------------------------------------------
    chunk0 = chunk = malloc(size + 128);
    if (!chunk) {
        return 0;
    }

    memset(chunk, 0, size);

    unsigned long tmp = (unsigned long) chunk;
    if (tmp & 31) {
        tmp -= (tmp & 31);
        tmp += 32;
        chunk = (unsigned long *) tmp;
    }

    //----------------------------------------
    // Set up random pointers to chunks.
    //
    if (random) {
        int nChunks = size / 256;
        chunk_ptrs = (unsigned long **) malloc(sizeof(unsigned long *) * nChunks);
        if (!chunk_ptrs) {
            return 0;
        }

        //----------------------------------------
        // Store pointers to all chunks into array.
        //
        int i;
        for (i = 0; i < nChunks; i++) {
            chunk_ptrs[i] = (unsigned long *) (((char *) chunk) + 256 * i);
        }

        //----------------------------------------
        // Randomize the array of chunk pointers.
        //
        int k = 200;
        while (k--) {
            for (i = 0; i < nChunks; i++) {
                int j = rand() % nChunks;
                if (i != j) {
                    unsigned long *ptr = chunk_ptrs[i];
                    chunk_ptrs[i] = chunk_ptrs[j];
                    chunk_ptrs[j] = ptr;
                }
            }
        }
    }

    loops = (1 << 29) / size;    // XX need to adjust for CPU MHz
    if (loops < 1)
        loops = 1;

    t0 = DateTime_getMicrosecondTime();

    while (diff < usec_per_test) {
        total_count += loops;

        switch (mode) {
            case NEON_64BIT:
                // Not needed any longer.
                break;
            case NEON_128BIT:
                if (random)
                    RandomReaderVector(chunk_ptrs, size / 256, loops);
                else
                    ReaderVector(chunk, size, loops);
                break;
            default:
                if (random) {
                    RandomReader(chunk_ptrs, size / 256, loops);
                } else {
                    Reader(chunk, size, loops);
                }
        }

        diff = DateTime_getMicrosecondTime() - t0;
    }

    int result = Testing_calculate_result(size, total_count, diff);

    free(chunk0);

    if (chunk_ptrs)
        free(chunk_ptrs);

    return result;
}

long Testing_registerToRegisterTest() {
    long long total_count = 0;
    time_t t0 = DateTime_getMicrosecondTime();

    int i;
    for (i = 0; i < N_INC_OUTER_LOOPS; i++) {
        RegisterToRegister(REGISTER_TRANSFERS_COUNT);
    }
    long diff = DateTime_getMicrosecondTime() - t0;
    if (diff > 0) {
        long double d = N_INC_OUTER_LOOPS;
        d *= REGISTER_TRANSFERS_COUNT;
        d *= N_INC_PER_INNER;
        d /= diff;
        d *= 1000000; // usec->sec
        d /= 1000000000; // billions/sec
        printf("64-bit register-to-register transfers per second: %.2Lf billion\n", d);
        total_count += REGISTER_TRANSFERS_COUNT;

        diff = DateTime_getMicrosecondTime() - t0;
    }

    return 0;
}

long Testing_vectorToVectorTest128() {
    long long total_count = 0;
    time_t t0 = DateTime_getMicrosecondTime();

    int i;
    for (i = 0; i < N_INC_OUTER_LOOPS; i++) {
        VectorToVector128(VREGISTER_TRANSFERS_COUNT);
        total_count += VREGISTER_TRANSFERS_COUNT;
    }
    long diff = DateTime_getMicrosecondTime() - t0;
    if (diff > 0) {
        long double d = N_INC_OUTER_LOOPS;
        d *= VREGISTER_TRANSFERS_COUNT;
        d *= N_INC_PER_INNER;
        d /= diff;
        d *= 1000000; // usec->sec
        d /= 1000000000; // billions/sec
        printf("128-bit vector register to vector register transfers per second: %.2Lf billion\n",
               d);
        diff = DateTime_getMicrosecondTime() - t0;
    }

    return 0;
}

long Testing_memsetTest() {
    char *a1;
    unsigned long t, t0;
    int i;

#define NT_SIZE (64*1024*1024)
#define NT_SIZE2 (100)

    a1 = malloc(NT_SIZE);
    if (!a1) {
        return 0;
    }

    //--------------------------------------
    t0 = DateTime_getMicrosecondTime();
    for (i = 0; i < NT_SIZE2; i++) {
        memset(a1, i, NT_SIZE);
    }
    t = DateTime_getMicrosecondTime();
    uint64_t dt = t - t0;
    free(a1);

    return Testing_calculate_result(NT_SIZE, NT_SIZE2, t - t0);
}

long Testing_memcpyTest() {
    char *a1, *a2;
    unsigned long t, t0;
    int i;

    a1 = malloc(NT_SIZE);
    if (!a1) {
        return 0;
    }

    a2 = malloc(NT_SIZE);
    if (!a2) {
        return 0;
    }

    //--------------------------------------
    t0 = DateTime_getMicrosecondTime();
    for (i = 0; i < NT_SIZE2; i++) {
        // NOTE: Do not compile this with -O3 on the Mac, it becomes a no-op.
        memcpy(a2, a1, NT_SIZE);
        a1[i] = i & 0xff;
    }
    t = DateTime_getMicrosecondTime();
    free(a1);
    free(a2);

    return Testing_calculate_result(NT_SIZE, NT_SIZE2, t - t0);
}

long Testing_incrementRegisters() {
    time_t t0 = DateTime_getMicrosecondTime();

    int i;
    for (i = 0; i < N_INC_OUTER_LOOPS; i++) {
        IncrementRegisters(N_INC_INNER_LOOPS);
    }
    time_t diff = DateTime_getMicrosecondTime() - t0;
    if (diff > 0) {
        long double d = N_INC_OUTER_LOOPS;
        d *= N_INC_INNER_LOOPS;
        d *= N_INC_PER_INNER;
        d /= diff;
        d *= 1000000; // usec->sec
        d /= 1000000000; // billions/sec
        printf("64-bit register increments per second: %.2Lf billion\n", d);
    }
    return 0;
}

long Testing_incrementStack() {
    time_t t0 = DateTime_getMicrosecondTime();

    int i;
    for (i = 0; i < N_INC_OUTER_LOOPS; i++) {
        IncrementStack(N_INC_INNER_LOOPS);
    }
    long diff = DateTime_getMicrosecondTime() - t0;
    if (diff > 0) {
        long double d = N_INC_OUTER_LOOPS;
        d *= N_INC_INNER_LOOPS;
        d *= N_INC_PER_INNER;
        d /= diff;
        d *= 1000000; // usec->sec
        d /= 1000000000; // billions/sec
        printf("64-bit stack value increments per second: %.2Lf billion\n", d);
    }
    return 0;
}
