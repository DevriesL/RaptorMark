/*============================================================================
  ObjectOriented, a facility for object-oriented programming on top of C.
  Copyright (C) 2019 by Zack T Smith.

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

#ifndef _OOC_H
#define _OOC_H

#define OOC_RELEASE "0.9"

// Change history
// 0.1 Basic idea refined.
// 0.2 Added Object, MutableImage, SimpleGraphing.
// 0.3 Added MutableArray and Array.
// 0.4 Added MutableString, String, Double, Int.
// 0.5 Added object type checking, class struct validation. 
// 0.6 Overhauled SimpleGraphing.
// 0.7 Retain/release.
// 0.8 Added BMP 8-bit output.
// 0.9 Added Image. More method-setting macros.

extern void* ooc_bzero(const void *start, size_t length);
extern unsigned long ooc_strlen(const char *start);
extern void ooc_strncpy(const char *dest, const char *src, size_t length);

#define STRINGIFY(a) "\""#a"\""

#ifdef DEBUG
#define nullObjectPointerError (fprintf(stderr, "NULL OBJECT POINTER for %s IN %s\n",STRINGIFY(METHOD),__FUNCTION__) & 0)
#define nullMethodPointerError (fprintf(stderr, "NULL METHOD POINTER for %s IN %s\n",STRINGIFY(METHOD),__FUNCTION__) & 0)
#define nullClassStructError (fprintf(stderr, "MISSING IS_A POINTER for %s IN %s\n",STRINGIFY(METHOD),__FUNCTION__) & 0)
#endif

#define warning(FUNC,MESSAGE) { fprintf(stderr, "Warning (%s): %s\n",FUNC, MESSAGE); }
#define error(FUNC,MESSAGE) { fprintf(stderr, "Error (%s): %s\n",FUNC, MESSAGE); exit(1); }
#define error_null_parameter(FUNC) { fprintf(stderr, "Error (%s): NULL parameter\n",FUNC); exit(1); }
#define error_bad_isa(FUNC) { fprintf(stderr, "Error (%s): Incorrect is_a pointer\n",FUNC); exit(1); }
#define error_double_release(FUNC) { fprintf(stderr, "Error (%s): Detected doulbe release\n",FUNC); exit(1); }

#define CONCAT_SYMBOLS(a,b) a##b
#define CONCAT_SYMBOLS3(a,b,c) a##b##c

extern void deallocateClasses ();
extern void registerClass (void*);

#define PREPARE_CLASS_STRUCT(CLASS,PARENTCLASS) \
  CONCAT_SYMBOLS3(_,CLASS,Class)=(CONCAT_SYMBOLS(CLASS,Class)*) calloc(sizeof(CONCAT_SYMBOLS(CLASS,Class)),1);\
  ooc_bzero(CONCAT_SYMBOLS3(_,CLASS,Class), sizeof(CONCAT_SYMBOLS(CLASS,Class))); \
  CONCAT_SYMBOLS3(_,CLASS,Class)->destroy = CONCAT_SYMBOLS(CLASS,_destroy); \
  CONCAT_SYMBOLS3(_,CLASS,Class)->className = STRINGIFY(CLASS); \
  registerClass (CONCAT_SYMBOLS3(_,CLASS,Class)); 

#ifdef DEBUG
#define $(OBJ,METHOD,...) \
 (OBJ? \
    (OBJ->is_a ? \
        (OBJ->is_a->METHOD ? 	\
            (OBJ->is_a->METHOD(OBJ, ##__VA_ARGS__) \
	    ) \
            : (typeof((OBJ->is_a->METHOD(OBJ, ##__VA_ARGS__))))0  nullMethodPointerError \
         ) \
         : nullClassStructError \
     ) \
     : nullObjectPointerError)
#else 
#define $(OBJ,METHOD,...) (\
		OBJ? \
		OBJ->is_a->METHOD(OBJ, ##__VA_ARGS__)\
            : (typeof((OBJ->is_a->METHOD(OBJ, ##__VA_ARGS__))))0 \
		)
#endif

#define VALIDATE_CLASS_STRUCT(CLASS_PTR) {\
		void **p = (void**) CLASS_PTR; \
		int n = sizeof(*CLASS_PTR)/sizeof(void*); \
		int bad = 0; \
		for (int i=2; i < n; i++) { \
			if (NULL == p[i]) { \
				bad++; \
				fprintf (stderr, "%s: Class struct has NULL method pointer at index %d.\n", __FUNCTION__, i); \
			} \
		} \
		if (bad) { fprintf (stderr, "%s: A total of %d method pointer(s) are NULL\n", __FUNCTION__, bad); exit (-1); } \
	}

// NOTE: Retain count is initially 0 i.e. not yet owned.
#ifdef GRATUITOUS_DEBUGGING
	#define allocate(CLASS) ((CLASS*)(printf("Allocating %s object\n",STRINGIFY(CLASS)), g_totalObjectAllocations++, calloc (sizeof(CLASS),1) ))
#else
	#define allocate(CLASS) ((CLASS*)(g_totalObjectAllocations++, calloc (sizeof(CLASS),1) ))
#endif

#define new(CLASS) ((CLASS*) CONCAT_SYMBOLS(CLASS,_init) (allocate(CLASS)))

// #define delete(OBJ) ((OBJ && OBJ->is_a && OBJ->is_a->destroy)?OBJ->is_a->destroy(OBJ):0, OBJ?free((void*)OBJ):0)

#define clearObjectSelf ooc_bzero(self, sizeof(*self));
// #define clearObjectSelf  

static inline void* retain (void* ptr) {
	if (ptr) {
		typedef struct obj_lite {
			void *is_a;
			int16_t retainCount;
		} LiteObj;
		LiteObj *obj = (LiteObj*) ptr;
		obj->retainCount++;
		return ptr;
	}
	else {
		return NULL;
	}
}

#define release(OBJ) { if (OBJ) { (OBJ)->retainCount--; if ((OBJ)->retainCount <= 0) { (OBJ)->is_a->destroy(OBJ); free(OBJ); g_totalObjectDeallocations++; OBJ = NULL; }}}

#define verifyCorrectClass(THIS,CLASS) \
 		if (!THIS || !THIS->is_a || THIS->is_a != CONCAT_SYMBOLS3(_,CLASS,Class)) {\
                	error_bad_isa (__FUNCTION__);\
		}

#define verifyCorrectClasses(THIS,CLASS,SUBCLASS) \
 		if (!THIS->is_a || (THIS->is_a != (void*)CONCAT_SYMBOLS3(_,CLASS,Class) && THIS->is_a != (void*)CONCAT_SYMBOLS3(_,SUBCLASS,Class))) {\
                	error_bad_isa (__FUNCTION__);\
		}

#define SET_METHOD_POINTER(CLASS,NAME) _##CLASS##Class->NAME = CLASS##_##NAME;

#define SET_OVERRIDDEN_METHOD_POINTER(CLASS,NAME) _##CLASS##Class->NAME = CLASS##_##NAME;

#define SET_INHERITED_METHOD_POINTER(CLASS,PARENTCLASS,NAME) _##CLASS##Class->NAME = (void*)PARENTCLASS##_##NAME;

#ifdef GRATUITOUS_DEBUGGING
	#define DEBUG_DESTROY puts(__FUNCTION__);fflush(0)
#else
	#define DEBUG_DESTROY 
#endif

#endif

