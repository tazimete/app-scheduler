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

    override suspend fun getAppSchedule(scheduledTime: String): Flow<Result<Response<AppSchedulerTableEntity>>> = flow {
        val app = appSchedulerDao.getByScheduledTime(scheduledTime)

        emit(Result.success(Response(isSuccess = true, message = "Get app schedule successfully", data = app)))
    }.catch { e ->
        val errorEntity = ErrorEntity.DatabaseAccessingError(404, "Failed to get app schedule")
        emit(Result.failure(errorEntity))
    }

    override suspend fun createAppSchedule(item: AppSchedulerTableEntity): Flow<Result<Response<Long>>> = flow {
        val data = appSchedulerDao.insert(item)
        if ( data > 0) {
            emit(Result.success(Response(isSuccess = true, message = "Created app schedule successfully", data = data)))
        } else {
            emit(Result.failure(ErrorEntity.NotFound(404, "Failed to create app schedule")))
        }
    }.catch { e ->
        val errorEntity = ErrorEntity.DatabaseAccessingError(404, "Failed to create app schedule")
        emit(Result.failure(errorEntity))
    }

    override suspend fun updateAppSchedule(item: AppSchedulerTableEntity): Flow<Result<Response<Int>>> = flow {
        val data = appSchedulerDao.update(item)
        if ( data > 0) {
            emit(Result.success(Response(isSuccess = true, message = "Updated app schedule successfully", data = data)))
        } else {
            emit(Result.failure(ErrorEntity.NotFound(404, "Failed to update app schedule")))
        }
    }.catch { e ->
        val errorEntity = ErrorEntity.DatabaseAccessingError(404, "Failed to update app schedule")
        emit(Result.failure(errorEntity))
    }

    override suspend fun deleteAppSchedule(item: AppSchedulerTableEntity): Flow<Result<Response<Int>>> = flow {
        val data = appSchedulerDao.delete(item)
        if ( data > 0) {
            emit(Result.success(Response(isSuccess = true, message = "Deleted app schedule successfully", data = data)))
        } else {
            emit(Result.failure(ErrorEntity.NotFound(404, "Failed to delete app schedule")))
        }
    }.catch { e ->
        val errorEntity = ErrorEntity.DatabaseAccessingError(404, "Failed to delete app schedule")
        emit(Result.failure(errorEntity))
    }

    override suspend fun getScheduledAppList(): Flow<Result<Response<List<AppSchedulerTableEntity>>>> = flow {
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