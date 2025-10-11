package com.interview.appscheduler.feature.scheduler.domain.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.Date
import java.util.concurrent.TimeUnit

class TaskScheduler(private val context: Context) {
    fun scheduleTaskNow() {
        val workRequest = OneTimeWorkRequestBuilder<AppLauncherWorker>()
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun scheduleTaskWithDelay(delayDate: Date) {
        val workRequest = OneTimeWorkRequestBuilder<AppLauncherWorker>()
            .setInitialDelay(delayDate.day.toLong(), TimeUnit.DAYS)
            .setInitialDelay(delayDate.hours.toLong(), TimeUnit.HOURS)
            .setInitialDelay(delayDate.minutes.toLong(), TimeUnit.MINUTES)
            .setInitialDelay(delayDate.seconds.toLong(), TimeUnit.SECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }

    fun scheduleTaskWithConstraints() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresCharging(false)
            .setRequiresBatteryNotLow(true)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<AppLauncherWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}

class AppLauncherWorker(val context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        Log.d("MyWorker", "Task executed at: ${System.currentTimeMillis()}")

        performScheduledTask()

        return Result.success()
    }

    private fun performScheduledTask() {
        sendNotification("Scheduled Task", "Task executed successfully!")

        val serviceIntent = Intent(applicationContext, MyBackgroundService::class.java)
        applicationContext.startService(serviceIntent)
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