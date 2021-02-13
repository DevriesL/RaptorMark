package io.github.devriesl.raptormark.data

const val SEQ_RW_TEST_ID = "seq_rw_test"
const val RAND_RW_TEST_ID = "rand_rw_test"
const val LATENCY_TEST_ID = "latency_test"

object TestItems {
    var testList : List<TestItem> = listOf(
        TestItem(SEQ_RW_TEST_ID, SeqRwNativeTest()),
        TestItem(RAND_RW_TEST_ID, RandRwNativeTest()),
        TestItem(LATENCY_TEST_ID, LatencyNativeTest()),
    )
}

data class TestItem (
    var id : String = "",
    var nativeTest: FIONativeTest
)
