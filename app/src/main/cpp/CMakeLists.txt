cmake_minimum_required(VERSION 3.16)
project("raptormark")

add_subdirectory(cjson)
add_subdirectory(libaio)
add_subdirectory(fio)
add_subdirectory(mbw)

add_library(fio-jni SHARED fio-jni.cpp helper.cpp)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=gnu99")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

target_link_libraries(fio-jni cjson log)
