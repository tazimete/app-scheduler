package com.interview.appscheduler.core.di

import com.interview.appscheduler.feature.scheduler.data.repository.AppSchedulerRepository
import com.interview.appscheduler.feature.scheduler.domain.repository.AbstractAppSchedulerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun provideAppSchedulerRepository(appSchedulerRepository: AppSchedulerRepository): AbstractAppSchedulerRepository
}