package io.github.devriesl.raptormark.data

const val SEQ_RW_TEST_ID = "seq_rw_test"
const val RAND_RW_TEST_ID = "rand_rw_test"
const val LATENCY_TEST_ID = "latency_test"

object TestItems {
    var testList : List<TestItem> = listOf(
        TestItem(SEQ_RW_TEST_ID, SeqRwTestJNI()),
        TestItem(RAND_RW_TEST_ID, RandRwTestJNI()),
        TestItem(LATENCY_TEST_ID, LatencyTestJNI()),
    )
}

data class TestItem (
    var id : String = "",
    var jni: TestBaseJNI
)
