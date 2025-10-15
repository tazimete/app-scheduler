package com.interview.appscheduler.feature.scheduler.domain.utility

import androidx.compose.ui.graphics.Color

enum class ScheduleStatus {
    SCHEDULED, RUNNING, COMPLETED, RESCHEDULED, FAILED, BLOCKED, CANCELLED;

    companion object {
        fun getStatusFromValue(value: Int): ScheduleStatus {
            return when(value) {
                0 -> SCHEDULED
                1 -> RUNNING
                2 -> COMPLETED
                3 -> RESCHEDULED
                4 -> FAILED
                5 -> BLOCKED
                6 -> CANCELLED
                else -> SCHEDULED
            }
        }
    }

    fun getStatusText(): String {
        return when(this) {
            SCHEDULED -> "SCHEDULED"
            RUNNING -> "RUNNING"
            COMPLETED -> "COMPLETED"
            RESCHEDULED -> "RESCHEDULED"
            FAILED -> "FAILED"
            BLOCKED -> "BLOCKED"
            CANCELLED -> "CANCELLED"
        }
    }

    fun getStatusColor(): Color {
        return when(this) {
            SCHEDULED -> Color(0xFF2196F3) // Blue
            RUNNING -> Color(0xFFDD4D01) // Orange
            COMPLETED -> Color(0xFF00966E) // Green
            RESCHEDULED -> Color(0xFF2196F3) // Green
            FAILED -> Color.Red // Red
            BLOCKED -> Color(0xFFFFC107) // Amber
            CANCELLED -> Color(0xFF9E9E9E) // Grey
        }
    }

    fun getStatusValue(): Int {
        return when(this) {
            SCHEDULED -> 0
            RUNNING -> 1
            COMPLETED -> 2
            RESCHEDULED -> 3
            FAILED -> 4
            BLOCKED -> 5
            CANCELLED -> 6
        }
    }
}