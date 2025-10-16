package com.interview.appscheduler.core.data.db.exception

enum class DatabaseErrorCode(val code: Int) {
    CONNECTION_FAILED(1001),
    CONSTRAINT_VIOLATION(1002),
    TIMEOUT(1003),
    NOT_FOUND(4004),
    UNKNOWN(4999)
}