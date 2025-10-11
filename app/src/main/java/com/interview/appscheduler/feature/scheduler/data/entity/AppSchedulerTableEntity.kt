package com.interview.appscheduler.feature.scheduler.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.interview.appscheduler.core.db.BaseEntity
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity

@Entity(tableName = "app_scheduler_table")
data class AppSchedulerTableEntity(
    @PrimaryKey(autoGenerate = true)
    override val id: Int,
    val appId: Int? = null,
    val name: String?,
    val packageName: String,
    val versionCode: Long,
    val versionName: String,
    val thumbnail: String? = null,
    val isScheduled: Boolean?,
    val scheduledTime: String? = null,
    val installedTime: String? = null
) : BaseEntity()


fun AppSchedulerTableEntity.toDomainEntity(): AppEntity {
    return AppEntity(
        id = id,
        appId = appId,
        name = name,
        packageName = packageName,
        versionCode = versionCode,
        versionName = versionName,
        thumbnail = thumbnail,
        isScheduled = isScheduled,
        scheduledTime = scheduledTime,
        installedTime = installedTime
    )
}