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

#define GRAPH_WIDTH 1440
#define GRAPH_HEIGHT 900

#include "defs.h"
#include "OOC/MutableImage.h"
#include "OOC/SimpleGraphing.h"
#include "OOC/Console.h"
#include "OOC/colors.h"
#include "Testing.h"
#include "CPUCharacteristics.h"

#define TITLE_MEMORY_GRAPH "Results from ''bandwidth'' " RELEASE " by Zack Smith, https://zsmith.co"

#if defined(__WIN32__) || defined(__WIN64__)
#include <w32api/windows.h> // Cygwin
#include <inttypes.h>
#endif

static enum {
	OUTPUT_MODE_GRAPH=1,
	OUTPUT_MODE_CSV=2,
} outputMode;

static FILE *csv_output_file = NULL;
static char *csv_file_path = NULL;

// Mode to be nice and to keep CPU temperature low.
static bool nice_mode = false;
#define NICE_DURATION (2)
#define MAX_CPU_TEMP (75)

Console *console = NULL;

static bool limit_at_128MB = true;

static SimpleGraphing *graph = NULL;

//----------------------------------------
// Parameters for the tests.
//

unsigned long usec_per_test;

static int chunk_sizes[] = {
#ifdef x86
#ifdef REALLY_WANT_CHUNKSIZE_128
	128,
#endif
#endif
	256,
#ifdef x86
	384,
#endif
	512,
#ifdef x86
	640,
#endif
	768,
#ifdef x86
	896,
#endif
	1024,
	1280,
	2048,
	3072,
	4096,
	6144,
	8192,	// Some processors' L1 data caches are only 8kB.
	12288,
	16384,
	20480,
	24576,
	28672,
	32768,	// Common L1 data cache size.
	34*1024,
	36*1024,
	40960,
	49152,
	65536,
	131072,	// Old L2 cache size.
	192 * 1024,
	256 * 1024,	// Old L2 cache size.
	320 * 1024,
	384 * 1024,
	480 * 1024,
	512 * 1024,	// Old L2 cache size.
	768 * 1024,
	1 << 20,	// 1 MB = common L2 cache size.
	(1024 + 256) * 1024,	// 1.25
	(1024 + 512) * 1024,	// 1.5
	(1024 + 768) * 1024,	// 1.75
	1 << 21,	// 2 MB = common L2 cache size.
	(2048 + 256) * 1024,	// 2.25
	(2048 + 512) * 1024,	// 2.5
	(2048 + 768) * 1024,	// 2.75
	3072 * 1024,	// 3 MB = common L2 cache size. 
	3407872, // 3.25 MB
	3 * 1024 * 1024 + 1024 * 512,	// 3.5 MB
	1 << 22,	// 4 MB
	5242880,	// 5 megs (Core i7-11xxxH has 5 MB L2)
	6291456,	// 6 megs (common L2 cache size)
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
#ifndef __arm__
	48 * 1024 * 1024,
	64 * 1024 * 1024,
	72 * 1024 * 1024,
	96 * 1024 * 1024,
	128 * 1024 * 1024,
#ifdef TEST_L4
	160 * 1024 * 1024,
	192 * 1024 * 1024,
	224 * 1024 * 1024,
	256 * 1024 * 1024,
	320 * 1024 * 1024,
	512 * 1024 * 1024,
#endif
#endif
	0
};

static double chunk_sizes_log2 [sizeof(chunk_sizes)/sizeof(int)];

//============================================================================
// Output multiplexor. 
//============================================================================

