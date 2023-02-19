package io.github.devriesl.raptormark.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.devriesl.raptormark.Converters

@Database(entities = [TestRecord::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class TestRecordDatabase : RoomDatabase() {
    abstract fun testRecordDao(): TestRecordDao

    companion object {
        @Volatile
        private var INSTANCE: TestRecordDatabase? = null
        private const val DB_NAME = "AppHistoryDb"

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE IF EXISTS test_records")
            }
        }

        fun getInstance(context: Context): TestRecordDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context, TestRecordDatabase::class.java, DB_NAME)
                .addMigrations(MIGRATION_1_2)
                .build().also { INSTANCE = it }
        }
    }
}
