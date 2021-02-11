cmake_minimum_required(VERSION 3.16)

file(GLOB_RECURSE FIO_SOURCES "*.c")

add_library(fio STATIC ${FIO_SOURCES})

target_include_directories(fio PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})

target_compile_options(fio PUBLIC
        -Wwrite-strings -Wall -Wdeclaration-after-statement
        -D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64 -DFIO_INTERNAL -ffast-math
        -O3 -U_FORTIFY_SOURCE -D_FORTIFY_SOURCE=2
        -D_GNU_SOURCE -DBITS_PER_LONG=64)

target_precompile_headers(fio PUBLIC fio-config.h)