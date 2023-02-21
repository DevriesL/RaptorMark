cmake_minimum_required(VERSION 3.16)

enable_language(ASM)

file(GLOB_RECURSE MBW_SOURCES "*.c" routines-arm-64bit.s)

add_library(mbw SHARED ${MBW_SOURCES})
target_sources(mbw INTERFACE ${MBW_SOURCES})

target_include_directories(mbw PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})