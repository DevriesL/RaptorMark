package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.Constants.IO_TYPE_SEQ_RD_VALUE
import io.github.devriesl.raptormark.Constants.SEQ_RD_TEST_ID
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.di.StringProvider

class SeqRdTestRepo(
    stringProvider: StringProvider,
    settingDataSource: SettingDataSource
) : TestRepository(stringProvider, settingDataSource) {
    override val testFileName = SEQ_RD_TEST_ID
    override var testTypeValue = IO_TYPE_SEQ_RD_VALUE

    override fun getTestName(): String {
        return stringProvider.getString(R.string.seq_rd_test_title)
    }
}