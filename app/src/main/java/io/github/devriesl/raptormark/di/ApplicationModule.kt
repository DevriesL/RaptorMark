package io.github.devriesl.raptormark.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.devriesl.raptormark.data.SettingSharedPrefs
import io.github.devriesl.raptormark.data.TestRecordDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun bindSettingSharedPrefs(@ApplicationContext context: Context): SettingSharedPrefs {
        return SettingSharedPrefs.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideTestRecordDatabase(@ApplicationContext context: Context): TestRecordDatabase {
        return TestRecordDatabase.getInstance(context)
    }

    @Provides
    fun provideTestRecordDao(testRecordDatabase: TestRecordDatabase) =
        testRecordDatabase.testRecordDao()
}
