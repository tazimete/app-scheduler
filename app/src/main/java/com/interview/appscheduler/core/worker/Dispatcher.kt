package com.interview.appscheduler.core.worker

import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers


interface DispatcherProvider {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
}

class DefaultDispatcherProvider @Inject constructor(): DispatcherProvider {
    override val io = Dispatchers.IO
    override val main = Dispatchers.Main
}
