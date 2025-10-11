package com.interview.appscheduler.feature.scheduler.domain.usecase

import com.interview.appscheduler.core.abstraction.AbstractUseCase
import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.repository.AbstractAppSchedulerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateAppScheduleUseCase @Inject constructor(
    private val repository: AbstractAppSchedulerRepository
) : AbstractUseCase<Flow<Result<Entity<Long>>>, AppEntity> {
    override suspend fun invoke(): Flow<Result<Entity<Long>>> = TODO("Not yet implemented")

    override suspend fun invoke(params: AppEntity): Flow<Result<Entity<Long>>> = repository.createAppSchedule(params)
}