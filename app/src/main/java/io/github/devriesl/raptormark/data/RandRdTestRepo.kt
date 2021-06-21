package io.github.devriesl.raptormark.data

import io.github.devriesl.raptormark.Constants.IO_TYPE_RAND_RD_VALUE
import io.github.devriesl.raptormark.Constants.RAND_RD_TEST_ID
import io.github.devriesl.raptormark.R
import io.github.devriesl.raptormark.di.StringProvider

class RandRdTestRepo(
    stringProvider: StringProvider,
    settingDataSource: SettingDataSource,
    historyDatabase: HistoryDatabase
) : TestRepository(stringProvider, settingDataSource, historyDatabase) {
    override val testFileName = RAND_RD_TEST_ID
    override var testTypeValue = IO_TYPE_RAND_RD_VALUE
    override var isRandTest = true

    override fun getTestName(): String {
        return stringProvider.getString(R.string.rand_rd_test_title)
    }

    override fun onTestResult(vararg results: Int) {
        testResult = results[SUM_OF_BW_RESULT_INDEX]
        randLatResult = results[AVG_OF_4N_LAT_RESULT_INDEX]
    }
}