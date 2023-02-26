package io.github.devriesl.raptormark.data

import androidx.annotation.StringRes
import io.github.devriesl.raptormark.Constants.IO_TYPE_RAND_RD_VALUE
import io.github.devriesl.raptormark.Constants.IO_TYPE_RAND_WR_VALUE
import io.github.devriesl.raptormark.Constants.IO_TYPE_SEQ_RD_VALUE
import io.github.devriesl.raptormark.Constants.IO_TYPE_SEQ_WR_VALUE
import io.github.devriesl.raptormark.Constants.MBW_APP_PERF_VALUE
import io.github.devriesl.raptormark.R
import kotlinx.serialization.Serializable

@Serializable
enum class TestCases(
    @StringRes val title: Int,
    val type: String
) {
    MBW_SEQ_RD(R.string.seq_rd_test_title, IO_TYPE_SEQ_RD_VALUE),
    MBW_SEQ_WR(R.string.seq_wr_test_title, IO_TYPE_SEQ_WR_VALUE),
    MBW_RAND_RD(R.string.rand_rd_test_title, IO_TYPE_RAND_RD_VALUE),
    MBW_RAND_WR(R.string.rand_wr_test_title, IO_TYPE_RAND_WR_VALUE),
    MBW_APP_PERF(R.string.app_perf_title, MBW_APP_PERF_VALUE),
    FIO_SEQ_RD(R.string.seq_rd_test_title, IO_TYPE_SEQ_RD_VALUE),
    FIO_SEQ_WR(R.string.seq_wr_test_title, IO_TYPE_SEQ_WR_VALUE),
    FIO_RAND_RD(R.string.rand_rd_test_title, IO_TYPE_RAND_RD_VALUE),
    FIO_RAND_WR(R.string.rand_wr_test_title, IO_TYPE_RAND_WR_VALUE)
}

fun TestCases.isMBW(): Boolean = this in listOf(
    TestCases.MBW_SEQ_RD,
    TestCases.MBW_SEQ_WR,
    TestCases.MBW_RAND_RD,
    TestCases.MBW_RAND_WR,
    TestCases.MBW_APP_PERF
)

fun TestCases.isFIO(): Boolean = this in listOf(
    TestCases.FIO_SEQ_RD,
    TestCases.FIO_SEQ_WR,
    TestCases.FIO_RAND_RD,
    TestCases.FIO_RAND_WR
)

fun TestCases.isFIORand(): Boolean = this in listOf(TestCases.FIO_RAND_RD, TestCases.FIO_RAND_WR)

fun TestCases.isMBWApp(): Boolean = this in listOf(TestCases.MBW_APP_PERF)

