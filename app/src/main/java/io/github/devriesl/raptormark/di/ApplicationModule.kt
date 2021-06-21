package io.github.devriesl.raptormark.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.devriesl.raptormark.data.HistoryDatabase
import io.github.devriesl.raptormark.data.SettingDataSource
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Singleton
    @Provides
    fun provideResources(@ApplicationContext context: Context): Resources = context.resources

    @Singleton
    @Provides
    fun bindSettingDataSource(@ApplicationContext context: Context): SettingDataSource {
        return SettingDataSource.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideHistoryDatabase(@ApplicationContext context: Context): HistoryDatabase {
        return HistoryDatabase.getInstance(context)
    }
}