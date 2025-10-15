package com.interview.appscheduler.feature.scheduler.data.source.local.service

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RoomDatabase
import com.interview.appscheduler.core.db.BaseDao
import com.interview.appscheduler.core.db.RoomDBEntities
import com.interview.appscheduler.feature.scheduler.data.entity.AppSchedulerTableEntity

@Dao
abstract class AppSchedulerDao(roomDatabase: RoomDatabase) : BaseDao<AppSchedulerTableEntity>(RoomDBEntities.app_scheduler_table, roomDatabase){
    @Query("SELECT * FROM ${RoomDBEntities.app_scheduler_table} WHERE scheduledTime = :scheduledTime LIMIT 1")
    abstract fun getByScheduledTime(scheduledTime: String): AppSchedulerTableEntity?

    @Query("select * from ${RoomDBEntities.app_scheduler_table}")
    abstract fun getAll(): List<AppSchedulerTableEntity>
}