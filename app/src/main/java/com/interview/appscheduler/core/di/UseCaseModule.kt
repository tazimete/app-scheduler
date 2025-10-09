package com.interview.appscheduler.core.di

import com.interview.appscheduler.feature.scheduler.domain.usecase.BaseGetAllAppListUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.GetAllAppListUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {
    @Binds
    abstract fun provideGetAllAppListUseCase(getAllAppListUseCase: GetAllAppListUseCase): BaseGetAllAppListUseCase
}