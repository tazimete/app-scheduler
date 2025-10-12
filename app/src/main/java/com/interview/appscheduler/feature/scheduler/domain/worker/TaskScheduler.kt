package com.interview.appscheduler.feature.scheduler.domain.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.library.DateUtils
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TaskScheduler  @Inject constructor() {
    fun scheduleTask(context: Context, appEntity: AppEntity) {
        var delayDate = DateUtils.getCalenderDate(appEntity.scheduledTime ?: "")

        val inputData = Data.Builder()
            .putString("PACKAGE_NAME", appEntity.packageName)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<AppLauncherWorker>()
            .setInitialDelay(delayDate.day.toLong(), TimeUnit.DAYS)
            .setInitialDelay(delayDate.hours.toLong(), TimeUnit.HOURS)
            .setInitialDelay(delayDate.minutes.toLong(), TimeUnit.MINUTES)
            .setInitialDelay(delayDate.seconds.toLong(), TimeUnit.SECONDS)
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}

class AppLauncherWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        Log.d("MyWorker", "Task executed at: ${System.currentTimeMillis()}")

        val packageName = inputData.getString("PACKAGE_NAME") ?: "com.android.chrome" // Default to Chrome if no package name provided
        launchApp(packageName)

        return Result.success()
    }

    fun launchApp(packageName: String): Boolean {
        return try {
            val intent = this.context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                context.startActivity(intent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private fun sendNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "task_channel",
                "Task Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, "task_channel")
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }
}