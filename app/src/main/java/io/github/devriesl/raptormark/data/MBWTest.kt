package io.github.devriesl.raptormark.data

import org.json.JSONArray
import org.json.JSONObject

class MBWTest(testCase: TestCases, settingSharedPrefs: SettingSharedPrefs) : BenchmarkTest(
    testCase, settingSharedPrefs
) {
    override fun nativeTest(jsonCommand: String): Int {
        return NativeHandler.native_MBWTest(jsonCommand)
    }

    override fun testOptionsBuilder(): String {
        val root = JSONObject()
        val options = JSONArray()

        root.put("shortopts", false)

        options.put(createOption(testCase.type))

        root.put("options", options)

        return root.toString()
    }

    companion object {
        @JvmStatic
        fun parseResult(result: String): TestResult.MBW {
            val datumList = result.split(System.lineSeparator()).map { line ->
                line.split(",").map { it.trim().toInt() }.let {
                    it.first() to it.last()
                }
            }
            return if (datumList.isNotEmpty() && datumList.all { it.first == 0 }) {
                TestResult.MBW(datumList, emptyList())
            } else {
                val bandwidth = datumList.filterIndexed { index, pair ->
                    datumList.take(index).all { it.first != pair.first }
                }
                val vectorBandwidth = datumList.filterIndexed { index, pair ->
                    datumList.take(index).any { it.first == pair.first }
                }
                TestResult.MBW(bandwidth, vectorBandwidth)
            }
        }
    }
}