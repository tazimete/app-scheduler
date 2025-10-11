package com.interview.appscheduler.core.navigator

import androidx.navigation.NavController

interface Coordinator {
    val navController: NavController
    fun start()
}