cmake_minimum_required(VERSION 3.10.2)
project("raptormark")

add_library(fio-lib STATIC
        fio-lib/t/read-to-pipe-async.c)

add_library(fio-jni SHARED
        fio-jni.cpp)

find_library(log-lib log)

target_link_libraries(fio-jni fio-lib ${log-lib})