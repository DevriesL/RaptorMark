package io.github.devriesl.raptormark

object Constants {
    const val TEST_FILE_NAME_SUFFIX = ".tmp"
    const val SEQ_RD_TEST_ID = "seq_rd_test"
    const val SEQ_WR_TEST_ID = "seq_wr_test"
    const val RAND_RD_TEST_ID = "rand_rd_test"
    const val RAND_WR_TEST_ID = "rand_wr_test"
    const val LATENCY_TEST_ID = "latency_test"

    const val NEW_JOB_OPT_NAME = "name"
    const val FILE_PATH_OPT_NAME = "filename"
    const val IO_DEPTH_OPT_NAME = "iodepth"
    const val RUNTIME_OPT_NAME = "runtime"
    const val BLOCK_SIZE_OPT_NAME = "blocksize"
    const val IO_TYPE_OPT_NAME = "readwrite"
    const val DIRECT_IO_OPT_NAME = "direct"
    const val IO_SIZE_OPT_NAME = "size"
    const val IO_ENGINE_OPT_NAME = "ioengine"

    const val DEFAULT_IO_DEPTH_VALUE = "8"
    const val DEFAULT_RUNTIME_LIMIT = "60"
    const val DEFAULT_BLOCK_SIZE_VALUE = "4k"
    const val DEFAULT_IO_SIZE_VALUE = "4g"
    const val DIRECT_IO_CONSTANT_VALUE = "1"
    const val PRIMARY_PREFERRED_ENGINE = "io_uring"
    const val SECONDARY_PREFERRED_ENGINE = "psync"

    const val IO_TYPE_SEQ_RD_VALUE = "read"
    const val IO_TYPE_SEQ_WR_VALUE = "write"
    const val IO_TYPE_RAND_RD_VALUE = "randread"
    const val IO_TYPE_RAND_WR_VALUE = "randwrite"
}