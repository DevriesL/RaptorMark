cmake_minimum_required(VERSION 3.16)

file(GLOB_RECURSE CJSON_SOURCES "*.c")

add_library(cjson STATIC ${CJSON_SOURCES})

target_include_directories(cjson PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})