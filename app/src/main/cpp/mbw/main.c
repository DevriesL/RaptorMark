/*============================================================================
  bandwidth, a benchmark to estimate memory transfer bandwidth.
  Copyright (C) 2005-2019 by Zack T Smith.

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

#include <string.h>
#include <unistd.h>
#include <time.h>
#include <stdio.h>
#include <stdlib.h>

#include "Testing.h"

int (*callback_func)(const char *) = NULL;

static bool limit_at_128MB = true;
static bool test_seq_rd = false;
static bool test_seq_wr = false;
static bool test_rand_rd = false;
static bool test_rand_wr = false;
static bool test_app_perf = false;

//----------------------------------------
// Parameters for the tests.
//

unsigned long usec_per_test;

static int chunk_sizes[] = {
        256,
        512,
        768,
        1024,
        1280,
        2048,
        3072,
        4096,
        6144,
        8192,    // Some processors' L1 data caches are only 8kB.
        12288,
        16384,
        20480,
        24576,
        28672,
        32768,    // Common L1 data cache size.
        34 * 1024,
        36 * 1024,
        40960,
        49152,
        65536,
        131072,    // Old L2 cache size.
        192 * 1024,
        256 * 1024,    // Old L2 cache size.
        320 * 1024,
        384 * 1024,
        480 * 1024,
        512 * 1024,    // Old L2 cache size.
        768 * 1024,
        1 << 20,    // 1 MB = common L2 cache size.
        (1024 + 256) * 1024,    // 1.25
        (1024 + 512) * 1024,    // 1.5
        (1024 + 768) * 1024,    // 1.75
        1 << 21,    // 2 MB = common L2 cache size.
        (2048 + 256) * 1024,    // 2.25
        (2048 + 512) * 1024,    // 2.5
        (2048 + 768) * 1024,    // 2.75
        3072 * 1024,    // 3 MB = common L2 cache size.
        3407872, // 3.25 MB
        3 * 1024 * 1024 + 1024 * 512,    // 3.5 MB
        1 << 22,    // 4 MB
        5242880,    // 5 megs (Core i7-11xxxH has 5 MB L2)
        6291456,    // 6 megs (common L2 cache size)
        7 * 1024 * 1024,
        8 * 1024 * 1024, // Xeon E3's often has 8MB L3
        9 * 1024 * 1024,
        10 * 1024 * 1024, // Xeon E5-2609 has 10MB L3
        11 * 1024 * 1024,
        12 * 1024 * 1024, // Core i7-11xx[x] often have 12MB L3
        13 * 1024 * 1024,
        14 * 1024 * 1024,
        15 * 1024 * 1024, // Xeon E6-2630 has 15MB L3
        16 * 1024 * 1024,
        20 * 1024 * 1024, // Xeon E5-2690 has 20MB L3
        21 * 1024 * 1024,
        32 * 1024 * 1024,
        48 * 1024 * 1024,
        64 * 1024 * 1024,
        72 * 1024 * 1024,
        96 * 1024 * 1024,
        128 * 1024 * 1024,
        160 * 1024 * 1024,
        192 * 1024 * 1024,
        224 * 1024 * 1024,
        256 * 1024 * 1024,
        320 * 1024 * 1024,
        512 * 1024 * 1024,
        0
};

static double chunk_sizes_log2[sizeof(chunk_sizes) / sizeof(int)];

//============================================================================
// Output multiplexor. 
//============================================================================

void
dataAddDatum(long x, long y) {
    char string[32];
    snprintf(string, sizeof(string) - 1, "%ld, %ld", x, y);
    callback_func(string);
}

//----------------------------------------------------------------------------
// Name:	main
//----------------------------------------------------------------------------
int
mbw(int argc, char *argv[], void *callback_ptr) {
    callback_func = callback_ptr;

    int i, chunk_size;

    usec_per_test = 5000000;    // 5 seconds per memory test.

    --argc;
    ++argv;

    i = 0;
    while (i < argc) {
        char *s = argv[i++];

        if (!strcmp("--slow", s)) {
            usec_per_test = 20000000;    // 20 seconds per test.
        } else if (!strcmp("--limit", s)) {
            limit_at_128MB = true;
        } else if (!strcmp("--fast", s)) {
            usec_per_test = 1000000; // 1 second per test.
        } else if (!strcmp("--faster", s)) {
            usec_per_test = 500000;    // 0.5 seconds per test.
        } else if (!strcmp("--fastest", s)) {
            usec_per_test = 50000;    // 0.05 seconds per test.
        } else if (!strcmp("--randread", s)) {
            test_rand_rd = true;
        } else if (!strcmp("--randwrite", s)) {
            test_rand_wr = true;
        } else if (!strcmp("--read", s)) {
            test_seq_rd = true;
        } else if (!strcmp("--write", s)) {
            test_seq_wr = true;
        } else if (!strcmp("--app", s)) {
            test_app_perf = true;
        }
    }

    for (i = 0; chunk_sizes[i] && i < sizeof(chunk_sizes) / sizeof(int); i++) {
        chunk_sizes_log2[i] = log2(chunk_sizes[i]);
    }

    unsigned long chunk_limit = limit_at_128MB ? 128 << 20 : 1 << 31;

    // SEQ READS
    if (test_seq_rd) {
        //------------------------------------------------------------
        // Sequential non-vector reads.
        //
        i = 0;
        while ((chunk_size = chunk_sizes[i++]) && chunk_size <= chunk_limit) {
            long amount = Testing_read(chunk_size, NO_SSE2, false);
            dataAddDatum(chunk_size, amount);
        }

        //------------------------------------------------------------
        // NEON 128 bit sequential reads.
        //
        i = 0;
        while ((chunk_size = chunk_sizes[i++]) && chunk_size <= chunk_limit) {
            long amount = Testing_read(chunk_size, NEON_128BIT, false);
            dataAddDatum(chunk_size, amount);
        }
    }

    // SEQ WRITES
    if (test_seq_wr) {
        //------------------------------------------------------------
        // Sequential non-vector writes.
        //
        i = 0;
        while ((chunk_size = chunk_sizes[i++]) && chunk_size <= chunk_limit) {
            if (!(chunk_size & 128)) {
                long amount = Testing_write(chunk_size, NO_SSE2, false);
                dataAddDatum(chunk_size, amount);
            }
        }

        //------------------------------------------------------------
        // NEON 128 bit sequential writes.
        //
        i = 0;
        while ((chunk_size = chunk_sizes[i++]) && chunk_size <= chunk_limit) {
            long amount = Testing_write(chunk_size, NEON_128BIT, false);
            dataAddDatum(chunk_size, amount);
        }
    }

    //RANDOM READS
    if (test_rand_rd) {
        //------------------------------------------------------------
        // Random non-vector reads.
        //
        srand(time(NULL));

        i = 0;
        while ((chunk_size = chunk_sizes[i++]) && chunk_size <= chunk_limit) {
            if (!(chunk_size & 128)) {
                long amount = Testing_read(chunk_size, NO_SSE2, true);
                dataAddDatum(chunk_size, amount);
            }
        }

        //------------------------------------------------------------
        // NEON 128 bit random reads.
        //
        srand(time(NULL));

        i = 0;
        while ((chunk_size = chunk_sizes[i++]) && chunk_size <= chunk_limit) {
            long amount = Testing_read(chunk_size, NEON_128BIT, true);
            dataAddDatum(chunk_size, amount);
        }
    }

    //RANDOM WRITES
    if (test_rand_wr) {
        //------------------------------------------------------------
        // Random non-vector writes.
        //
        srand(time(NULL));

        i = 0;
        while ((chunk_size = chunk_sizes[i++]) && chunk_size <= chunk_limit) {
            if (!(chunk_size & 128)) {
                long amount = Testing_write(chunk_size, NO_SSE2, true);
                dataAddDatum(chunk_size, amount);
            }
        }

        //------------------------------------------------------------
        // NEON 128 bit random writes.
        //
        srand(time(NULL));

        i = 0;
        while ((chunk_size = chunk_sizes[i++]) && chunk_size <= chunk_limit) {
            long amount = Testing_write(chunk_size, NEON_128BIT, true);
            dataAddDatum(chunk_size, amount);
        }
    }


    // Application
    //------------------------------------------------------------
    // C library performance.
    //
    if (test_app_perf) {
        dataAddDatum(0, Testing_memsetTest());
        dataAddDatum(0, Testing_memcpyTest());
    }

    //------------------------------------------------------------
    // Register to register.
    //
    // Testing_registerToRegisterTest();
    // Testing_vectorToVectorTest128();

    //------------------------------------------------------------
    // Register vs stack.
    //
    // Testing_incrementRegisters();
    // Testing_incrementStack();

    return 0;
}
