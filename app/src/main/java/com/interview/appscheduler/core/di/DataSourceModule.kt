package com.interview.appscheduler.core.di

import com.interview.appscheduler.core.data.db.exception.DatabaseErrorMapper
import com.interview.appscheduler.core.data.db.exception.abstraction.AbstractDatabaseErrorMapper
import com.interview.appscheduler.feature.scheduler.data.source.local.AbstractAppSchedulerLocalDataSource
import com.interview.appscheduler.feature.scheduler.data.source.local.AppSchedulerLocalDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun provideAppSchedulerLocalDataSource(appSchedulerLocalDataSource: AppSchedulerLocalDataSource): AbstractAppSchedulerLocalDataSource

    @Binds
    abstract fun bindDatabaseErrorMapper(databaseErrorMapper: DatabaseErrorMapper): AbstractDatabaseErrorMapper
}