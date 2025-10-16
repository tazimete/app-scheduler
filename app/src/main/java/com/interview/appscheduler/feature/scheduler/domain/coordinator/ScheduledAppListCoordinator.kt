package com.interview.appscheduler.feature.scheduler.domain.coordinator

import androidx.navigation.NavController
import com.interview.appscheduler.core.navigator.Coordinator
import com.interview.appscheduler.core.navigator.NavigationItem

class ScheduledAppListCoordinator: Coordinator {
    override val navController: NavController

    constructor(navController: NavController) {
        this.navController = navController
    }

    override fun start() {
        navController.navigate(NavigationItem.Home.route)
    }

    fun navigateToInstalledAppListView() {
        navController.navigate(NavigationItem.InstalledAppList.route)
    }

    fun navigateToScheduledAppListView() {
        navController.navigate(NavigationItem.ScheduledAppList.route)
    }

    fun  navigateToScreen(navController: NavController, screen: String) {
        navController.navigate(screen)
    }

    fun back() {
        navController.popBackStack()
    }
}