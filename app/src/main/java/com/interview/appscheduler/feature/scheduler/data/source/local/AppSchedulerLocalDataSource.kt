package com.interview.appscheduler.feature.scheduler.data.source.local

import com.interview.appscheduler.core.Exception.ErrorEntity
import com.interview.appscheduler.core.data.Response
import com.interview.appscheduler.feature.scheduler.data.entity.AppSchedulerTableEntity
import com.interview.appscheduler.feature.scheduler.data.source.local.service.AppSchedulerDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AppSchedulerLocalDataSource @Inject constructor(
    private val appSchedulerDao: AppSchedulerDao,
) : AbstractAppSchedulerLocalDataSource {
    override suspend fun getAllAppList(): Flow<Result<Response<List<AppSchedulerTableEntity>>>> = flow {
        val data: List<AppSchedulerTableEntity> = appSchedulerDao.getAll()
        if (data.isNotEmpty()) {
            emit(Result.success(Response(isSuccess = true, message = "Get all app scheduler successfully", data = data)))
        } else {
            emit(Result.failure(ErrorEntity.NotFound(404, "No app scheduler found in the database")))
        }
    }.catch { e ->
        val errorEntity = ErrorEntity.DatabaseAccessingError(404, "Failed to access app scheduler in the database")
        emit(Result.failure(errorEntity))
    }
}