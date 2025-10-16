package com.interview.appscheduler.core.domain.exception

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabaseLockedException
import android.database.sqlite.SQLiteException
import com.interview.appscheduler.core.domain.exception.abstraction.AbstractDatabaseErrorMapper
import java.sql.SQLException
import javax.inject.Inject

class DatabaseErrorMapper @Inject constructor() : AbstractDatabaseErrorMapper {
    override fun map(throwable: Throwable): DatabaseError {
        return when (throwable) {
            is SQLiteConstraintException ->
                DatabaseError.ConstraintViolation

            is SQLiteDatabaseLockedException ->
                DatabaseError.Timeout

            is SQLiteException -> when {
                throwable.message?.contains("no such table", true) == true ->
                    DatabaseError.NotFound
                throwable.message?.contains("locked", true) == true ->
                    DatabaseError.Timeout
                else ->
                    DatabaseError.ConnectionFailed
            }

            is SQLException ->
                DatabaseError.ConnectionFailed

            is NoSuchElementException ->
                DatabaseError.NotFound

            else ->
                DatabaseError.Unknown
        }
    }
}