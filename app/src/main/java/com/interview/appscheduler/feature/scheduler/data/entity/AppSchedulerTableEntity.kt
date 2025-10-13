package com.interview.appscheduler.feature.scheduler.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.interview.appscheduler.core.db.BaseEntity
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity

@Entity(tableName = "app_scheduler_table")
data class AppSchedulerTableEntity(
    @PrimaryKey(autoGenerate = true)
    override val id: Long,
    val appId: Int? = null,
    val taskId: String,
    val name: String,
    val packageName: String,
    val versionCode: Long,
    val versionName: String,
    val thumbnail: String? = null,
    val isScheduled: Boolean,
    val scheduledTime: String,
    val installedTime: String,
    val status: Int? = null
) : BaseEntity()

fun AppSchedulerTableEntity.toDomainEntity(): AppEntity {
    return AppEntity(
        id = id,
        appId = appId,
        taskId = taskId,
        name = name,
        packageName = packageName,
        versionCode = versionCode,
        versionName = versionName,
        thumbnail = thumbnail,
        isScheduled = isScheduled,
        scheduledTime = scheduledTime,
        installedTime = installedTime,
        status = status
    )
}

fun AppEntity.toDataEntity(): AppSchedulerTableEntity {
    return AppSchedulerTableEntity(
        id = id ?: 0,
        appId = appId,
        taskId = taskId,
        name = name,
        packageName = packageName,
        versionCode = versionCode ?: 0,
        versionName = versionName ?: "",
        thumbnail = thumbnail,
        isScheduled = isScheduled,
        scheduledTime = scheduledTime ?: "",
        installedTime = installedTime ?: "",
        status = status
    )
}