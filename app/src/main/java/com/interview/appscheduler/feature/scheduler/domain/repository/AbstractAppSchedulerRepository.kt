package com.interview.appscheduler.feature.scheduler.domain.repository

import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import kotlinx.coroutines.flow.Flow

interface AbstractAppSchedulerRepository {
    suspend fun getAppList(): Flow<Result<Entity<List<AppEntity>>>>
}