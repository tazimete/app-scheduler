package com.interview.appscheduler.feature.scheduler.data.source.local

import com.interview.appscheduler.core.data.Response
import com.interview.appscheduler.feature.scheduler.data.entity.AppSchedulerTableEntity
import kotlinx.coroutines.flow.Flow

interface AbstractAppSchedulerLocalDataSource {
    suspend fun getAllAppList(): Flow<Result<Response<List<AppSchedulerTableEntity>>>>
}