package com.interview.appscheduler.asset.string.database

import com.interview.appscheduler.asset.string.abstraction.StringResource

enum class DatabaseStringAssets(override val value: String): StringResource {
    // Error Messages
    CONNECTION_FAILED("Database connection failed"),
    CONSTRAINT_VIOLATION("Constraint violation"),
    READ_FAILED("Failed to read record to database"),
    WRITE_FAILED("Failed to write record to database"),
    UPDATE_FAILED("Failed to update record to database"),
    DELETE_FAILED("Failed to delete record to database"),
    TIME_CONFLICTS("Time conflicts with other app schedule. Please choose a different time."),
    TIMEOUT("Database timeout"),
    NOT_FOUND("Record not found"),
    UNKNOWN("Unknown database error"),
}