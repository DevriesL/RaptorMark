package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.Constants.SEQ_WR_TEST_ID
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.di.StringProvider

class SeqWrTestRepo(
    stringProvider: StringProvider,
    settingDataSource: SettingDataSource
) : TestRepository(stringProvider, settingDataSource) {
    override val testFileName = SEQ_WR_TEST_ID

    override fun getTestName(): String {
        return stringProvider.getString(R.string.seq_wr_test_title)
    }

    override fun runTest() {
        TODO("Not yet implemented")
    }
}