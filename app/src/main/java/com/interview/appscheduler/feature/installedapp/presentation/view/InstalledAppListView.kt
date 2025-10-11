@file:OptIn(ExperimentalMaterial3Api::class)

package com.interview.appscheduler.feature.installedapp.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.interview.appscheduler.component.NoDataView
import com.interview.appscheduler.feature.scheduler.domain.coordinator.ScheduledAppListCoordinator
import com.interview.appscheduler.feature.scheduler.presentation.view.subview.AppItemView
import com.interview.appscheduler.feature.scheduler.presentation.viewmodel.AppSchedulerViewModel
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstalledAppListView(
    modifier: Modifier = Modifier,
    viewModel: AppSchedulerViewModel = hiltViewModel<AppSchedulerViewModel>(),
    coordinator: ScheduledAppListCoordinator,
)  {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val showBottomSheet = remember { mutableStateOf(false) }
    val lazyListState = rememberLazyListState()
    val appListUiState by viewModel.installedAppListUIState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllInstalledApps()
    }

    //show bottom sheet with date and time picker
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet.value = false },
            sheetState = bottomSheetState
        ) {
            DatePickerBottomSheet(
                onSelectDateTime = { date->
                    viewModel.createScheduledApp(viewModel.selectedApp!!, date)
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Installed Apps (${appListUiState.data.count()})") },
                navigationIcon = {
                    IconButton(onClick = { coordinator.back() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        if(appListUiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(appListUiState.data.count(), key = { item -> item }) { index->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White),
                        colors = CardColors(
                            containerColor = Color(0xFFFFFFFF),
                            contentColor = Color(0xFFFFFFFF),
                            disabledContainerColor = Color(0xFFFFFFFF),
                            disabledContentColor = Color(0xFFFFFFFF) // Replace with your desired color
                        )
                    ) {
                        AppItemView(
                            item = appListUiState.data[index],
                            showAddButton = true,
                            showDeleteButton = false,
                            onClickAdd = {
                                viewModel.selectedApp = appListUiState.data[index]
                                showBottomSheet.value = true
                            },
                            onClickDelete = {
                                // Handle delete button click
                            }
                        )
                    }
                }
            }

        if(!appListUiState.isLoading && appListUiState.data.isEmpty()) {
            NoDataView(details = "There is no installed app available. Please installed few from google play store.")
        }
    }
}

@Composable
fun AddToScheduleButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 15.dp), // height matches the design
        shape = RoundedCornerShape(50), // pill shape
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF00966E), // green background
            contentColor = Color.White          // text color
        )
    ) {
        Text(
            text = "Create Schedule",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}


@Composable
fun DatePickerBottomSheet(onSelectDateTime: (Date) -> Unit) {
    // Bottom sheet state
    val bottomSheetScrollState = rememberScrollState()
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()

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
                    val selectedDateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                    val hours = if (timePickerState.hour == 0 ) Date().hours else timePickerState.hour
                    val minutes = if (timePickerState.minute == 0 ) Date().minutes else timePickerState.minute

                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = selectedDateMillis
                    }
//                    val year = calendar.get(Calendar.YEAR)
//                    val month = calendar.get(Calendar.MONTH) // 0-based
//                    val day = calendar.get(Calendar.DAY_OF_MONTH)

//                    val calendar = Calendar.getInstance()
//                    calendar.set(year, month, day, hours, minutes, 0)
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