package com.interview.appscheduler.feature.scheduler.data.repository

import android.content.pm.PackageManager
import com.interview.appscheduler.application.SchedulerApplication
import com.interview.appscheduler.core.Exception.ErrorEntity
import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.feature.scheduler.data.entity.toDataEntity
import com.interview.appscheduler.feature.scheduler.data.entity.toDomainEntity
import com.interview.appscheduler.feature.scheduler.data.source.local.AbstractAppSchedulerLocalDataSource
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.repository.AbstractAppSchedulerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppSchedulerRepository @Inject constructor(
    private val localDataSource: AbstractAppSchedulerLocalDataSource
) : AbstractAppSchedulerRepository {
    override suspend fun createAppSchedule(item: AppEntity): Flow<Result<Entity<Long>>> {
        return localDataSource.createAppSchedule(item.toDataEntity())
            .map { result ->
                result.map { data ->
                    Entity<Long>(
                        isSuccess = data.isSuccess,
                        message = data.message,
                        data = data.data,
                    )
                }
            }
    }

    override suspend fun updateAppSchedule(item: AppEntity): Flow<Result<Entity<Int>>> {
        return localDataSource.updateAppSchedule(item.toDataEntity())
            .map { result ->
                result.map { data ->
                    Entity<Int>(
                        isSuccess = data.isSuccess,
                        message = data.message,
                        data = data.data,
                    )
                }
            }
    }

    override suspend fun deleteAppSchedule(item: AppEntity): Flow<Result<Entity<Int>>> {
        return localDataSource.deleteAppSchedule(item.toDataEntity())
            .map { result ->
                result.map { data ->
                    Entity<Int>(
                        isSuccess = data.isSuccess,
                        message = data.message,
                        data = data.data,
                    )
                }
            }
    }

    override suspend fun getScheduledAppList(): Flow<Result<Entity<List<AppEntity>>>> {
        val packageManager = SchedulerApplication.getApplicationContext().packageManager
        return localDataSource.getScheduledAppList()
            .map { result ->
                result.map { data ->
                    Entity<List<AppEntity>>(
                        isSuccess = data.isSuccess,
                        message = data.message,
                        data = data.data?.map {
                            it.toDomainEntity().copy(icon = packageManager.getApplicationIcon(it.packageName))
                        } ?: emptyList(),
                    )
                }
            }
    }

    override suspend fun getInstalledAppList(): Flow<Result<Entity<List<AppEntity>>>> = flow {
            val packageManager = SchedulerApplication.getApplicationContext().packageManager
            val appEntities = mutableListOf<AppEntity>()

             val flags = PackageManager.GET_META_DATA or
                PackageManager.GET_PERMISSIONS or
                PackageManager.GET_ACTIVITIES
            val packages = packageManager.getInstalledApplications(flags)

            for (applicationInfo in packages) {
                val appName = packageManager.getApplicationLabel(applicationInfo).toString()
                val packageInfo = packageManager.getPackageInfo(applicationInfo.packageName, 0)

                val appEntity = AppEntity(
                    name = appName,
                    packageName = applicationInfo.packageName,
                    icon = packageManager.getApplicationIcon(applicationInfo.packageName),
                    versionName = packageInfo.versionName,
                    versionCode = packageInfo.longVersionCode,
                    isSystemApp = applicationInfo.flags != 0,
                    isScheduled = false
                )

                appEntities.add(appEntity)
            }

            emit(Result.success(Entity(isSuccess = true, message = "Get all installed app scheduler successfully", data = appEntities.toList())))
        }.catch { e ->
            val errorEntity = ErrorEntity.DatabaseAccessingError(404, "Failed to load installed app scheduler from os")
            emit(Result.failure(errorEntity))
        }
}