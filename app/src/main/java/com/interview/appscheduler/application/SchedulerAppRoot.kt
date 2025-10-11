package com.interview.appscheduler.application

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.interview.appscheduler.core.navigator.NavigationItem
import com.interview.appscheduler.feature.scheduler.domain.coordinator.ScheduledAppListCoordinator
import com.interview.appscheduler.feature.scheduler.presentation.view.InstalledAppListView
import com.interview.appscheduler.feature.scheduler.presentation.view.ScheduledAppListView

@Composable
fun SchedulerAppRoot(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = NavigationItem.ScheduledAppList.route
    ) {
        composable(NavigationItem.ScheduledAppList.route) {
            ScheduledAppListView(coordinator = ScheduledAppListCoordinator(navController))
        }

        composable(NavigationItem.InstalledAppList.route) {
            InstalledAppListView()
        }
    }
}