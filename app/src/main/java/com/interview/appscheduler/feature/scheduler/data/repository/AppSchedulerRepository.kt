package com.interview.appscheduler.feature.scheduler.data.repository

import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.feature.scheduler.data.entity.toDomainEntity
import com.interview.appscheduler.feature.scheduler.data.source.local.AbstractAppSchedulerLocalDataSource
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.repository.AbstractAppSchedulerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppSchedulerRepository @Inject constructor(
    private val localDataSource: AbstractAppSchedulerLocalDataSource
) : AbstractAppSchedulerRepository {
    override suspend fun getAppList(): Flow<Result<Entity<List<AppEntity>>>> {
        return localDataSource.getAllAppList()
            .map { result ->
                result.map { data ->
                    Entity<List<AppEntity>>(
                        isSuccess = data.isSuccess,
                        message = data.message,
                        data = data.data?.map { it.toDomainEntity() } ?: emptyList(),
                    )
                }
            }
    }
}