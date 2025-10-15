package com.interview.appscheduler.feature.scheduler.domain.worker

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkQuery
import com.interview.appscheduler.core.worker.DispatcherProvider
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.library.DateUtils
import kotlinx.coroutines.flow.flowOn
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskScheduler  @Inject constructor(
    val dispatcherProvider: DispatcherProvider
) {
    private fun createWorkRequest(
        appEntity: AppEntity,
        isUpdate: Boolean = false
    ): OneTimeWorkRequest {
        val currentDate = Calendar.getInstance()
        val scheduledDate = Calendar.getInstance()
        scheduledDate.time = DateUtils.getCalenderDate(appEntity.scheduledTime ?: "")

        val delayMillis = scheduledDate.timeInMillis - currentDate.timeInMillis
        val delay = if (delayMillis > 0) delayMillis else 0

        val inputData = Data.Builder()
            .putString("PACKAGE_NAME", appEntity.packageName)
            .build()

        var workRequest = OneTimeWorkRequestBuilder<AppLauncherWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        if(isUpdate) {
            workRequest = OneTimeWorkRequestBuilder<AppLauncherWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setId(UUID.fromString(appEntity.taskId))
                .setInputData(inputData)
                .build()
        }

        return workRequest
    }

    fun addScheduleTask(context: Context, appEntity: AppEntity): OneTimeWorkRequest {
        val workRequest = createWorkRequest(appEntity, false)
        WorkManager.getInstance(context).enqueue(workRequest)
        return workRequest
    }

    fun updateScheduleTask(context: Context, appEntity: AppEntity): OneTimeWorkRequest {
        WorkManager.getInstance(context).cancelWorkById(UUID.fromString(appEntity.taskId))
        var workRequest = createWorkRequest(appEntity, false)
//        WorkManager.getInstance(context).updateWork(workRequest)
        WorkManager.getInstance(context).enqueue(workRequest)

        return workRequest
    }

    fun deleteScheduleTask(context: Context, appEntity: AppEntity): UUID {
        val uuid = UUID.fromString(appEntity.taskId)
        WorkManager.getInstance(context).cancelWorkById(uuid)

        return uuid
    }

    suspend fun observeWorksStatus(
        context: Context,
        workIds: List<UUID>,
        onStatusChanged: (List<WorkInfo>) -> Unit
    ) {
        val workQuery = WorkQuery.fromIds(workIds)
        WorkManager.getInstance(context).getWorkInfosFlow(workQuery)
            .flowOn(dispatcherProvider.main)
            .collect { workInfoList ->
                onStatusChanged(workInfoList)
            }
    }
}