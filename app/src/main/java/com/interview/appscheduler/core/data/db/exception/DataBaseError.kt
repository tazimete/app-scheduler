package com.interview.appscheduler.core.data.db.exception

sealed class DatabaseError(val code: Int, override val message: String): Throwable() {
    object ConnectionFailed : DatabaseError(DatabaseErrorCode.CONNECTION_FAILED.code, "Database connection failed")
    object ConstraintViolation : DatabaseError(DatabaseErrorCode.CONNECTION_FAILED.code, "Constraint violation")
    object WriteError : DatabaseError(DatabaseErrorCode.CONNECTION_FAILED.code, "Failed to write record to database")
    object ReadError : DatabaseError(DatabaseErrorCode.CONNECTION_FAILED.code, "Failed to read record to database")
    object UpdateError : DatabaseError(DatabaseErrorCode.CONNECTION_FAILED.code, "Failed to update record to database")
    object DeleteError : DatabaseError(DatabaseErrorCode.CONNECTION_FAILED.code, "Failed to delete record to database")
    object Timeout : DatabaseError(DatabaseErrorCode.CONNECTION_FAILED.code, "Database timeout")
    object NotFound : DatabaseError(DatabaseErrorCode.CONNECTION_FAILED.code, "Record not found")
    object Unknown : DatabaseError(DatabaseErrorCode.CONNECTION_FAILED.code, "Unknown database error")

    data class Custom(val customCode: Int, val details: String) :
        DatabaseError(customCode, details)
}