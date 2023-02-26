package io.github.devriesl.raptormark.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "test_records")
data class TestRecord(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "test_date") val timestamp: Long
) {
    @ColumnInfo(name = "results")
    var results: Map<TestCases, String> = mapOf()

    fun setResult(testCase: TestCases, result: String) {
        results = results + mapOf(testCase to result)
    }

    constructor() : this(0L, System.currentTimeMillis())
}