void dataBegins (const char *title, const char *subtitle)
{
	require(outputMode) else { error (__FUNCTION__, "Bad output mode."); }
	require(title) else { error_null_parameter (__FUNCTION__); }
	//==========

	if (!title) {
		title = TITLE_MEMORY_GRAPH;
		subtitle = NULL;
	}
	else if (!subtitle) {
		subtitle = TITLE_MEMORY_GRAPH;
	}

	if (outputMode & OUTPUT_MODE_GRAPH) {
		if (graph)
			error (__FUNCTION__, "Graphing already initialized.");

		graph = SimpleGraphing_newWithSize (GRAPH_WIDTH, GRAPH_HEIGHT);
		retain (graph);

		$(graph, setXAxisMode, MODE_X_AXIS_LOG2);
		if (title) {
			$(graph, setTitle, String_newWithCString(title));
		}
		if (subtitle) {
			$(graph, setSubtitle, String_newWithCString(subtitle));
		}
	}	

	if (outputMode & OUTPUT_MODE_CSV) {
		if (csv_output_file)
			error (__FUNCTION__, "CSV file already initialized.");

		csv_output_file = fopen (csv_file_path, "wb");
		if (!csv_output_file) {
			error (__FUNCTION__, "Cannot open CSV output file.");
		}
		if (title)
			fprintf (csv_output_file, "%s\n", title);
	}
}

void dataBeginSection (const char *name, uint32_t parameter)
{
	require(outputMode) else { error (__FUNCTION__, "Bad output mode."); }
	require(name) else { error_null_parameter (__FUNCTION__); }
	//==========

	if (nice_mode) {
		sleep (NICE_DURATION);

#ifdef __APPLE__
#define POPEN_BUFSIZE (256)
		// Keep CPU temperature below 50 C.
		//
		int cpu_temperature = 0;
		bool done = true;
		do {
			FILE *f = popen ("sysctl machdep.xcpm.cpu_thermal_level | sed '\''s/machdep.xcpm.cpu_thermal_level/CPU temperature/'\''", "r");
			if (f) {
				char buffer [POPEN_BUFSIZE] = {0};
				if (0 < fread (buffer, 1, POPEN_BUFSIZE-1, f)) {
					int i;
					for (i=0; i < POPEN_BUFSIZE && buffer[i]; i++) {
						if (isdigit (buffer[i]))
							break;
					}
					if (i < POPEN_BUFSIZE) {
						cpu_temperature = atoi (buffer + i);
						printf ("CPU temperature is %d C.\n", cpu_temperature);
					} else {
						break;
					}
				}
				pclose (f);
			} else {
				break;
			}

			done = (cpu_temperature < MAX_CPU_TEMP);
			if (!done) {
				$(console, println, "Pausing to let CPU cool down.");
				sleep (10);
			}
		} while (!done);
#endif
	}

	if (outputMode & OUTPUT_MODE_GRAPH) {
		if (!graph)
			error (__FUNCTION__, "Graphing not initialized.");

		$(graph, addLine, name, parameter);
	}

	if (outputMode & OUTPUT_MODE_CSV) {
		if (!csv_output_file) 
			error (__FUNCTION__, "CSV output not initialized.");

		fprintf (csv_output_file, "%s\n", name);
	}
}

void dataEnds (const char *parameter)
{
	require(outputMode) else { error (__FUNCTION__, "Bad output mode."); }
	require(parameter) else { error_null_parameter (__FUNCTION__); }
	//==========

	if (outputMode & OUTPUT_MODE_GRAPH) {
		if (!graph)
			error (__FUNCTION__, "Graphing not initialized.");
		if (!parameter)
			error (__FUNCTION__, "dataEnds: NULL name.");

		$(graph, make);
		MutableImage *image = $(graph, image);
		$(image, writeBMP, parameter);

		$(console, println, "Wrote graph to: bandwidth.bmp");
	}

	if (outputMode & OUTPUT_MODE_CSV) {
		if (!csv_output_file) 
			error (__FUNCTION__, "CSV output not initialized.");
		fclose (csv_output_file);
		$(console, println, "Wrote CSV.");
	}
}

void 
dataAddDatum (long x, long y)
{
	require(outputMode) else { error (__FUNCTION__, "Bad output mode."); }
	//==========

	if (outputMode & OUTPUT_MODE_GRAPH) {
		if (!graph)
			error (__FUNCTION__, "Graphing not initialized.");

		$(graph, addPoint, x, y);
	}

	if (outputMode & OUTPUT_MODE_CSV) {
		if (!csv_output_file) 
			error (__FUNCTION__, "CSV output not initialized.");

		fprintf (csv_output_file, "%lld, %.1Lf\n", (long long)x, (long double)y/10.);
		fflush (csv_output_file);
	}
}

