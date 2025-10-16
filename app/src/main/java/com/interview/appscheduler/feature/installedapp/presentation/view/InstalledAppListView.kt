@file:OptIn(ExperimentalMaterial3Api::class)

package com.interview.appscheduler.feature.installedapp.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import com.interview.appscheduler.asset.string.installedapp.InstalledAppStringAssets
import com.interview.appscheduler.component.NoDataView
import com.interview.appscheduler.feature.scheduler.domain.coordinator.ScheduledAppListCoordinator
import com.interview.appscheduler.feature.scheduler.presentation.view.subview.AppItemView
import com.interview.appscheduler.feature.scheduler.presentation.view.subview.DatePickerBottomSheet
import com.interview.appscheduler.feature.scheduler.presentation.viewmodel.AppSchedulerViewModel
import com.interview.appscheduler.library.DateUtils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstalledAppListView(
    modifier: Modifier = Modifier,
    viewModel: AppSchedulerViewModel = hiltViewModel<AppSchedulerViewModel>(),
    coordinator: ScheduledAppListCoordinator,
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    val showBottomSheet = remember { mutableStateOf(false) }
    val snackBarHostState = remember { SnackbarHostState() }
    val snackBarScope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val installedAppListUIState by viewModel.installedAppListUIState.collectAsState()
    val scheduledAppListUIState by viewModel.scheduledAppListUIState.collectAsState()
    viewModel.coordinator = coordinator

    LaunchedEffect(Unit) {
        viewModel.getAllInstalledApps()
    }

    //show snackbar for installedAppListUIState message
    LaunchedEffect(installedAppListUIState.message) {
        installedAppListUIState.message?.let {
            snackBarScope.launch {
                snackBarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            }
        }
    }

    //show snackbar for scheduledAppListUIState message
    LaunchedEffect(scheduledAppListUIState.message) {
        scheduledAppListUIState.message?.let {
            snackBarScope.launch {
                snackBarHostState.showSnackbar(it, duration = SnackbarDuration.Short)
            }
        }
    }

    //show bottom sheet with date and time picker
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet.value = false },
            sheetState = bottomSheetState
        ) {
            DatePickerBottomSheet(
                selectedDate = DateUtils.getDeviceLocalDate(),
                onSelectDateTime = { date ->
                    viewModel.selectedDate = date
                    viewModel.checkAndScheduleApp(viewModel.actionType)
                    showBottomSheet.value = false
                }
            )
        }
    }

    Scaffold(
        snackbarHost = @Composable { SnackbarHost(hostState = snackBarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Installed Apps (${installedAppListUIState.data.count()})") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.coordinator?.back() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        if (installedAppListUIState.isLoading) {
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
            items(installedAppListUIState.data.count(), key = { item -> item }) { index ->
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
                        item = installedAppListUIState.data[index],
                        showAddButton = true,
                        showDeleteButton = false,
                        onClickAdd = {
                            viewModel.selectedApp = installedAppListUIState.data[index]
                            showBottomSheet.value = true
                        },
                    )
                }
            }
        }

        if (!installedAppListUIState.isLoading && installedAppListUIState.data.isEmpty()) {
            NoDataView(details = InstalledAppStringAssets.NO_INSTALLED_APPS_AVAILABLE.value)
        }
    }
}

