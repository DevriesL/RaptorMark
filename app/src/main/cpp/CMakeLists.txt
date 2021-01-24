cmake_minimum_required(VERSION 3.10.2)
project("raptormark")

add_library(fio-lib STATIC
        fio-lib/gettime.c fio-lib/ioengines.c fio-lib/init.c fio-lib/stat.c fio-lib/log.c
        fio-lib/time.c fio-lib/filesetup.c fio-lib/eta.c fio-lib/verify.c fio-lib/memory.c
        fio-lib/io_u.c fio-lib/parse.c fio-lib/fio_sem.c fio-lib/rwlock.c fio-lib/pshared.c
        fio-lib/options.c fio-lib/smalloc.c fio-lib/filehash.c fio-lib/profile.c
        fio-lib/engines/cpu.c fio-lib/engines/mmap.c fio-lib/engines/sync.c fio-lib/engines/null.c
        fio-lib/engines/net.c fio-lib/engines/ftruncate.c fio-lib/engines/filecreate.c
        fio-lib/engines/filestat.c fio-lib/server.c fio-lib/client.c fio-lib/iolog.c
        fio-lib/backend.c fio-lib/libfio.c fio-lib/flow.c fio-lib/cconv.c fio-lib/gettime-thread.c
        fio-lib/helpers.c fio-lib/json.c fio-lib/idletime.c fio-lib/td_error.c fio-lib/io_u_queue.c
        fio-lib/filelock.c fio-lib/workqueue.c fio-lib/rate-submit.c fio-lib/optgroup.c
        fio-lib/helper_thread.c fio-lib/steadystate.c fio-lib/zone-dist.c fio-lib/zbd.c
        fio-lib/t/read-to-pipe-async.c)

target_compile_options(fio-lib PUBLIC
        -Wwrite-strings -Wall -Wdeclaration-after-statement
        -D_LARGEFILE_SOURCE -D_FILE_OFFSET_BITS=64 -DFIO_INTERNAL -ffast-math
        -O3 -U_FORTIFY_SOURCE -D_FORTIFY_SOURCE=2)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -std=gnu99")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

add_library(fio-jni SHARED
        fio-jni.cpp)

find_library(log-lib log)

target_link_libraries(fio-jni fio-lib ${log-lib})