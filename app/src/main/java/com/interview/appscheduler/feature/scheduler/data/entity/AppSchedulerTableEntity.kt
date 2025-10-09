package com.interview.appscheduler.feature.scheduler.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.interview.appscheduler.core.db.BaseEntity
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity

@Entity(tableName = "app_scheduler_table")
data class AppSchedulerTableEntity(
    @PrimaryKey(autoGenerate = true)
    override val id: Int,
    val appId: Int,
    val code: String,
    val name: String,
    val packageName: String,
    val thumbnail: String,
    val isScheduled: Boolean,
    val scheduledTime: String,
    val installedTime: String
) : BaseEntity()


fun AppSchedulerTableEntity.toDomainEntity(): AppEntity {
    return AppEntity(
        id = id,
        appId = appId,
        code = code,
        name = name,
        packageName = packageName,
        thumbnail = thumbnail,
        isScheduled = isScheduled,
        scheduledTime = scheduledTime,
        installedTime = installedTime
    )
}