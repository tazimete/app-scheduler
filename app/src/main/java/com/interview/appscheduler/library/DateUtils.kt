package com.interview.appscheduler.library

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateUtils {
    companion object {
        const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"

        fun getCalendarDateToString(date: Date): String {
            val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
            return sdf.format(date)
        }

        fun getCalenderDate(date: String): Date {
            val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
            return sdf.parse(date)
        }
    }
}