package com.interview.appscheduler.feature.scheduler.domain.utility

enum class ScheduleActionType {
    ADD, UPDATE, DELETE, NONE;

    companion object {
        fun getStatusFromValue(value: Int): ScheduleActionType {
            return when(value) {
                1 -> ADD
                2 -> UPDATE
                3 -> DELETE
                else -> NONE
            }
        }
    }

    fun getStatusText(): String {
        return when(this) {
            ADD -> "ADD"
            UPDATE -> "UPDATE"
            DELETE -> "DELETE"
            NONE -> "NONE"
        }
    }

    fun getStatusValue(): Int {
        return when(this) {
            ADD -> 1
            UPDATE -> 2
            DELETE -> 3
            NONE -> -1
        }
    }
}