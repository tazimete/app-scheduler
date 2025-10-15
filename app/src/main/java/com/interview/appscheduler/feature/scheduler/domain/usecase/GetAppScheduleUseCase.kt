package com.interview.appscheduler.feature.scheduler.domain.usecase

import com.interview.appscheduler.core.abstraction.AbstractUseCase
import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.repository.AbstractAppSchedulerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAppScheduleUseCase @Inject constructor(
    private val repository: AbstractAppSchedulerRepository
) : AbstractUseCase<Flow<Result<Entity<AppEntity>>>, String> {
    override suspend fun invoke(): Flow<Result<Entity<AppEntity>>> = TODO("Not yet implemented")

    override suspend fun invoke(params: String): Flow<Result<Entity<AppEntity>>> = repository.getAppSchedule(params)
}