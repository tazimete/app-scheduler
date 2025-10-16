package com.interview.appscheduler.feature.scheduler.data.source.local

import com.interview.appscheduler.asset.string.scheduleddapp.ScheduledAppStringAssets
import com.interview.appscheduler.core.data.Response
import com.interview.appscheduler.core.domain.exception.DatabaseError
import com.interview.appscheduler.core.domain.exception.abstraction.AbstractDatabaseErrorMapper
import com.interview.appscheduler.feature.scheduler.data.entity.AppSchedulerTableEntity
import com.interview.appscheduler.feature.scheduler.data.source.local.service.AppSchedulerDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AppSchedulerLocalDataSource @Inject constructor(
    private val appSchedulerDao: AppSchedulerDao,
    private val databaseErrorMapper: AbstractDatabaseErrorMapper
) : AbstractAppSchedulerLocalDataSource {

    override suspend fun getAppSchedule(scheduledTime: String): Flow<Result<Response<AppSchedulerTableEntity>>> = flow {
        val app = appSchedulerDao.getByScheduledTime(scheduledTime)

        emit(
            Result.success(
                Response(
                    isSuccess = true,
                    message = ScheduledAppStringAssets.GET_SCHEDULED_APP.value,
                    data = app
                )
            )
        )
    }.catch { e ->
        emit(
            Result.failure(
                databaseErrorMapper.map(e)
            )
        )
    }

    override suspend fun createAppSchedule(item: AppSchedulerTableEntity): Flow<Result<Response<Long>>> = flow {
        val data = appSchedulerDao.insert(item)
        if ( data > 0) {
            emit(
                Result.success(
                    Response(
                        isSuccess = true,
                        message = ScheduledAppStringAssets.CREATE_APP_SCHEDULE.value,
                        data = data
                    )
                )
            )
        } else {
            emit(
                Result.failure(DatabaseError.WriteError)
            )
        }
    }.catch { e ->
        emit(
            Result.failure(
                databaseErrorMapper.map(e)
            )
        )
    }

    override suspend fun updateAppSchedule(item: AppSchedulerTableEntity): Flow<Result<Response<Int>>> = flow {
        val data = appSchedulerDao.update(item)
        if ( data > 0) {
            emit(
                Result.success(
                    Response(
                        isSuccess = true,
                        message = ScheduledAppStringAssets.UPDATE_APP_SCHEDULE.value, data = data
                    )
                )
            )
        } else {
            emit(
                Result.failure(DatabaseError.UpdateError)
            )
        }
    }.catch { e ->
        emit(
            Result.failure(
                databaseErrorMapper.map(e)
            )
        )
    }

    override suspend fun deleteAppSchedule(item: AppSchedulerTableEntity): Flow<Result<Response<Int>>> = flow {
        val data = appSchedulerDao.delete(item)
        if ( data > 0) {
            emit(
                Result.success(
                    Response(
                        isSuccess = true,
                        message = ScheduledAppStringAssets.DELETE_APP_SCHEDULE.value,
                        data = data
                    )
                )
            )
        } else {
            emit(
                Result.failure(DatabaseError.DeleteError)
            )
        }
    }.catch { e ->
        emit(
            Result.failure(
                databaseErrorMapper.map(e)
            )
        )
    }

    override suspend fun getScheduledAppList(): Flow<Result<Response<List<AppSchedulerTableEntity>>>> = flow {
        val data: List<AppSchedulerTableEntity> = appSchedulerDao.getAll()
        if (data.isNotEmpty()) {
            emit(
                Result.success(
                    Response(
                        isSuccess = true,
                        message = ScheduledAppStringAssets.GET_ALL_APP_SCHEDULES.value,
                        data = data
                    )
                )
            )
        } else {
            emit(
                Result.failure(DatabaseError.ReadError)
            )
        }
    }.catch { e ->
        emit(
            Result.failure(
                databaseErrorMapper.map(e)
            )
        )
    }
}