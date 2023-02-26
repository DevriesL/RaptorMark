package io.github.devriesl.raptormark.data

class MBWTest(testCase: TestCases, settingSharedPrefs: SettingSharedPrefs) : BenchmarkTest(
    testCase, settingSharedPrefs
) {
    override fun nativeTest(jsonCommand: String): Int {
        return NativeHandler.native_MBWTest(jsonCommand)
    }

    override fun testOptionsBuilder(): String {
        return ""
    }

    companion object {
        @JvmStatic
        fun parseResult(result: String): TestResult.MBW {
            return TestResult.MBW()
        }
    }
}