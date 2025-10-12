package com.interview.appscheduler.library

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DateUtils {
    companion object {
        const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val TIME_FORMAT = "HH:mm:ss"
        const val TIME_FORMAT_SHORT = "HH:mm:ss"

        fun getCalendarDateToString(date: Date): String {
            val calendar = Calendar.getInstance().apply {
                set(date.year, date.month, date.day, date.hours, date.minutes, date.seconds)
            }

            val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
            return sdf.format(calendar.time)
        }

        fun getCalenderDate(date: String): Date {
            val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
            return sdf.parse(date)
        }
    }
}