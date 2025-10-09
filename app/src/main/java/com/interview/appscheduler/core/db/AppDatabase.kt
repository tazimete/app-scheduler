package com.interview.appscheduler.core.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.interview.appscheduler.feature.scheduler.data.entity.AppSchedulerTableEntity
import com.interview.appscheduler.feature.scheduler.data.source.local.service.AppSchedulerDao

@Database(
    entities = [AppSchedulerTableEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appSchedulerDao(): AppSchedulerDao
}