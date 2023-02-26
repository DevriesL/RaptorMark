package io.github.devriesl.raptormark.data

sealed class TestResult {
    class FIO(
        val bandwidth: Int,
        val latency: Int
    ) : TestResult()

    class MBW(
        val bandwidth: List<Pair<Int, Int>>,
        val vectorBandwidth: List<Pair<Int, Int>>
    ) : TestResult()
}
