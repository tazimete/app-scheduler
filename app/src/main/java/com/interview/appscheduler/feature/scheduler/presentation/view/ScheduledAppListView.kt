package com.interview.appscheduler.feature.scheduler.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.interview.appscheduler.feature.installedapp.presentation.view.DatePickerBottomSheet
import com.interview.appscheduler.feature.scheduler.domain.coordinator.ScheduledAppListCoordinator
import com.interview.appscheduler.feature.scheduler.presentation.view.subview.AppItemView
import com.interview.appscheduler.feature.scheduler.presentation.viewmodel.AppSchedulerViewModel
import com.interview.appscheduler.library.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduledAppListView(
    modifier: Modifier = Modifier,
    viewModel: AppSchedulerViewModel = hiltViewModel<AppSchedulerViewModel>(),
    coordinator: ScheduledAppListCoordinator,
)  {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val showBottomSheet = remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val appListUiState by viewModel.scheduledAppListUIState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllScheduledAppList()
    }

    //show bottom sheet with date and time picker
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet.value = false },
            sheetState = bottomSheetState
        ) {
            DatePickerBottomSheet(
                selectedDate = DateUtils.getCalenderDate((viewModel.selectedApp?.scheduledTime) ?: ""),
                onSelectDateTime = { date->
                    viewModel.updateScheduledAppTask(viewModel.selectedApp!!, date)
                    showBottomSheet.value = false
                }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Scheduled Apps (${appListUiState.data.count()})") },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            coordinator.navigateToInstalledAppListView()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add, // Replace with your desired icon
                            contentDescription = "Add Schedule"
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (appListUiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            for (app in appListUiState.data) {
                AppItemView(
                    item = app,
                    showEditButton = true,
                    showDeleteButton = true,
                    onClickEdit = {
                        viewModel.selectedApp = app
                        showBottomSheet.value = true
                    },
                    onClickDelete = {
                        viewModel.selectedApp = app
                        viewModel.deleteScheduledAppTask(viewModel.selectedApp!!)
                    }
                )
            }

            // show no data view
            if (!appListUiState.isLoading && appListUiState.data.isEmpty()) {
                NoDataView(details = "There is no scheduled app available. Please add a schedule.")
            }

            if (!appListUiState.isLoading ) {
                Spacer(Modifier.height(20.dp))

                AddScheduleButton(
                    onClick = {
                        coordinator.navigateToInstalledAppListView()
                    }
                )

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun AddScheduleButton(
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
            text = "Add Schedule",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

