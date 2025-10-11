package com.interview.appscheduler.feature.scheduler.domain.repository

import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import kotlinx.coroutines.flow.Flow

interface AbstractAppSchedulerRepository {
    suspend fun getScheduledAppList(): Flow<Result<Entity<List<AppEntity>>>>
    suspend fun getInstalledAppList(): Flow<Result<Entity<List<AppEntity>>>>
}