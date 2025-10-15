package com.interview.appscheduler.library

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DateUtils {
    companion object {
        const val DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
        const val DATE_TIME_READABLE_FORMAT = "yyyy, dd MMM 'at' HH:mm"

        fun getDeviceLocalDate(): Date {
            return Calendar.getInstance().time
        }

        fun getCalendarDateToString(date: Date): String {
            val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
            return sdf.format(date)
        }

        fun getCalenderDate(date: String): Date {
            val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
            return sdf.parse(date)
        }

        fun getReadableStringFromDate(date: String): String {
            val sdf = SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault())
            sdf.timeZone = TimeZone.getDefault()
            val simpleDateFormat = SimpleDateFormat(DATE_TIME_READABLE_FORMAT, Locale.getDefault())
            return simpleDateFormat.format(sdf.parse(date))
        }
    }
}