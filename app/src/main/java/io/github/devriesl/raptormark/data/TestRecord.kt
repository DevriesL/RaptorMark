package io.github.devriesl.raptormark.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_records")
data class TestRecord(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "test_date") val timestamp: Long
) {
    @ColumnInfo(name = "mbw_result")
    var mbwResult: String? = null

    @ColumnInfo(name = "seq_rd_result")
    var seqRdResult: String? = null

    @ColumnInfo(name = "seq_wr_result")
    var seqWrResult: String? = null

    @ColumnInfo(name = "rand_rd_result")
    var randRdResult: String? = null

    @ColumnInfo(name = "rand_wr_result")
    var randWrResult: String? = null

    fun getResults(): LinkedHashMap<TestCases, String?> = linkedMapOf(
        Pair(TestCases.MBW, mbwResult),
        Pair(TestCases.SEQ_RD, seqRdResult),
        Pair(TestCases.SEQ_WR, seqWrResult),
        Pair(TestCases.RAND_RD, randRdResult),
        Pair(TestCases.RAND_WR, randWrResult)
    )

    fun setResult(testCase: TestCases, result: String?) {
        when (testCase) {
            TestCases.MBW -> {
                mbwResult = result
            }
            TestCases.SEQ_RD -> {
                seqRdResult = result
            }
            TestCases.SEQ_WR -> {
                seqWrResult = result
            }
            TestCases.RAND_RD -> {
                randRdResult = result
            }
            TestCases.RAND_WR -> {
                randWrResult = result
            }
        }
    }

    constructor() : this(0L, System.currentTimeMillis())
}
