package com.interview.appscheduler.core.domain.exception

import com.interview.appscheduler.asset.string.database.DatabaseStringAssets

sealed class DatabaseError(val code: Int, override val message: String): Throwable() {
    object ConnectionFailed : DatabaseError(DatabaseErrorCode.CONNECTION_FAILED.code, DatabaseStringAssets.CONNECTION_FAILED.value)
    object ConstraintViolation : DatabaseError(DatabaseErrorCode.CONSTRAINT_VIOLATION.code, DatabaseStringAssets.CONSTRAINT_VIOLATION.value)
    object ReadError : DatabaseError(DatabaseErrorCode.READ_FAILED.code, DatabaseStringAssets.READ_FAILED.value)
    object WriteError : DatabaseError(DatabaseErrorCode.WRITE_FAILED.code, DatabaseStringAssets.WRITE_FAILED.value)
    object UpdateError : DatabaseError(DatabaseErrorCode.WRITE_FAILED.code, DatabaseStringAssets.UPDATE_FAILED.value)
    object DeleteError : DatabaseError(DatabaseErrorCode.DELETE_FAILED.code, DatabaseStringAssets.DELETE_FAILED.value)
    object TimeConflicts : DatabaseError(DatabaseErrorCode.TIME_CONFLICTS.code, DatabaseStringAssets.TIME_CONFLICTS.value)
    object Timeout : DatabaseError(DatabaseErrorCode.TIMEOUT.code, DatabaseStringAssets.TIMEOUT.value)
    object NotFound : DatabaseError(DatabaseErrorCode.CONNECTION_FAILED.code, DatabaseStringAssets.NOT_FOUND.value)
    object Unknown : DatabaseError(DatabaseErrorCode.UNKNOWN.code, DatabaseStringAssets.UNKNOWN.value)


    data class Custom(val customCode: Int, val details: String) :
        DatabaseError(customCode, details)
}