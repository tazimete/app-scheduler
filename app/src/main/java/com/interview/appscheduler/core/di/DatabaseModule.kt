package com.interview.appscheduler.core.di

import android.content.Context
import androidx.room.Room
import com.interview.appscheduler.core.db.AppDatabase
import com.interview.appscheduler.core.db.RoomDBEntities
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Singleton
    @Provides
    fun provideYourDatabase(
        @ApplicationContext app: Context
    ) = Room.databaseBuilder(
        app,
        AppDatabase::class.java,
        RoomDBEntities.DATABASE_NAME
    ).build()

    @Singleton
    @Provides
    fun provideAppSchedulerDao(db: AppDatabase) = db.appSchedulerDao()
}