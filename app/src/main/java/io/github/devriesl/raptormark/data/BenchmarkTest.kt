package io.github.devriesl.raptormark.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONObject
import java.io.IOException
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.declaredFunctions

abstract class BenchmarkTest(
    val testCase: TestCases,
    val settingSharedPrefs: SettingSharedPrefs
) {
    private val nativeListener = object : NativeListener {
        override fun onTestResult(result: String) {
            nativeResult = listOfNotNull(nativeResult, result).joinToString(System.lineSeparator())
            this@BenchmarkTest::class.companionObject?.declaredFunctions?.find {
                it.name == parseResultMethodName
            }?.let { parseResultMethod ->
                testResult = parseResultMethod.call(
                    this@BenchmarkTest::class.companionObjectInstance,
                    nativeResult
                ) as? TestResult
            }
        }
    }

    var testResult: TestResult? by mutableStateOf(null)
        private set

    var nativeResult: String? = null

    abstract fun nativeTest(jsonCommand: String): Int

    abstract fun testOptionsBuilder(): String

    open fun runTest(): String? {
        nativeResult = null

        NativeHandler.registerListener(nativeListener)
        val options = testOptionsBuilder()
        val ret = nativeTest(options)
        NativeHandler.unregisterListener(nativeListener)

        if (ret != 0) {
            throw IOException("$ret")
        }

        return nativeResult
    }

    fun createOption(name: String, value: String? = null): JSONObject {
        val jsonOption = JSONObject()
        jsonOption.put("name", name)
        jsonOption.put("value", value)
        return jsonOption
    }

    companion object {
        const val parseResultMethodName = "parseResult"
    }
}
