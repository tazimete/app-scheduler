package com.interview.appscheduler.application

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class SchedulerApplication : Application(){
    init {
        instance = this
    }
    companion object {
        private var instance: SchedulerApplication? = null
        fun getApplicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}