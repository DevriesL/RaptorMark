package io.github.devriesl.raptormark

object Constants {
    const val TEST_FILE_NAME_SUFFIX = ".tmp"
    const val SEQ_RD_TEST_ID = "seq_rd_test"
    const val SEQ_WR_TEST_ID = "seq_wr_test"
    const val RAND_RD_TEST_ID = "rand_rd_test"
    const val RAND_WR_TEST_ID = "rand_wr_test"
    const val LATENCY_TEST_ID = "latency_test"

    const val PRIMARY_PREFERRED_ENGINE = "io_uring"
    const val SECONDARY_PREFERRED_ENGINE = "psync"
}