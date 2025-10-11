package com.interview.appscheduler.core.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.sqlite.db.SimpleSQLiteQuery

abstract class BaseDao<T : BaseEntity>(
    val tableName: String,
    private val roomDatabase: RoomDatabase
) {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    abstract fun insert(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    abstract fun insertWithReplace(entity: T): Long

    @Insert(onConflict = OnConflictStrategy.Companion.IGNORE)
    abstract fun insert(entities: List<T>): LongArray

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    abstract fun insertWithReplace(entities: List<T>): LongArray

    @Update
    abstract fun update(entity: T): Int

    @Update
    abstract fun update(entities: List<T>): Int

    @Delete
    abstract fun delete(entity: T): Int

    @Delete
    abstract fun delete(entities: List<T>): Int

    @RawQuery
    protected abstract fun deleteAll(query: SupportSQLiteQuery): Int

    fun deleteAll(): Int {
        val query = SimpleSQLiteQuery("DELETE FROM $tableName")
        return deleteAll(query)
    }


    fun dropDatabase() {
        roomDatabase.clearAllTables()
    }
}
