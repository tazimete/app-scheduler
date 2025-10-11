package com.interview.appscheduler.feature.scheduler.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.interview.appscheduler.component.NoDataView
import com.interview.appscheduler.feature.scheduler.presentation.view.subview.AppItemView
import com.interview.appscheduler.feature.scheduler.presentation.viewmodel.AppSchedulerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstalledAppListView(
    modifier: Modifier = Modifier,
    viewModel: AppSchedulerViewModel = hiltViewModel<AppSchedulerViewModel>(),
    navController: NavController = rememberNavController(),
)  {
    val scrollState = rememberScrollState()

    val appListUiState by viewModel.appListUIState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Installed Apps") },
                navigationIcon = {
                    IconButton(onClick = {  }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                for(app in appListUiState.data) {
                    Divider(
                        modifier = Modifier
                            .height(6.dp),
                        color = Color.Gray.copy(alpha = 0.2f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    AppItemView(
                        item = app,
                        showAddButton = true,
                        showDeleteButton = true,
                        onClickAdd = {
                            // Handle add button click
                        },
                        onClickDelete = {
                            // Handle delete button click
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Divider(
                        thickness = 0.75.dp,
                        color = Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 0.dp, horizontal = 8.dp)
                    )
                }

                if(appListUiState.data.isEmpty()) {
                    NoDataView(details = "There is no installed app available. Please installed few from google play store.")
                }

                if(appListUiState.data.isNotEmpty()) {
                    Spacer(Modifier.height(20.dp))

                    AddToScheduleButton(
                        onClick = {

                        }
                    )
                }

                Spacer(Modifier.height(100.dp))
            }
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

