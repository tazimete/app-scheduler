package com.interview.appscheduler.asset.string

enum class StringAssets(override val value: String): StringResource {
    // App Scheduler Repository
    GET_ALL_INSTALLED_APPS("Get all installed app scheduler successfully"),
    FAILED_TO_GET_ALL_INSTALLED_APPS("Failed to load installed app scheduler from os"),

    // App Scheduler Local Data Source
    GET_ALL_APP_SCHEDULES("Get all app scheduler successfully"),
    FAILED_TO_GET_ALL_APP_SCHEDULES("Get all app scheduler successfully"),

    GET_SCHEDULED_APP("Get app schedule successfully"),
    FAILED_TO_GET_SCHEDULED_APP("Failed to get app schedule"),

    CREATE_APP_SCHEDULE("Created app schedule successfully"),
    FAILED_TO_CREATE_APP_SCHEDULE("Failed to create app schedule"),

    UPDATE_APP_SCHEDULE("Updated app schedule successfully"),
    FAILED_TO_UPDATE_APP_SCHEDULE("Failed to update app schedule"),

    DELETE_APP_SCHEDULE("Deleted app schedule successfully"),
    FAILED_TO_DELETE_APP_SCHEDULE("Failed to delete app schedule"),
}
