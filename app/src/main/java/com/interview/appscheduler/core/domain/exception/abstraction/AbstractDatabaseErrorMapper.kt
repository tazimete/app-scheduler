package com.interview.appscheduler.core.domain.exception.abstraction

import com.interview.appscheduler.core.domain.exception.DatabaseError

interface AbstractDatabaseErrorMapper {
    fun map(throwable: Throwable): DatabaseError
}