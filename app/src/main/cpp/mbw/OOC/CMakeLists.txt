cmake_minimum_required(VERSION 3.16)

file(GLOB_RECURSE OOC_SOURCES "*.c")

add_library(libooc STATIC ${OOC_SOURCES})
target_sources(libooc INTERFACE ${OOC_SOURCES})

target_include_directories(libooc PUBLIC ${CMAKE_CURRENT_SOURCE_DIR})

target_link_libraries(libooc log)