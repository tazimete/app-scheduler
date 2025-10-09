package com.interview.appscheduler.core.di

import com.interview.appscheduler.core.worker.DefaultDispatcherProvider
import com.interview.appscheduler.core.worker.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class DispatcherProviderModule {
    @Binds
    abstract fun provideDispatcher(dispatcherProvider: DefaultDispatcherProvider): DispatcherProvider
}