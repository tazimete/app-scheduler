package com.interview.appscheduler.feature.scheduler.domain.worker

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkManager.UpdateResult
import androidx.work.await
import com.google.common.util.concurrent.ListenableFuture
import com.interview.appscheduler.core.worker.DefaultDispatcherProvider
import com.interview.appscheduler.core.worker.DispatcherProvider
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.library.DateUtils
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskScheduler  @Inject constructor(
    val dispatcherProvider: DispatcherProvider
) {
    private fun createWorkRequest(
        context: Context,
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

    suspend fun addScheduleTask(context: Context, appEntity: AppEntity): OneTimeWorkRequest? {
        val workRequest = createWorkRequest(context, appEntity)
        val result = WorkManager.getInstance(context).enqueue(workRequest)

        val updateResult: Operation.State = withContext(dispatcherProvider.io) {
            result.await()
        }

        return workRequest
    }

    suspend fun updateScheduleTask(context: Context, appEntity: AppEntity): OneTimeWorkRequest? {
        var workRequest = createWorkRequest(context, appEntity, true)
        val result = WorkManager.getInstance(context).updateWork(workRequest)

        val updateResult = withContext(dispatcherProvider.io) {
            result.await()
        }

        return workRequest
    }

    suspend fun deleteScheduleTask(context: Context, appEntity: AppEntity): Operation.State {
        val result = WorkManager.getInstance(context).cancelWorkById(UUID.fromString(appEntity.taskId))

        val updateResult: Operation.State = withContext(dispatcherProvider.io) {
            result.await()
        }

        return updateResult
    }

    suspend fun observeWorkStatus(
        context: Context,
        workId: UUID,
        onStatusChanged: (WorkInfo.State) -> Unit
    ) {
        WorkManager.getInstance(context).getWorkInfoByIdFlow(workId)
            .flowOn(dispatcherProvider.main)
            .collect { result ->
                onStatusChanged(result.state)
            }
    }
}