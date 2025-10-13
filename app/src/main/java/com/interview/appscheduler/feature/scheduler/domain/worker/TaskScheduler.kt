package com.interview.appscheduler.feature.scheduler.domain.worker

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.library.DateUtils
import java.util.Calendar
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskScheduler  @Inject constructor() {
    fun scheduleTask(context: Context, appEntity: AppEntity, onCompleteSchedule: (OneTimeWorkRequest)-> Unit) {
        val currentDate = Calendar.getInstance()
        val scheduledDate = Calendar.getInstance()
        scheduledDate.time = DateUtils.getCalenderDate(appEntity.scheduledTime ?: "")

        val delayMillis = scheduledDate.timeInMillis - currentDate.timeInMillis
        val delay = if (delayMillis > 0) delayMillis else 0

        val inputData = Data.Builder()
            .putString("PACKAGE_NAME", appEntity.packageName)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<AppLauncherWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)

        onCompleteSchedule(workRequest)
    }

    fun updateTask(context: Context, workId: UUID) {
        WorkManager.getInstance(context).cancelWorkById(workId)
    }

    fun deleteTask(context: Context, workId: UUID) {
        WorkManager.getInstance(context).cancelWorkById(workId)
    }

    fun observeWorkStatus(
        context: Context,
        workId: UUID,
        lifecycleOwner: LifecycleOwner,
        onStatusChanged: (WorkInfo.State) -> Unit = {},
        onComplete: () -> Unit,
        onFailed: () -> Unit
    ) {
        WorkManager.getInstance(context).getWorkInfoByIdLiveData(workId).observe(lifecycleOwner) { workInfo ->
            if (workInfo != null) {
                when (workInfo.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        onComplete()
                    }
                    WorkInfo.State.FAILED -> {
                        onFailed()
                    }

                    else -> {
                        // Other states: ENQUEUED, RUNNING, CANCELLED, BLOCKED
                        onStatusChanged(workInfo.state)
                    }
                }
            }
        }
    }
}