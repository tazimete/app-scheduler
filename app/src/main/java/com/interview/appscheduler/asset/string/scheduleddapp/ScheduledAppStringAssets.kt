package com.interview.appscheduler.asset.string.scheduleddapp

import com.interview.appscheduler.asset.string.abstraction.StringResource

enum class ScheduledAppStringAssets(override val value: String): StringResource {
    // App Scheduler Local Data Source
    GET_ALL_APP_SCHEDULES("Got all app scheduler successfully"),
    FAILED_TO_GET_ALL_APP_SCHEDULES("Get all app scheduler successfully"),

    GET_SCHEDULED_APP("Get app schedule successfully"),
    FAILED_TO_GET_SCHEDULED_APP("Failed to get app schedule"),

    CREATE_APP_SCHEDULE("Created app schedule successfully"),
    FAILED_TO_CREATE_APP_SCHEDULE("Failed to create app schedule"),

    UPDATE_APP_SCHEDULE("Updated app schedule successfully"),
    FAILED_TO_UPDATE_APP_SCHEDULE("Failed to update app schedule"),

    DELETE_APP_SCHEDULE("Deleted app schedule successfully"),
    FAILED_TO_DELETE_APP_SCHEDULE("Failed to delete app schedule"),

    //App Scheduler ViewModel
    TIME_CONFLICTS_WITH_ANOTHER_SCHEDULE("Time conflicts with another schedule, Please choose a different time"),
    NO_DATA_FOUND("No data found"),

    //Exception Messages
    DECODING_ERROR("Decoding error, Failed to decode your data"),
    DATABASE_ACCESS_ERROR("Database accessing error, Contact with your admin."),
    DATABASE_WRITE_ERROR("Database writing  error, Contact with your admin."),
    DATABASE_OBJECT_NOT_FOUND_ERROR("Requested data not found, Contact with your admin."),
    UNEXPECTED_ERROR("An unexpected error occurred. Please try again."),

    //App Scheduler View
    NO_SCHEDULED_APPS_AVAILABLE("There is no scheduled app available. Please add a schedule."),
}