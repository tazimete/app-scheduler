package com.interview.appscheduler.feature.scheduler.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.interview.appscheduler.application.SchedulerApplication
import com.interview.appscheduler.asset.string.installedapp.InstalledAppStringAssets
import com.interview.appscheduler.asset.string.scheduleddapp.ScheduledAppStringAssets
import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.core.domain.exception.DatabaseError
import com.interview.appscheduler.core.worker.DispatcherProvider
import com.interview.appscheduler.feature.scheduler.domain.coordinator.ScheduledAppListCoordinator
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.usecase.CreateAppScheduleUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.DeleteAppScheduleUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.GetAllInstalledAppListUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.GetAllScheduledAppListUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.GetAppScheduleUseCase
import com.interview.appscheduler.feature.scheduler.domain.usecase.UpdateAppScheduleUseCase
import com.interview.appscheduler.feature.scheduler.domain.utility.ScheduleActionType
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
    public var coordinator: ScheduledAppListCoordinator? = null

    private val _scheduledAppListUIState =
        MutableStateFlow(AppListUIState(initialLoad = true, isLoading = true))
    val scheduledAppListUIState = _scheduledAppListUIState.asStateFlow()

    private val _installedAppListUIState =
        MutableStateFlow(AppListUIState(initialLoad = true, isLoading = true))
    val installedAppListUIState = _installedAppListUIState.asStateFlow()

    var selectedApp: AppEntity? = null
    var selectedDate: Date? = null
    var actionType: ScheduleActionType = ScheduleActionType.ADD

    fun getAllScheduledAppList() {
        _scheduledAppListUIState.value =
            _scheduledAppListUIState.value.copy(isLoading = true, message = null)

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
        _installedAppListUIState.value =
            _installedAppListUIState.value.copy(isLoading = true, message = null)

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
        val taskIds = appEntities
            .filter { app -> app.status != ScheduleStatus.COMPLETED.getStatusValue() }
            .map { it.taskId }.filter { it.isNotEmpty() }

        if (taskIds.isEmpty()) return // no tasks to observe

        taskIds.forEach { taskId ->
            observeTasksStatus(taskId) { workInfo ->
                val appEntity = appEntities.first() { app ->
                    app.taskId == workInfo.id.toString()
                }

                when (workInfo.state) {
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
                        appEntity.status = ScheduleStatus.RESCHEDULED.getStatusValue() //ScheduleStatus.CANCELLED.getStatusValue()
                        // set schedule time to update in case of reschedule
                        if( (selectedApp?.scheduledTime ?: "").isNotEmpty() ) {
                            appEntity.scheduledTime = selectedApp?.scheduledTime
                        }
                        selectedApp = appEntity
                        // Update the app status in database
                        updateScheduledApp(appEntity)
                    }

                    WorkInfo.State.ENQUEUED -> {
                        appEntity.status = (
                                if ((appEntity.status ?: -1) == ScheduleStatus.SCHEDULED.getStatusValue()
                                )
                                    ScheduleStatus.SCHEDULED.getStatusValue()
                                else
                                    ScheduleStatus.RESCHEDULED.getStatusValue()
                                )
                        // Update the app status in database
                        selectedApp = appEntity
                        updateScheduledApp(appEntity)
                    }

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

    fun observeTasksStatus(
        workId: String,
        onStatusChanged: (WorkInfo) -> Unit
    ) {
        val context = SchedulerApplication.getApplicationContext()
        var workUuid = UUID.fromString(workId)

        viewModelScope.launch(dispatcherProvider.main) {
            taskScheduler.observeWorkStatus(
                context = context,
                workId = workUuid,
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

    fun checkAndScheduleApp(actionType: ScheduleActionType) {
        _scheduledAppListUIState.value =
            _scheduledAppListUIState.value.copy(isLoading = true, message = null)
        val scheduledTime = DateUtils.getCalendarDateToString(selectedDate!!)

        viewModelScope.launch(dispatcherProvider.main) {
            getAppScheduleUseCase.invoke(scheduledTime)
                .flowOn(dispatcherProvider.io)
                .collect { result ->
                    result.fold(
                        { entity ->
                            if (entity.data != null) {
                                onFailedResponse(DatabaseError.TimeConflicts)
                            } else {
                                when (actionType) {
                                    ScheduleActionType.ADD -> addScheduleAppTask(selectedApp!!, selectedDate!!)

                                    ScheduleActionType.UPDATE -> updateScheduledAppTask(selectedApp!!, selectedDate!!)

                                    else -> {
                                        // Nothing to do
                                    }
                                }
                            }
                        },
                        { error -> onFailedResponse(error) }
                    )
                }
        }
    }

    fun createScheduledApp(appEntity: AppEntity) {
        _scheduledAppListUIState.value =
            _scheduledAppListUIState.value.copy(isLoading = true, message = null)

        appEntity.status = ScheduleStatus.SCHEDULED.getStatusValue()

        viewModelScope.launch(dispatcherProvider.main) {
            createAppScheduleUseCase.invoke(appEntity)
                .flowOn(dispatcherProvider.io)
                .collect { result ->
                    result.fold(
                        { entity -> onSuccessCreateAppScheduleResponse(entity) },
                        { error -> onFailedResponse(error) }
                    )
                }
        }
    }

    fun updateScheduledApp(appEntity: AppEntity) {
        _scheduledAppListUIState.value =
            _scheduledAppListUIState.value.copy(isLoading = true, message = null)

        viewModelScope.launch(dispatcherProvider.main) {
            updateAppScheduleUseCase.invoke(appEntity)
                .flowOn(dispatcherProvider.io)
                .collect { result ->
                    result.fold(
                        { entity -> onSuccessUpdateAppScheduleResponse(entity) },
                        { error -> onFailedResponse(error) }
                    )
                }
        }
    }

    fun deleteScheduledApp(appEntity: AppEntity) {
        _scheduledAppListUIState.value =
            _scheduledAppListUIState.value.copy(isLoading = true, message = null)

        viewModelScope.launch(dispatcherProvider.main) {
            deleteAppScheduleUseCase.invoke(appEntity)
                .flowOn(dispatcherProvider.io)
                .collect { result ->
                    result.fold(
                        { entity -> onSuccessDeleteAppScheduleResponse(entity) },
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
                code = 200,
                data = it
            )

            showMessage(message = InstalledAppStringAssets.GET_ALL_INSTALLED_APPS.value)
        } ?: run {
            _installedAppListUIState.value = AppListUIState(
                isLoading = false,
                code = 404
            )

            onFailedResponse(DatabaseError.NotFound)
        }
    }

    private fun onSuccessGetAllScheduledAppListResponse(response: Entity<List<AppEntity>>) {
        registerObserverForTaskStatus(response.data ?: emptyList())

        response.data?.let {
            _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(
                isLoading = false,
                code = 200,
                data = it
            )

            showMessage(message = ScheduledAppStringAssets.GET_ALL_APP_SCHEDULES.value)
        } ?: run {
            onFailedResponse(DatabaseError.NotFound)
        }
    }

    // Handle success and failure responses
    private fun onSuccessCreateAppScheduleResponse(response: Entity<Long>) {
        selectedApp?.id = response.data

        var data = _scheduledAppListUIState.value.data.toMutableList()
        data.add(selectedApp!!)

        response.data?.let {
            _scheduledAppListUIState.value = _scheduledAppListUIState.value.copy(
                isLoading = false,
                data = data
            )

            showMessage(message = ScheduledAppStringAssets.CREATE_APP_SCHEDULE.value)
        } ?: run {
            onFailedResponse(DatabaseError.WriteError)
        }

        //register observer
        registerObserverForTaskStatus(listOf(selectedApp!!))

        //navigate back to scheduled app list view
        coordinator?.back()
    }

    // Handle success update app schedule response
    private fun onSuccessUpdateAppScheduleResponse(response: Entity<Int>) {
        var data = _scheduledAppListUIState.value.data.toMutableList()
        var index = data.indexOfFirst { it.id == selectedApp?.id }

        if (index != -1) {
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
                data = data
            )

            showMessage(message = ScheduledAppStringAssets.UPDATE_APP_SCHEDULE.value)
        } ?: run {
            onFailedResponse(DatabaseError.UpdateError)
        }
    }

    // Handle success delete app schedule response
    private fun onSuccessDeleteAppScheduleResponse(response: Entity<Int>) {
        var data = _scheduledAppListUIState.value.data.toMutableList()
        var index = data.indexOfFirst { it.id == selectedApp?.id }

        if (index != -1) {
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
                data = data
            )

            showMessage(message = ScheduledAppStringAssets.DELETE_APP_SCHEDULE.value)
        } ?: run {
            onFailedResponse(DatabaseError.DeleteError)
        }
    }

    private fun onFailedResponse(error: Throwable) {
        showMessage(error.message ?: ScheduledAppStringAssets.FAILED_TO_PROCESS_REQUEST.value)

        selectedApp = null // clear selected app after error
    }

    fun showMessage(message: String) {
        _scheduledAppListUIState.value =
            _scheduledAppListUIState.value.copy(isLoading = false, message = message)

        viewModelScope.launch {
            delay(1000) // import kotlinx.coroutines.delay
            _scheduledAppListUIState.value =
                _scheduledAppListUIState.value.copy(isLoading = false, message = null)
        }
    }
}