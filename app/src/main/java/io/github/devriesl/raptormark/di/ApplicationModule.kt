package io.github.devriesl.raptormark.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.devriesl.raptormark.Constants.GITHUB_URL
import io.github.devriesl.raptormark.data.SettingSharedPrefs
import io.github.devriesl.raptormark.data.TestRecordDatabase
import io.github.devriesl.raptormark.data.network.GitHubService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Singleton
    @Provides
    fun provideGitHubService(): GitHubService = Retrofit.Builder()
        .baseUrl(GITHUB_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GitHubService::class.java)
}