//----------------------------------------------------------------------------
// Name:	usage
//----------------------------------------------------------------------------
void
usage ()
{
	printf ("Usage: bandwidth [--slow] [--fast] [--faster] [--fastest] [--limit] [--title string] [--csv file] [--nice]\n");
	exit (0);
}

//----------------------------------------------------------------------------
// Name:	main
//----------------------------------------------------------------------------
int
mbw (int argc, char *argv[], void *callback_ptr)
{
	// NOTE: There's no need to initialize classes, as this is done lazily in the "new" calls.
   	bool use_sse2 = true;
        bool use_sse4 = true;

	Testing *routines = new(Testing);

	console = new(Console);

	int i, chunk_size;

	usec_per_test = 5000000;	// 5 seconds per memory test.

	outputMode = OUTPUT_MODE_GRAPH;

	--argc;
	++argv;

	char graph_title [512] = {0};

	i = 0;
	while (i < argc) {
		char *s = argv [i++];
		
		if (!strcmp ("--nice", s)) {
			nice_mode = true;
		}
		else if (!strcmp ("--slow", s)) {
			usec_per_test=20000000;	// 20 seconds per test.
		}
		else
		if (!strcmp ("--limit", s)) {
			limit_at_128MB = true;
		}
		else
		if (!strcmp ("--fast", s)) {
			usec_per_test = 1000000; // 1 second per test.
		}
		else
		if (!strcmp ("--faster", s)) {
			usec_per_test = 500000;	// 0.5 seconds per test.
		}
		else
		if (!strcmp ("--fastest", s)) {
			usec_per_test = 50000;	// 0.05 seconds per test.
		}
		else
		if (!strcmp ("--nosse2", s)) {
			use_sse2 = false;
			use_sse4 = false;
		}
		else
		if (!strcmp ("--nosse4", s)) {
			use_sse4 = false;
		}
		else
		if (!strcmp ("--help", s)) {
			usage ();
		}
		else
		if (!strcmp ("--nograph", s)) {
			outputMode &= ~OUTPUT_MODE_GRAPH;
		}
		else
		if (!strcmp ("--csv", s) && i != argc) {
			outputMode |= OUTPUT_MODE_CSV;
			if (i < argc)
				csv_file_path = argv[i++];
			else
				usage ();
		}
		else
		if (!strcmp ("--title", s) && i != argc) {
			snprintf (graph_title, sizeof(graph_title)-1, "%s", argv[i++]);
		}
		else {
			if ('-' == *s)
				usage ();
		}
	}

	for (i = 0; chunk_sizes[i] && i < sizeof(chunk_sizes)/sizeof(int); i++) {
		chunk_sizes_log2[i] = log2 (chunk_sizes[i]);
	}

	$(console, println, "This is bandwidth version " RELEASE);
	$(console, println, "Copyright (C) 2005-2021 by Zack T Smith.");
	$(console, newline);
	$(console, println, "This software is covered by the GNU Public License.");
	$(console, println, "It is provided AS-IS, use at your own risk.");
	$(console, println, "See the file COPYING for more information.");
	$(console, newline);
	$(console, flush);

	CPUCharacteristics *cpu = new(CPUCharacteristics);
	$(cpu, printCharacteristics);
#if defined(x86) && defined(__linux__) 
	$(console, newline);
	$(cpu, printCacheInfo);
#endif
	$(console, newline);

	if (!cpu->has_sse41)
		use_sse4 = false;
	if (!cpu->has_sse2)
		use_sse2 = false;
	routines->use_sse2 = use_sse2;
        routines->use_sse4 = use_sse4;
        routines->use_avx = cpu->has_avx;

	//------------------------------------------------------------
	// Attempt to obtain information about the CPU.
	//
	char *sysInfo = $(cpu, getCPUString);
	if (strlen (sysInfo)) {
		$(console, printf, "System: %s\n", sysInfo);
		strncpy (graph_title, sysInfo, sizeof(graph_title));
	} else {
		strncpy (graph_title, "Memory bandwidth", sizeof(graph_title));
	}

	unsigned long chunk_limit = limit_at_128MB ?  128 << 20 : 1<<31;

	$(console, newline);
	$(console, println, "Notation: B = byte, kB = 1024 B, MB = 1048576 B.");
	$(console, flush);

	dataBegins (graph_title, TITLE_MEMORY_GRAPH);

	//------------------------------------------------------------
	// Sequential non-vector reads.
	//
	$(console, newline);
#ifdef IS_64BIT
	dataBeginSection ( "Sequential 64-bit reads", RGB_BLUE);
#else
	dataBeginSection ( "Sequential 32-bit reads", RGB_BLUE);
#endif

	i = 0;
	while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
		long amount = $(routines, read, chunk_size, NO_SSE2, false);
		dataAddDatum (chunk_size, amount);
	}

