package io.github.devriesl.raptormark.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.OffsetDateTime

@Entity(tableName = "test_records")
data class TestRecord(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long,
    @ColumnInfo(name = "test_date") val testDate: OffsetDateTime,
    @ColumnInfo(name = "test_options") val testOptions: String,
    @ColumnInfo(name = "seq_rd_result") val seqRdResult: String?,
    @ColumnInfo(name = "seq_wr_result") val seqWrResult: String?,
    @ColumnInfo(name = "rand_rd_result") val randRdResult: String?,
    @ColumnInfo(name = "rand_wr_result") val randWrResult: String?
)