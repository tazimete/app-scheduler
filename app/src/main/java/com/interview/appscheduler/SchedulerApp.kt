package com.interview.appscheduler

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class SchedulerApp : Application(){
    init {
        instance = this
    }
    companion object {
        private var instance: SchedulerApp? = null
        fun getApplicationContext(): Context {
            return instance!!.applicationContext
        }
    }
}