#if defined(__arm__) || defined(__aarch64__)
	//------------------------------------------------------------
	// NEON 128 bit sequential reads.
	//
	dataBeginSection ("Sequential 128-bit reads", RGB_RED);

	$(console, newline);

	i = 0;
	while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
		long amount = $(routines, read, chunk_size, NEON_128BIT, false);
		dataAddDatum (chunk_size, amount);
	}
#endif

#ifdef x86
	//------------------------------------------------------------
	// SSE2 sequential reads.
	//
	if (use_sse2) {
		dataBeginSection ("Sequential 128-bit reads", RGB_RED);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			long amount = $(routines, read, chunk_size, SSE2, false);
			dataAddDatum (chunk_size, amount);
		}
	}

	//------------------------------------------------------------
	// AVX sequential reads.
	//
	if (cpu->has_avx) {
		dataBeginSection ( "Sequential 256-bit reads", RGB_TURQUOISE);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			if (!(chunk_size & 128)) {
				long amount = $(routines, read, chunk_size, AVX, false);
				dataAddDatum (chunk_size, amount);
			}
		}
	}

#ifdef IS_64BIT
	//------------------------------------------------------------
	// AVX512 sequential reads (Intel-only).
	//
	if (!cpu->is_amd && cpu->has_avx512_f) {
		dataBeginSection ( "Sequential 512-bit reads (dashed)", RGB_DARKPURPLE | DASHED);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			if (!(chunk_size & 128)) {
				long amount = $(routines, read, chunk_size, AVX512, false);
				dataAddDatum (chunk_size, amount);
			}
		}
	}
#endif
#endif

	//------------------------------------------------------------
	// SSE4 sequential reads that do bypass the caches.
	//
	if (use_sse4) {
		dataBeginSection ( "Sequential 128-bit non-temporal reads", 0xA04040);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			long amount = $(routines, read, chunk_size, SSE2_BYPASS, false);
			dataAddDatum (chunk_size, amount);
		}
	}

// SEQ WRITES
	//------------------------------------------------------------
	// Sequential non-vector writes.
	//
#ifdef IS_64BIT
	dataBeginSection ( "Sequential 64-bit writes", RGB_DARKGREEN);
#else
	dataBeginSection ( "Sequential 32-bit writes", RGB_DARKGREEN);
#endif

	$(console, newline);

	i = 0;
	while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
		if (!(chunk_size & 128)) {
			long amount = $(routines, write, chunk_size, NO_SSE2, false);
			dataAddDatum (chunk_size, amount);
		}
	}

#if defined(__arm__) || defined(__aarch64__)
	//------------------------------------------------------------
	// NEON 128 bit sequential writes.
	//
	dataBeginSection ("Sequential 128-bit writes", 0xA04040);

	$(console, newline);

	i = 0;
	while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
		long amount = $(routines, write, chunk_size, NEON_128BIT, false);
		dataAddDatum (chunk_size, amount);
	}
#endif

	//------------------------------------------------------------
	// SSE2 sequential writes that do not bypass the caches.
	//
	if (use_sse2) {
		dataBeginSection ( "Sequential 128-bit writes", RGB_PURPLE);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			long amount = $(routines, write, chunk_size, SSE2, false);
			dataAddDatum (chunk_size, amount);
		}
	}

	//------------------------------------------------------------
	// AVX sequential writes that do not bypass the caches.
	//
	if (cpu->has_avx) {
		dataBeginSection ( "Sequential 256-bit writes", RGB_PINK);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			if (!(chunk_size & 128)) {
				long amount = $(routines, write, chunk_size, AVX, false);
				dataAddDatum (chunk_size, amount);
			}
		}
	}

