package io.github.devriesl.raptormark.data

import androidx.annotation.StringRes
import io.github.devriesl.raptormark.Constants.IO_TYPE_RAND_RD_VALUE
import io.github.devriesl.raptormark.Constants.IO_TYPE_RAND_WR_VALUE
import io.github.devriesl.raptormark.Constants.IO_TYPE_SEQ_RD_VALUE
import io.github.devriesl.raptormark.Constants.IO_TYPE_SEQ_WR_VALUE
import io.github.devriesl.raptormark.R

enum class TestCases(
    @StringRes val title: Int,
    val type: String,
    val isRand: Boolean
) {
    SEQ_RD(R.string.seq_rd_test_title, IO_TYPE_SEQ_RD_VALUE, false),
    SEQ_WR(R.string.seq_wr_test_title, IO_TYPE_SEQ_WR_VALUE, false),
    RAND_RD(R.string.rand_rd_test_title, IO_TYPE_RAND_RD_VALUE, true),
    RAND_WR(R.string.rand_wr_test_title, IO_TYPE_RAND_WR_VALUE, true)
}
