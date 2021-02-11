cmake_minimum_required(VERSION 3.16)
project("raptormark")

add_subdirectory(cjson)
add_subdirectory(fio)

add_library(fio-jni SHARED fio-jni.cpp)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=gnu99")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

find_library(log-lib log)

target_link_libraries(fio-jni fio cjson ${log-lib})