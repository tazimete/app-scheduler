package com.interview.appscheduler.core.domain.exception

enum class DatabaseErrorCode(val code: Int) {
    CONNECTION_FAILED(1001),
    CONSTRAINT_VIOLATION(1002),
    TIMEOUT(1003),
    NOT_FOUND(4004),
    UNKNOWN(4999),
    READ_FAILED(4000),
    WRITE_FAILED(4001),
    UPDATE_FAILED(4002),
    DELETE_FAILED(4003),
    TIME_CONFLICTS(4005),
}