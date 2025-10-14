package com.interview.appscheduler.feature.scheduler.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interview.appscheduler.application.SchedulerApplication
import com.interview.appscheduler.core.Exception.ErrorEntity
import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.core.worker.DispatcherProvider
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.usecase.CreateAppScheduleUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.DeleteAppScheduleUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.GetAllInstalledAppListUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.GetAllScheduledAppListUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.UpdateAppScheduleUseCase
import com.interview.appscheduler.feature.scheduler.domain.worker.TaskScheduler
import com.interview.appscheduler.feature.scheduler.presentation.state.AppListUIState
import com.interview.appscheduler.library.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AppSchedulerViewModel @Inject constructor(
    private val getAllInstalledAppListUseCase: GetAllInstalledAppListUseCase,
    private val getAllScheduledAppListUseCase: GetAllScheduledAppListUseCase,
    private val createAppScheduleUseCase: CreateAppScheduleUseCase,
    private val updateAppScheduleUseCase: UpdateAppScheduleUseCase,
    private val deleteAppScheduleUseCase: DeleteAppScheduleUseCase,
    private val taskScheduler: TaskScheduler,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {
    private val _scheduledAppListUIState = MutableStateFlow(AppListUIState(isLoading = true))
    val scheduledAppListUIState = _scheduledAppListUIState.asStateFlow()

    private val _installedAppListUIState = MutableStateFlow(AppListUIState(isLoading = true))
    val installedAppListUIState = _installedAppListUIState.asStateFlow()

    var selectedApp: AppEntity? = null

    fun getAllScheduledAppList() {
        _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(isLoading = true, message = null)

        viewModelScope.launch(dispatcherProvider.main) {
            getAllScheduledAppListUseCase.invoke()
                .flowOn(dispatcherProvider.io)
                .collect { result ->
                    result.fold(
                        { entity -> onSuccessGetAllScheduledAppListResponse(entity) },
                        { error -> onFailedResponse(error) }
                    )
                }
        }
    }

    fun getAllInstalledApps() {
        _installedAppListUIState.value = _installedAppListUIState.value.copy(isLoading = true, message = null)

        viewModelScope.launch(dispatcherProvider.main) {
            getAllInstalledAppListUseCase.invoke()
                .flowOn(dispatcherProvider.io)
                .collect { result ->
                    result.fold(
                        { entity -> onSuccessGetAllInstalledAppListResponse(entity) },
                        { error -> onFailedResponse(error) }
                    )
                }
        }
    }

    fun addScheduleAppTask(appEntity: AppEntity, selectedDate: Date) {
        val context = SchedulerApplication.getApplicationContext()
        appEntity.scheduledTime = DateUtils.getCalendarDateToString(selectedDate)

        // Schedule the app to worker thread using WorkManager
        var workRequest = taskScheduler.addScheduleTask(
            context = context,
            appEntity = appEntity
        )

        appEntity.taskId = workRequest.id.toString()
        selectedApp = appEntity
        createScheduledApp(appEntity)
    }

    fun updateScheduledAppTask(appEntity: AppEntity, selectedDate: Date) {
        val context = SchedulerApplication.getApplicationContext()
        appEntity.scheduledTime = DateUtils.getCalendarDateToString(selectedDate)

        // Update the scheduled app to worker thread using WorkManager
        var result = taskScheduler.updateScheduleTask(
            context = context,
            appEntity = appEntity
        )

        updateScheduledApp(appEntity)
    }

    fun deleteScheduledAppTask(appEntity: AppEntity) {
        val context = SchedulerApplication.getApplicationContext()

        // Delete the scheduled app to worker thread using WorkManager
        var result = taskScheduler.deleteScheduleTask(
            context = context,
            appEntity = appEntity
        )

        deleteScheduledApp(appEntity)
    }

    fun createScheduledApp(appEntity: AppEntity) {
        _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(isLoading = true, message = null)

        viewModelScope.launch(dispatcherProvider.main) {
            createAppScheduleUseCase.invoke(appEntity)
                .flowOn(dispatcherProvider.io)
                .collect { result ->
                    result.fold(
                        { entity -> onSuccessCreateAppScheduleListResponse(entity) },
                        { error -> onFailedResponse(error) }
                    )
                }
        }
    }

fun updateScheduledApp(appEntity: AppEntity) {
    _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(isLoading = true, message = null)

    viewModelScope.launch(dispatcherProvider.main) {
        updateAppScheduleUseCase.invoke(appEntity)
            .flowOn(dispatcherProvider.io)
            .collect { result ->
                result.fold(
                    { entity -> onSuccessUpdateAppScheduleListResponse(entity) },
                    { error -> onFailedResponse(error) }
                )
            }
    }
}

    fun deleteScheduledApp(appEntity: AppEntity) {
        _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(isLoading = true, message = null)

        viewModelScope.launch(dispatcherProvider.main) {
            deleteAppScheduleUseCase.invoke(appEntity)
                .flowOn(dispatcherProvider.io)
                .collect { result ->
                    result.fold(
                        { entity -> onSuccessDeleteAppScheduleListResponse(entity) },
                        { error -> onFailedResponse(error) }
                    )
                }
        }
    }

    // Handle success and failure responses
    private fun onSuccessGetAllInstalledAppListResponse(response: Entity<List<AppEntity>>) {
        response.data?.let {
            _installedAppListUIState.value = _installedAppListUIState.value.copy(
                isLoading = false,
                message = "Got all installed app list successfully",
                code = 200,
                data = it
            )
        } ?: run {
            onFailedResponse(
                ErrorEntity.ServerError(
                    errorCode = 404,
                    errorMessage = "No data found"
                )
            )
        }
    }

    private fun onSuccessGetAllScheduledAppListResponse(response: Entity<List<AppEntity>>) {
        response.data?.let {

            _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(
                isLoading = false,
                message = "Get all scheduled app list successfully",
                code = 200,
                data = it
            )
        } ?: run {
            onFailedResponse(
                ErrorEntity.ServerError(
                    errorCode = 404,
                    errorMessage = "No data found"
                )
            )
        }
    }

    // Handle success and failure responses
    private fun onSuccessCreateAppScheduleListResponse(response: Entity<Long>) {
        selectedApp?.id = response.data

        var data = _scheduledAppListUIState.value.data.toMutableList()
        data.add(selectedApp!!)

        response.data?.let {
            _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(
                isLoading = false,
                message = "Created app schedule successfully",
                code = 200,
                data = data
            )
        } ?: run {
            onFailedResponse(
                ErrorEntity.ServerError(
                    errorCode = 404,
                    errorMessage = "No data found"
                )
            )
        }

        selectedApp = null // clear selected app after add
    }

    // Handle success update app schedule response
    private fun onSuccessUpdateAppScheduleListResponse(response: Entity<Int>) {
        var data = _scheduledAppListUIState.value.data.toMutableList()
        var index = data.indexOfFirst { it.id == selectedApp?.id }

        if(index != -1) {
            data[index] = selectedApp!!

            data = data.map {
                AppEntity(
                    id = it.id,
                    appId = it.appId,
                    taskId = it.taskId,
                    name = it.name,
                    packageName = it.packageName,
                    icon = it.icon,
                    versionCode = it.versionCode,
                    versionName = it.versionName,
                    isSystemApp = it.isSystemApp,
                    thumbnail = it.thumbnail,
                    isScheduled = it.isScheduled,
                    scheduledTime = it.scheduledTime,
                    installedTime = it.installedTime,
                    status = it.status,
                )
            }.toMutableList()
        }

        response.data?.let {
            _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(
                isLoading = false,
                message = "Updated app schedule successfully",
                code = 200,
                data = data
            )
        } ?: run {
            onFailedResponse(
                ErrorEntity.ServerError(
                    errorCode = 404,
                    errorMessage = "No data found"
                )
            )
        }

        selectedApp = null // clear selected app after update
    }

    // Handle success delete app schedule response
    private fun onSuccessDeleteAppScheduleListResponse(response: Entity<Int>) {
        var data = _scheduledAppListUIState.value.data.toMutableList()
        var index = data.indexOfFirst { it.id == selectedApp?.id }

        if(index != -1) {
            data.removeAt(index)

            data = data.map {
                AppEntity(
                    id = it.id,
                    appId = it.appId,
                    taskId = it.taskId,
                    name = it.name,
                    packageName = it.packageName,
                    icon = it.icon,
                    versionCode = it.versionCode,
                    versionName = it.versionName,
                    isSystemApp = it.isSystemApp,
                    thumbnail = it.thumbnail,
                    isScheduled = it.isScheduled,
                    scheduledTime = it.scheduledTime,
                    installedTime = it.installedTime,
                    status = it.status,
                )
            }.toMutableList()
        }

        response.data?.let {
            _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(
                isLoading = false,
                message = "Deleted app schedule successfully",
                code = 200,
                data = data
            )
        } ?: run {
            onFailedResponse(
                ErrorEntity.ServerError(
                    errorCode = 404,
                    errorMessage = "No data found"
                )
            )
        }

        selectedApp = null // clear selected app after delete
    }

    private fun onFailedResponse(error: Throwable) {
        val errorEntity = error as? ErrorEntity

        _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(
            isLoading = false,
            data = emptyList(),
            code = 404,
            message = when (errorEntity) {
                is ErrorEntity.NetworkError -> "Network connection error. Please try again."
                is ErrorEntity.ServerError -> "Server error. Please try again later."
                is ErrorEntity.DecodingError -> "Internal error, Contact with your admin."
                is ErrorEntity.NotFound -> "No data found"
                is ErrorEntity.DatabaseAccessingError -> "Database accessing error, Contact with your admin."
                is ErrorEntity.DatabaseWritingError -> "Database writing  error, Contact with your admin."
                is ErrorEntity.ObjectNotFoundInDatabaseError -> "Requested data not found, Contact with your admin."
                is ErrorEntity.FileAccessingError -> "Directory access error, Contact with your admin."
                is ErrorEntity.FileWritingError -> "File writing error, Contact with your admin."
                else -> "An unexpected error occurred. Please try again."
            }
        )

        selectedApp = null // clear selected app after error
    }
}