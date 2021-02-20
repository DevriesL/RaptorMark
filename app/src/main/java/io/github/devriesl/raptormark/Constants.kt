package io.github.devriesl.raptormark

object Constants {
    const val TEST_FILE_NAME_SUFFIX = ".tmp"
    const val SEQ_RW_TEST_ID = "seq_rw_test"
    const val RAND_RW_TEST_ID = "rand_rw_test"
    const val LATENCY_TEST_ID = "latency_test"

    const val PRIMARY_PREFERRED_ENGINE = "io_uring"
    const val SECONDARY_PREFERRED_ENGINE = "psync"
}