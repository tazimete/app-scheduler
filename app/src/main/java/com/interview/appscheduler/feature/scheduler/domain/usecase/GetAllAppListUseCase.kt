package com.interview.appscheduler.feature.scheduler.domain.usecase

import com.interview.appscheduler.core.abstraction.AbstractUseCase
import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.repository.AbstractAppSchedulerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

typealias BaseGetAllAppListUseCase = AbstractUseCase<GetAllAppListReturnType, GetAllAppListParamType>
typealias GetAllAppListReturnType = Flow<Result<Entity<List<AppEntity>>>>
typealias GetAllAppListParamType = Unit

class GetAllAppListUseCase @Inject constructor(
    private val repository: AbstractAppSchedulerRepository
//) : AbstractUseCase<GetAllAppListReturnType, GetAllAppListParamType> {
) : BaseGetAllAppListUseCase {
    override suspend fun invoke(): GetAllAppListReturnType =  repository.getAppList()

    override suspend fun invoke(params: Unit): GetAllAppListReturnType  = TODO("Not yet implemented")
}