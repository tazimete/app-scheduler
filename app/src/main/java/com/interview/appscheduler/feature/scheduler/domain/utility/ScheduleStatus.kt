package com.interview.appscheduler.feature.scheduler.domain.utility

import androidx.compose.ui.graphics.Color

enum class ScheduleStatus {
    SCHEDULED, COMPLETED, FAILED, BLOCKED, CANCELLED;

    companion object {
        fun getStatusFromValue(value: Int): ScheduleStatus {
            return when(value) {
                0 -> SCHEDULED
                1 -> COMPLETED
                2 -> FAILED
                3 -> BLOCKED
                4 -> CANCELLED
                else -> SCHEDULED
            }
        }
    }

    fun getStatusText(): String {
        return when(this) {
            SCHEDULED -> "SCHEDULED"
            COMPLETED -> "COMPLETED"
            FAILED -> "FAILED"
            BLOCKED -> "BLOCKED"
            CANCELLED -> "CANCELLED"
        }
    }

    fun getStatusColor(): Color {
        return when(this) {
            SCHEDULED -> Color(0xFF2196F3) // Blue
            COMPLETED -> Color(0xFF00966E) // Green
            FAILED -> Color(0xFFF44336) // Red
            BLOCKED -> Color(0xFFFFC107) // Amber
            CANCELLED -> Color(0xFF9E9E9E) // Grey
        }
    }

    fun getStatusValue(): Int {
        return when(this) {
            SCHEDULED -> 0
            COMPLETED -> 1
            FAILED -> 2
            BLOCKED -> 3
            CANCELLED -> 4
        }
    }
}