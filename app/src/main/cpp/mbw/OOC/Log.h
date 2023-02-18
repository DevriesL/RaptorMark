/*============================================================================
  Log, a simple logging facility.
  Copyright (C) 2018, 2021 by Zack T Smith.

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

#ifndef _LOG_H
#define _LOG_H

#include <stdbool.h>

#include "Graphics.h"

extern bool Log_reopen (const char *path);
extern void Log_close();

extern void Log_print (const char *message);
extern void Log_println (const char *message);

extern void Log_error (const char *funcName, const char *message);
extern void Log_error_int (const char *funcName, const char *message, int value);
extern void Log_warning (const char *funcName, const char *message);
extern void Log_warning_name (const char *funcName, const char *message, const char *name);
extern void Log_warning_int (const char *funcName, const char *message, int value);
extern void Log_info (const char *funcName, const char *message);
extern void Log_info_int (const char *funcName, const char *message, int value);
extern void Log_debug (const char *funcName, const char *message);
extern void Log_debug_name (const char *funcName, const char *message, const char *name);
extern void Log_debug_string (const char *funcName, const char *message, const char *string);
extern void Log_debug_int (const char *funcName, const char *message, int value);
extern void Log_debug_size (const char *funcName, const char *message, Size size);
extern void Log_debug_point (const char *funcName, const char *message, Point point);
extern void Log_debug_rect (const char *funcName, const char *message, Rect rect);
extern void Log_debug_rgb (const char *funcName, const char *message, RGBA color);
extern void Log_errorNullParameter (const char *funcName);
extern void Log_perror (const char *funcName);

#endif

