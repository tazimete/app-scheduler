package com.interview.appscheduler.core.navigator

sealed class NavigationItem(val route: String, val title: String){
    object Home : NavigationItem("home", "Home")
    object ScheduledAppList : NavigationItem("scheduled_app_list", "Scheduled Apps")
    object InstalledAppList : NavigationItem("installed_app_list", "Installed Apps")
}