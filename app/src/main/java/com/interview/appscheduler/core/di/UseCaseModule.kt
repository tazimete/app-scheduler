package com.interview.appscheduler.core.di

import com.interview.appscheduler.core.abstraction.AbstractUseCase
import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.usecase.GetAllAppListUseCase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.flow.Flow

@Module
@InstallIn(ViewModelComponent::class)
abstract class UseCaseModule {
    @Binds
    abstract fun provideGetAllAppListUseCase(getAllAppListUseCase: GetAllAppListUseCase): AbstractUseCase<Flow<Result<Entity<kotlin.collections.List<AppEntity>>>>, Unit>
}