package com.interview.appscheduler.feature.home.presentation.view

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.interview.appscheduler.application.SchedulerAppRoot

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeView(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
)  {
    Scaffold(){ padding ->
        SchedulerAppRoot(navController = navController as NavHostController)
    }
}
