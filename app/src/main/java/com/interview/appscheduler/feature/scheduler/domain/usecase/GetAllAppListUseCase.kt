package com.interview.appscheduler.feature.scheduler.domain.usecase

import com.interview.appscheduler.core.abstraction.AbstractUseCase
import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.repository.AbstractAppSchedulerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllAppListUseCase @Inject constructor(
    private val repository: AbstractAppSchedulerRepository
) : AbstractUseCase<Flow<Result<Entity<List<AppEntity>>>>, Unit> {
    override suspend operator fun invoke(): Flow<Result<Entity<List<AppEntity>>>> =  repository.getAppList()

    override suspend operator fun invoke(params: Unit): Flow<Result<Entity<List<AppEntity>>>>  = TODO("Not yet implemented")
}