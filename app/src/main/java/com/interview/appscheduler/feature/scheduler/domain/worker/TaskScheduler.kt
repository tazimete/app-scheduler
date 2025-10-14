package com.interview.appscheduler.feature.scheduler.domain.worker

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.WorkManager.UpdateResult
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
        val workRequest = createWorkRequest(appEntity)
//        val result = WorkManager.getInstance(context).enqueue(workRequest)
        WorkManager.getInstance(context).enqueueUniqueWork(
            appEntity.scheduledTime ?: "",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )

        return workRequest
    }

    fun updateScheduleTask(context: Context, appEntity: AppEntity): UpdateResult {
        var workRequest = createWorkRequest(appEntity, true)
        val result = WorkManager.getInstance(context).updateWork(workRequest)

        val updateResult = result.get()

        return updateResult
    }

    fun deleteScheduleTask(context: Context, appEntity: AppEntity): Operation.State {
        val uuid = UUID.fromString(appEntity.taskId)
        val result = WorkManager.getInstance(context).cancelWorkById(uuid)

        val updateResult: Operation.State = result.result.get()

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