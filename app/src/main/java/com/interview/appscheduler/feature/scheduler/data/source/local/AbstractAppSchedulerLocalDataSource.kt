package com.interview.appscheduler.feature.scheduler.data.source.local

import com.interview.appscheduler.core.data.Response
import com.interview.appscheduler.feature.scheduler.data.entity.AppSchedulerTableEntity
import kotlinx.coroutines.flow.Flow

interface AbstractAppSchedulerLocalDataSource {
    suspend fun getAppSchedule(scheduledTime: String): Flow<Result<Response<AppSchedulerTableEntity>>>
    suspend fun createAppSchedule(item: AppSchedulerTableEntity): Flow<Result<Response<Long>>>
    suspend fun updateAppSchedule(item: AppSchedulerTableEntity): Flow<Result<Response<Int>>>
    suspend fun deleteAppSchedule(item: AppSchedulerTableEntity): Flow<Result<Response<Int>>>

    suspend fun getScheduledAppList(): Flow<Result<Response<List<AppSchedulerTableEntity>>>>
}