package com.interview.appscheduler.feature.scheduler.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.interview.appscheduler.application.SchedulerApplication
import com.interview.appscheduler.core.Exception.ErrorEntity
import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.core.worker.DispatcherProvider
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.usecase.CreateAppScheduleUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.DeleteAppScheduleUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.GetAllInstalledAppListUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.GetAllScheduledAppListUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.GetAppScheduleUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.UpdateAppScheduleUseCase
import com.interview.appscheduler.feature.scheduler.domain.utility.ScheduleStatus
import com.interview.appscheduler.feature.scheduler.domain.worker.TaskScheduler
import com.interview.appscheduler.feature.scheduler.presentation.state.AppListUIState
import com.interview.appscheduler.library.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AppSchedulerViewModel @Inject constructor(
    private val getAllInstalledAppListUseCase: GetAllInstalledAppListUseCase,
    private val getAllScheduledAppListUseCase: GetAllScheduledAppListUseCase,
    private val getAppScheduleUseCase: GetAppScheduleUseCase,
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

    fun registerObserverForTaskStatus(
        appEntities: List<AppEntity>
    ) {
        appEntities.filter { it.status != ScheduleStatus.COMPLETED.getStatusValue() }.map { appEntity->
            observeTaskStatus(appEntity.taskId) { state->
                when(state) {
                    WorkInfo.State.SUCCEEDED -> {
                        appEntity.status = ScheduleStatus.COMPLETED.getStatusValue()
                        // Update the app status in database
                        selectedApp = appEntity
                        updateScheduledApp(appEntity)
                    }
                    WorkInfo.State.FAILED -> {
                        appEntity.status = ScheduleStatus.FAILED.getStatusValue()
                        // Update the app status in database
                        selectedApp = appEntity
                        updateScheduledApp(appEntity)
                    }
                    WorkInfo.State.BLOCKED -> {
                        appEntity.status = ScheduleStatus.BLOCKED.getStatusValue()
                        // Update the app status in database
                        selectedApp = appEntity
                        updateScheduledApp(appEntity)
                    }
                    WorkInfo.State.CANCELLED -> {
                        appEntity.status = ScheduleStatus.CANCELLED.getStatusValue()
                        // Update the app status in database
                        selectedApp = appEntity
                        updateScheduledApp(appEntity)
                    }

                    WorkInfo.State.ENQUEUED -> {}
                    WorkInfo.State.RUNNING -> {
                        appEntity.status = ScheduleStatus.RUNNING.getStatusValue()
                        // Update the app status in database
                        selectedApp = appEntity
                        updateScheduledApp(appEntity)
                    }
                }
            }
        }
    }

    fun observeTaskStatus(
        workId: String,
        onStatusChanged: (WorkInfo.State) -> Unit
    ) {
        val context = SchedulerApplication.getApplicationContext()
        viewModelScope.launch(dispatcherProvider.main) {
            taskScheduler.observeWorkStatus(
                context = context,
                workId = UUID.fromString(workId),
                onStatusChanged = { status ->
                    onStatusChanged(status)
                }
            )
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
        var workRequest = taskScheduler.updateScheduleTask(
            context = context,
            appEntity = appEntity
        )

        appEntity.taskId = workRequest.id.toString()
        appEntity.status = ScheduleStatus.RESCHEDULED.getStatusValue()
        selectedApp = appEntity
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

    fun getScheduledApp(scheduledTime: String) {
        _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(isLoading = true, message = null)

        viewModelScope.launch(dispatcherProvider.main) {
            getAppScheduleUseCase.invoke(scheduledTime)
                .flowOn(dispatcherProvider.io)
                .collect { result ->
                    result.fold(
                        { entity -> onSuccessGetAppScheduleResponse(entity) },
                        { error -> onFailedResponse(error) }
                    )
                }
        }
    }

    fun createScheduledApp(appEntity: AppEntity) {
        _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(isLoading = true, message = null)

        appEntity.status = ScheduleStatus.SCHEDULED.getStatusValue()

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
        registerObserverForTaskStatus(response.data ?: emptyList())

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

    // Handle success response for get app shedule by scheduled time
    private fun onSuccessGetAppScheduleResponse(response: Entity<AppEntity>) {
        if(response.data != null) {
            _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(
                isLoading = false,
                message = "Time conflicts with another schedule, Please choose a different time",
            )
        } else {
            
        }
    }

    // Handle success and failure responses
    private fun onSuccessCreateAppScheduleListResponse(response: Entity<Long>) {
        selectedApp?.id = response.data

        var data = _scheduledAppListUIState.value.data.toMutableList()
        data.add(selectedApp!!)

        //register observer
        registerObserverForTaskStatus(data)

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

        //register observer
        registerObserverForTaskStatus(data)

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

//        selectedApp = null // clear selected app after update
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
            message = when (errorEntity) {
                is ErrorEntity.DecodingError -> "Internal error, Contact with your admin."
                is ErrorEntity.NotFound -> "No data found"
                is ErrorEntity.DatabaseAccessingError -> "Database accessing error, Contact with your admin."
                is ErrorEntity.DatabaseWritingError -> "Database writing  error, Contact with your admin."
                is ErrorEntity.ObjectNotFoundInDatabaseError -> "Requested data not found, Contact with your admin."
                is ErrorEntity.NotUniqueData -> "App schedule already exists with given time, Please choose a different time"
                else -> "An unexpected error occurred. Please try again."
            }
        )

        selectedApp = null // clear selected app after error
    }

    fun showMessage(message: String) {
        _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(message = message)

        viewModelScope.launch {
            delay(1000) // import kotlinx.coroutines.delay
            _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(message = null)
        }
    }
}