#ifdef IS_64BIT
	//------------------------------------------------------------
	// AVX512 sequential writes (Intel-only).
	//
	if (!cpu->is_amd && cpu->has_avx512_f) {
		dataBeginSection ( "Sequential 512-bit writes (dashed)", RGB_TURQUOISE | DASHED);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			if (!(chunk_size & 128)) {
				long amount = $(routines, write, chunk_size, AVX512, false);
				dataAddDatum (chunk_size, amount);
			}
		}
	}
#endif

	//------------------------------------------------------------
	// SSE4 sequential writes that do bypass the caches.
	//
	if (use_sse4) {
		dataBeginSection ( "Sequential 128-bit non-temporal writes", RGB_DARKORANGE);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			long amount = $(routines, write, chunk_size, SSE2_BYPASS, false);
			dataAddDatum (chunk_size, amount);
		}
	}

	//------------------------------------------------------------
	// AVX sequential writes that do bypass the caches.
	//
	if (cpu->has_avx) {
		dataBeginSection ( "Sequential 256-bit non-temporal writes", RGB_DARKOLIVEGREEN);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			if (!(chunk_size & 128)) {
				long amount = $(routines, write, chunk_size, AVX_BYPASS, false);
				dataAddDatum (chunk_size, amount);
			}
		}
	}

//RANDOM READS
	//------------------------------------------------------------
	// Random non-vector reads.
	//
	$(console, newline);
#ifdef IS_64BIT
	dataBeginSection ( "Random 64-bit reads", RGB_DARKCYAN);
#else
	dataBeginSection ( "Random 32-bit reads", RGB_DARKCYAN);
#endif
	srand (time (NULL));
	
	i = 0;
	while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
		if (!(chunk_size & 128)) {
			long amount = $(routines, read, chunk_size, NO_SSE2, true);
			dataAddDatum (chunk_size, amount);
		}
	}

#ifdef x86
	//------------------------------------------------------------
	// SSE2 random reads.
	//
	if (use_sse2) {
		dataBeginSection ( "Random 128-bit reads", RGB_MAROON);

		$(console, newline);
		srand (time (NULL));

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			if (!(chunk_size & 128)) {
				long amount = $(routines, read, chunk_size, SSE2, true);
				dataAddDatum (chunk_size, amount);
			}
		}
	}

	//------------------------------------------------------------
	// AVX random reads.
	//
#ifdef IS_64BIT
	if (cpu->has_avx) {
		dataBeginSection ( "Random 256-bit reads", RGB_BROWN );

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			if (!(chunk_size & 128)) {
				long amount = $(routines, read, chunk_size, AVX, true);
				dataAddDatum (chunk_size, amount);
			}
		}
	}
#endif

#if defined(__arm__) || defined(__aarch64__)
	//------------------------------------------------------------
	// NEON 128 bit random reads.
	//
	dataBeginSection ( "Random 128-bit reads", RGB_MAROON);

	$(console, newline);
	srand (time (NULL));

	i = 0;
	while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
		long amount = $(routines, read, chunk_size, NEON_128BIT, true);
		dataAddDatum (chunk_size, amount);
	}
#endif

	//------------------------------------------------------------
	// SSE4 random reads that bypass the caches.
	//
	if (use_sse4) {
		dataBeginSection ( "Random 128-bit non-temporal reads", 0x301934 /* dark purple */);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			if (!(chunk_size & 128)) {
				long amount = $(routines, read, chunk_size, SSE2_BYPASS, true);
				dataAddDatum (chunk_size, amount);
			}
		}
	}

//RANDOM WRITES
	//------------------------------------------------------------
	// Random non-vector writes.
	//
#ifdef IS_64BIT
	dataBeginSection ( "Random 64-bit writes", RGB_GREEN);
