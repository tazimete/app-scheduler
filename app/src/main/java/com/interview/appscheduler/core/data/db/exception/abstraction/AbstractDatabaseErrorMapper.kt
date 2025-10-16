package com.interview.appscheduler.core.data.db.exception.abstraction

import com.interview.appscheduler.core.data.db.exception.DatabaseError

interface AbstractDatabaseErrorMapper {
    fun map(throwable: Throwable): DatabaseError
}