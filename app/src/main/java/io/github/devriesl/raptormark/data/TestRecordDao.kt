package io.github.devriesl.raptormark.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TestRecordDao {
    @Query("SELECT * FROM test_records ORDER BY test_date DESC")
    fun getTestRecords(): Flow<List<TestRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestRecord(testRecord: TestRecord): Long

    @Delete
    suspend fun deleteTestRecord(testRecord: TestRecord)
}