cmake_minimum_required(VERSION 3.16)

file(GLOB_RECURSE LIBAIO_SOURCES "*.c")

add_library(libaio STATIC ${LIBAIO_SOURCES})
target_sources(libaio INTERFACE ${LIBAIO_SOURCES})

target_compile_options(libaio PUBLIC -static -fomit-frame-pointer -O2)

target_include_directories(libaio PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})
