package com.interview.appscheduler.feature.scheduler.data.repository

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.interview.appscheduler.application.SchedulerApplication
import com.interview.appscheduler.core.Exception.ErrorEntity
import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.feature.scheduler.data.entity.toDomainEntity
import com.interview.appscheduler.feature.scheduler.data.source.local.AbstractAppSchedulerLocalDataSource
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.repository.AbstractAppSchedulerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import androidx.core.graphics.createBitmap

class AppSchedulerRepository @Inject constructor(
    private val localDataSource: AbstractAppSchedulerLocalDataSource
) : AbstractAppSchedulerRepository {
    override suspend fun getScheduledAppList(): Flow<Result<Entity<List<AppEntity>>>> {
        return localDataSource.getScheduledAppList()
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