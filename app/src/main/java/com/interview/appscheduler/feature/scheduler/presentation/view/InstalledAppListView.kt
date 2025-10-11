package com.interview.appscheduler.feature.scheduler.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstalledAppListView(
    modifier: Modifier = Modifier,
    viewModel: AppSchedulerViewModel = hiltViewModel<AppSchedulerViewModel>(),
    coordinator: ScheduledAppListCoordinator
)  {
    val lazyListState = rememberLazyListState()
    val appListUiState by viewModel.installedAppListUIState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Installed Apps") },
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
                                // Handle add button click
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

        if(!appListUiState.isLoading && appListUiState.data.isNotEmpty()) {
            Spacer(Modifier.height(20.dp))

            AddToScheduleButton(
                onClick = {

                }
            )

            Spacer(Modifier.height(100.dp))
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
            text = "Add Schedule",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

