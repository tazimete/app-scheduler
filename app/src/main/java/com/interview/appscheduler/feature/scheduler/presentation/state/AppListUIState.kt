package com.interview.appscheduler.feature.scheduler.presentation.state

import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity

data class AppListUIState(
    val data: List<AppEntity> = emptyList<AppEntity>(),
    val isLoading: Boolean = false,
    val message: String? = null,
    val code: Int? = null,
    val navigateToNext: Boolean = false
)