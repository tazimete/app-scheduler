package com.interview.appscheduler.core.data.db.exception

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.interview.appscheduler.core.data.db.exception.abstraction.AbstractDatabaseErrorMapper
import java.sql.SQLException
import javax.inject.Inject

class DatabaseErrorMapper @Inject constructor() : AbstractDatabaseErrorMapper {
    override fun map(throwable: Throwable): DatabaseError {
        return when (throwable) {
            is SQLiteConstraintException -> DatabaseError.ConstraintViolation
            is SQLiteException -> {
                if (throwable.message?.contains("timeout", ignoreCase = true) == true) {
                    DatabaseError.Timeout
                } else {
                    DatabaseError.ConnectionFailed
                }
            }
            is SQLException -> DatabaseError.ConnectionFailed
            is NoSuchElementException -> DatabaseError.NotFound
            else -> DatabaseError.Unknown
        }
    }
}