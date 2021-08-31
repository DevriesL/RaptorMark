package io.github.devriesl.raptormark.data

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TestRecordRepo @Inject constructor(private val testRecordDao: TestRecordDao) {
    val testRecords: Flow<List<TestRecord>> get() = testRecordDao.getTestRecords()

    suspend fun insertTestRecord(testRecord: TestRecord): Long {
        return testRecordDao.insertTestRecord(testRecord)
    }

    suspend fun deleteTestRecord(testRecord: TestRecord) {
        testRecordDao.deleteTestRecord(testRecord)
    }
}
