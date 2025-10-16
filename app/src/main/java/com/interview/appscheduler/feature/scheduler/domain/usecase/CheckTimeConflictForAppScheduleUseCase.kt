package com.interview.appscheduler.feature.scheduler.domain.usecase

import com.interview.appscheduler.core.abstraction.AbstractUseCase
import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.feature.scheduler.domain.repository.AbstractAppSchedulerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CheckTimeConflictForAppScheduleUseCase @Inject constructor(
    private val repository: AbstractAppSchedulerRepository
) : AbstractUseCase<Flow<Result<Entity<Boolean>>>, String> {
    override suspend fun invoke(): Flow<Result<Entity<Boolean>>> = TODO("Not yet implemented")

    override suspend fun invoke(params: String): Flow<Result<Entity<Boolean>>> {
        return repository.getAppSchedule(params)
            .map { result ->
                result.map { data ->
                    Entity<Boolean>(
                        isSuccess = data.isSuccess,
                        message = data.message,
                        data = (data.data != null),
                    )
                }
            }
    }
}