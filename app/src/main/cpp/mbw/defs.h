/*============================================================================
  bandwidth, a benchmark to estimate memory transfer bandwidth.
  Copyright (C) 2005-2022 by Zack T Smith.

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

//---------------------------------------------------------------------------
// Change log
// 0.18	Grand unified release supports x86/intel64/arm, Linux/Win32/WinMobile.
// 0.19	Now have 128-bit writer that goes to cache AND one that bypasses.
// 0.20	Added my bmplib and graphing of output. Also added --slow option.
// 0.21	Adds random testing. Min chunk size = 256 B. Allows non-2^n chunks.
// 0.22	Adds register-to-register and register-to/from-stack transfers.
// 0.23	Adds vector-to-vector and register-to-vector transfers, & Mac support.
// 0.24	Adds network bandwidth tests from this PC to specified others.
// 0.25	Made network tests bidirectional to test asymmetric networks.
// 0.26	Fixes to prevent certain vector instructions being used w/AMD chips.
// 0.27 Added 128-byte tests for greater precision.
// 0.28	Added use of CPUID.
// 0.29 Added more 128-byte tests.
// 0.30 Adds cache identification for Intel CPUs in 64-bit mode.
// 0.31 Adds cache identification for Intel CPUs in 32-bit mode.
// 0.32 Added AVX support.
// 1.0	Moved graphing logic into BMPGraphing. Added string-instruction test.
// 1.1	Switched to larger font in graphing module.
// 1.2	Re-added ARM 32 support.
// 1.3	Added CSV output support. Added 32-bit Raspberry π 3 support.
// 1.4	Added 256-bit routines RandomReaderAVX, RandomWriterAVX.
// 1.4.1 Added --limit parameter.
// 1.4.2 Fixed compiler warnings.
// 1.5 Fixed AVX writer bug that gave inaccurate results. Added nice mode.
// 1.5.1 Fixed crashing bug.
// 1.5.2 Disabled AVX tests if AMD CPU.
// 1.6	Converted the code to object-oriented C.
// 1.6.1 Moved CPUID calls into utility*asm. Detection of SHA, SGX, CET.
// 1.6.2 Converted some code to Object-Oriented C.
// 1.6.3 Removed string instruction tests. Improved Object-Oriented C.
// 1.6.4 Code cleanup.
// 1.6.5 Fixed Linux issues.
// 1.7 Separated object-oriented C (OOC) from bandwidth.
// 1.8 OO improvements. Makefile improvements. Added Win64 support.
// 1.9 OO improvements. Win64. Removed Linux fbdev.
// 1.10 ARM64 support and improved ARM32. Apple M1 support. AVX512 detection.
// 1.11	AVX-512 support. PK fonts.
// 1.11.2 Fixes for Windows 64-bit Cygwin.
//---------------------------------------------------------------------------

#ifndef _DEFS_H
#define _DEFS_H

#define RELEASE "1.11.2d"

#define require(COND) if(COND){}

#endif

