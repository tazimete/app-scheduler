package com.interview.appscheduler.asset.string.installedapp

import com.interview.appscheduler.asset.string.abstraction.StringResource

enum class InstalledAppStringAssets(override val value: String): StringResource {
    // App Scheduler Repository
    GET_ALL_INSTALLED_APPS("Got all installed apps successfully"),
    FAILED_TO_GET_ALL_INSTALLED_APPS("Failed to load installed app scheduler from os"),

    //App Scheduler ViewModel
    TIME_CONFLICTS_WITH_ANOTHER_SCHEDULE("Time conflicts with another schedule, Please choose a different time"),
    NO_DATA_FOUND("No data found"),

    //App Scheduler View
    NO_INSTALLED_APPS_AVAILABLE("There is no installed app available. Please install app."),
}