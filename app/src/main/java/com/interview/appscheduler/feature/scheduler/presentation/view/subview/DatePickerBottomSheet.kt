package com.interview.appscheduler.feature.scheduler.presentation.view.subview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.interview.appscheduler.library.DateUtils
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerBottomSheet(selectedDate: Date? = null, onSelectDateTime: (Date) -> Unit) {
    val bottomSheetScrollState = rememberScrollState()
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.time ?: Date().time,
        initialDisplayedMonthMillis = selectedDate?.time ?: Date().time,
        yearRange = IntRange(DateUtils.getDeviceLocalDate().year, 2100)
    )
    val timePickerState = rememberTimePickerState(
        initialHour = selectedDate?.hours ?: Date().hours,
        initialMinute = selectedDate?.minutes ?: Date().minutes,
        is24Hour = true
    )

    Box(
        modifier = Modifier
            .verticalScroll(bottomSheetScrollState),
        contentAlignment = Alignment.Center,
    ) {
        Column {
            DatePicker(
                state = datePickerState,
                showModeToggle = true // Shows both calendar and input mode
            )

            Spacer(Modifier.height(30.dp))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(
                    state = timePickerState
                )
            }

            Spacer(Modifier.height(20.dp))

            AddToScheduleButton(
                onClick = {
                    val selectedDateMillis =
                        datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    val hours =
                        if (timePickerState.hour == 0) Date().hours else timePickerState.hour
                    val minutes =
                        if (timePickerState.minute == 0) Date().minutes else timePickerState.minute

                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = selectedDateMillis
                    }

                    calendar.set(Calendar.HOUR_OF_DAY, hours)
                    calendar.set(Calendar.MINUTE, minutes)
                    calendar.set(Calendar.SECOND, 0)
                    calendar.set(Calendar.MILLISECOND, 0)

                    onSelectDateTime(calendar.time)
                }
            )

            Spacer(Modifier.height(100.dp))
        }
    }
}