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

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <sys/errno.h>

#ifdef ANDROID
#include <android/log.h>
#include <errno.h>
#define LOG_TAG "ObjectOrientedC"
#endif

#include "Log.h"

#define kMaxLineLength (2048)

static FILE *_outputFile = NULL;

void Log_close()
{
#ifndef ANDROID
	if (_outputFile != NULL)
		fclose (_outputFile);
	_outputFile = NULL;
#endif
}

bool Log_reopen (const char *path)
{
#ifndef ANDROID
	if (!path)
		return false;
	FILE *f = fopen (path, "ab");
	if (!f) {
		perror ("fopen");
		return false;
	}
	Log_close ();
	_outputFile = f;
#endif
	return true;
}

void Log_print (const char *message)
{
	if (!message)
		return;
#ifndef ANDROID
	FILE *file = _outputFile == NULL ? stderr : _outputFile;
	fprintf (file, "%s", message);
#else
	__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "%s", message);
#endif
}

void Log_println (const char *message)
{
	if (!message)
		return;
#ifndef ANDROID
	FILE *file = _outputFile == NULL ? stderr : _outputFile;
	fprintf (file, "%s\n", message);
#else
	__android_log_print(ANDROID_LOG_VERBOSE, LOG_TAG, "%s", message);
#endif
}

void Log_error (const char *funcName, const char *message)
{
#ifndef ANDROID
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "Error(%s): %s", funcName, message);
	Log_println (line);
	fflush (NULL);
	exit (-1);
#else
	__android_log_print(ANDROID_LOG_ERROR, LOG_TAG, "%s", message);
#endif
}

void Log_error_int (const char *funcName, const char *message, int value)
{
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "%s = %d", message, value);
	Log_error (funcName, line);
}

void Log_errorNullParameter (const char *funcName)
{
	Log_error (funcName, "Null parameter.");
}

void Log_perror (const char *funcName)
{
	char *message = strerror(errno);
	Log_error (funcName, message ?: "(C library error)");
}

void Log_warning (const char *funcName, const char *message)
{
#ifndef ANDROID
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "Warning(%s): %s", funcName, message);
	Log_println (line);
	fflush (NULL);
#else
	__android_log_print(ANDROID_LOG_WARN, LOG_TAG, "%s", message);
#endif
}

void Log_warning_int (const char *funcName, const char *message, int value)
{
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "%s = %d", message, value);
	Log_warning (funcName, line);
}

void Log_warning_name (const char *funcName, const char *message, const char *name)
{
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "%s \"%s\"", message, name ?: "");
	Log_warning (funcName, line);
}

void Log_info (const char *funcName, const char *message)
{
#ifndef ANDROID
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "Info(%s): %s", funcName, message);
	Log_println (line);
	fflush (NULL);
#else
	__android_log_print(ANDROID_LOG_INFO, LOG_TAG, "%s", message);
#endif
}

void Log_info_int (const char *funcName, const char *message, int value)
{
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "%s = %d", message, value);
	Log_info (funcName, line);
}

void Log_debug (const char *funcName, const char *message)
{
#ifndef ANDROID
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "Debug(%s): %s", funcName, message);
	Log_println (line);
	fflush (NULL);
#else
	__android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, "%s", message);
#endif
}

void Log_debug_string (const char *funcName, const char *message, const char *string)
{
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "%s = %s", message, string);
	Log_debug (funcName, line);
}

void Log_debug_name (const char *funcName, const char *message, const char *name)
{
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "%s \"%s\"", message, name);
	Log_debug (funcName, line);
}

void Log_debug_int (const char *funcName, const char *message, int value)
{
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "%s = %d", message, value);
	Log_debug (funcName, line);
}

void Log_debug_size (const char *funcName, const char *message, Size size)
{
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "%s = (%dx%d)", message, size.width, size.height);
	Log_debug (funcName, line);
}

void Log_debug_point (const char *funcName, const char *message, Point point)
{
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "%s = (%d,%d)", message, point.x , point.y);
	Log_debug (funcName, line);
}

void Log_debug_rgb (const char *funcName, const char *message, RGBA color)
{
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "%s = #%08lx", message, (unsigned long)color);
	Log_debug (funcName, line);
}

void Log_debug_rect (const char *funcName, const char *message, Rect rect)
{
	char line [kMaxLineLength];
	snprintf (line, sizeof(line)-1, "%s = (%d,%d %dx%d)", message, rect.origin.x, rect.origin.y, rect.size.width, rect.size.height);
	Log_debug (funcName, line);
}