#else
	dataBeginSection ( "Random 32-bit writes", RGB_GREEN);
#endif

	$(console, newline);
	srand (time (NULL));

	i = 0;
	while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
		if (!(chunk_size & 128)) {
			long amount = $(routines, write, chunk_size, NO_SSE2, true);
			dataAddDatum (chunk_size, amount);
		}
	}

#if defined(__arm__) || defined(__aarch64__)
	//------------------------------------------------------------
	// NEON 128 bit random writes.
	//
	dataBeginSection ( "Random 128-bit writes", RGB_NAVYBLUE);

	$(console, newline);
	srand (time (NULL));

	i = 0;
	while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
		long amount = $(routines, write, chunk_size, NEON_128BIT, true);
		dataAddDatum (chunk_size, amount);
	}
#endif

	//------------------------------------------------------------
	// SSE2 random writes that do not bypass the caches.
	//
	if (use_sse2) {
		dataBeginSection ( "Random 128-bit writes", RGB_NAVYBLUE);

		$(console, newline);
		srand (time (NULL));

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			if (!(chunk_size & 128)) {
				long amount = $(routines, write, chunk_size, SSE2, true);
				dataAddDatum (chunk_size, amount);
			}
		}
	}

	//------------------------------------------------------------
	// AVX randomized writes that do not bypass the caches.
	//
#ifdef IS_64BIT
	if (cpu->has_avx) {
		dataBeginSection ( "Random 256-bit writes", RGB_RED);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			if (!(chunk_size & 128)) {
				long amount = $(routines, write, chunk_size, AVX, true);
				dataAddDatum (chunk_size, amount);
			}
		}
	}
#endif

	//------------------------------------------------------------
	// SSE4 random writes that bypass the caches.
	//
	if (use_sse4) {
		dataBeginSection ( "Random 128-bit non-temporal writes", RGB_GOLD);

		$(console, newline);
		srand (time (NULL));

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			if (!(chunk_size & 128)) {
				long amount = $(routines, write, chunk_size, SSE2_BYPASS, true);
				dataAddDatum (chunk_size, amount);
			}
		}
	}

	//------------------------------------------------------------
	// SSE2 sequential copy.
	//
	if (use_sse2) {
		dataBeginSection ( "Sequential 128-bit copy", 0x8f8844);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			long amount = $(routines, copy, chunk_size, SSE2);
			dataAddDatum (chunk_size, amount);
		}
	}

	//------------------------------------------------------------
	// AVX sequential copy.
	//
	if (cpu->has_avx) {
		dataBeginSection ( "Sequential 256-bit copy", RGB_CHARTREUSE);

		$(console, newline);

		i = 0;
		while ((chunk_size = chunk_sizes [i++]) && chunk_size <= chunk_limit) {
			if (!(chunk_size & 128)) {
				long amount = $(routines, copy, chunk_size, AVX);
				dataAddDatum (chunk_size, amount);
			}
		}
	}
#endif

	//------------------------------------------------------------
	// Register to register.
	//
	$(console, newline);
	$(routines, registerToRegisterTest);
	$(routines, vectorToVectorTest128);

	//------------------------------------------------------------
	// Stack to/from register.
	//
	$(routines, stackPush);
	$(routines, stackPop);

	//------------------------------------------------------------
	// Register vs stack.
	//
	$(routines, incrementRegisters);
	$(routines, incrementStack);

	//------------------------------------------------------------
	// C library performance.
	//
	$(console, newline);
	$(routines, memsetTest);
	$(routines, memcpyTest);

	$(console, flush);

	$(console, newline);
	dataEnds ("bandwidth.bmp");

	$(console, newline);
	$(console, println, "Done.");

	release (graph);
	release (routines);
	release (console);
	release (cpu);

	if (g_totalObjectAllocations == g_totalObjectDeallocations) {
		puts ("All objects that were allocated were deallocated.");
	} else {
		puts ("Not all objects that were allocated were deallocated.");
		printf ("%ld\n", (long)g_totalObjectAllocations-(long)g_totalObjectDeallocations);
	}

	deallocateClasses();

	return 0;
}
