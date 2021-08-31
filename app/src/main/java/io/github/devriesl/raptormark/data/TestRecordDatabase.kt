package io.github.devriesl.raptormark.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import io.github.devriesl.raptormark.Converters

@Database(entities = [TestRecord::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TestRecordDatabase : RoomDatabase() {
    abstract fun testRecordDao(): TestRecordDao

    companion object {
        @Volatile
        private var INSTANCE: TestRecordDatabase? = null
        private const val DB_NAME = "AppHistoryDb"

        fun getInstance(context: Context): TestRecordDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context, TestRecordDatabase::class.java, DB_NAME)
                .build().also { INSTANCE = it }
        }
    }
}
