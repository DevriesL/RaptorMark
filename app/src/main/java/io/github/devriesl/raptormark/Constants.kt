package io.github.devriesl.raptormark

object Constants {
    const val NEW_JOB_OPT_NAME = "name"
    const val FILE_PATH_OPT_NAME = "filename"
    const val IO_DEPTH_OPT_NAME = "iodepth"
    const val RUNTIME_OPT_NAME = "runtime"
    const val BLOCK_SIZE_OPT_NAME = "blocksize"
    const val IO_TYPE_OPT_NAME = "readwrite"
    const val IO_SIZE_OPT_NAME = "size"
    const val IO_ENGINE_OPT_NAME = "ioengine"
    const val NUM_THREADS_OPT_NAME = "numjobs"

    const val DEFAULT_IO_DEPTH_VALUE = "8"
    const val DEFAULT_RUNTIME_LIMIT_VALUE = "60"
    const val DEFAULT_SEQ_BLOCK_SIZE_VALUE = "1024k"
    const val DEFAULT_RAND_BLOCK_SIZE_VALUE = "4k"
    const val DEFAULT_IO_SIZE_VALUE = "256m"
    const val DEFAULT_IO_ENGINE_VALUE = "libaio"
    const val DEFAULT_NUM_THREADS_VALUE = "8"

    const val DIRECT_IO_OPT_NAME = "direct"
    const val ETA_PRINT_OPT_NAME = "eta"
    const val OUTPUT_FORMAT_OPT_NAME = "output-format"
    const val CONSTANT_DIRECT_IO_VALUE = "1"
    const val CONSTANT_ETA_PRINT_VALUE = "always"
    const val CONSTANT_OUTPUT_FORMAT_VALUE = "json"

    const val IO_TYPE_SEQ_RD_VALUE = "read"
    const val IO_TYPE_SEQ_WR_VALUE = "write"
    const val IO_TYPE_RAND_RD_VALUE = "randread"
    const val IO_TYPE_RAND_WR_VALUE = "randwrite"
}