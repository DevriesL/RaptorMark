cmake_minimum_required(VERSION 3.16)
project("raptormark")

file(GLOB_RECURSE FIO_LIB_SOURCES fio-lib "*.c")

add_library(fio-lib STATIC ${FIO_LIB_SOURCES})

target_include_directories(fio-lib PUBLIC fio-lib)

target_compile_options(fio-lib PUBLIC
        -Wwrite-strings -Wall -Wdeclaration-after-statement
        -D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64 -DFIO_INTERNAL -ffast-math
        -O3 -U_FORTIFY_SOURCE -D_FORTIFY_SOURCE=2
        -D_GNU_SOURCE -DBITS_PER_LONG=64)

target_precompile_headers(fio-lib PUBLIC fio-config.h)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=gnu99")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

add_library(fio-jni SHARED
        fio-jni.cpp)

find_library(log-lib log)

target_link_libraries(fio-jni fio-lib ${log-lib})