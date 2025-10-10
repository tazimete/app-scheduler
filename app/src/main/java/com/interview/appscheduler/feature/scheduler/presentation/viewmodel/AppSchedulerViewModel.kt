package com.interview.appscheduler.feature.scheduler.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.interview.appscheduler.core.Exception.ErrorEntity
import com.interview.appscheduler.core.domain.Entity
import com.interview.appscheduler.core.worker.DispatcherProvider
import com.interview.appscheduler.feature.scheduler.domain.entity.AppEntity
import com.interview.appscheduler.feature.scheduler.domain.usecase.GetAllAppListUseCase
import com.interview.appscheduler.feature.scheduler.presentation.state.AppListUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppSchedulerViewModel @Inject constructor(
    private val getAllAppListUseCase: GetAllAppListUseCase,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {
    private val _appListUIState = MutableStateFlow(AppListUIState(isLoading = true))
    val appListUIState = _appListUIState.asStateFlow()

    init {
        getAllAppList()
    }

    fun getAllAppList() {
        _appListUIState.value = _appListUIState.value.copy(isLoading = true, message = null)

        viewModelScope.launch(dispatcherProvider.main) {
            getAllAppListUseCase.invoke()
                .flowOn(dispatcherProvider.io)
                .collect { result ->
                    result.fold(
                        { entity -> onSuccessResponse(entity) },
                        { error -> onFailedResponse(error) }
                    )
                }
        }
    }

    private fun onSuccessResponse(response: Entity<List<AppEntity>>) {
        // You can update the UI state with the received data
        response.data?.let {

            _appListUIState.value = _appListUIState.value.copy(
                isLoading = false,
                message = null,
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

    private fun onFailedResponse(error: Throwable) {
        val errorEntity = error as? ErrorEntity

        _appListUIState.value = _appListUIState.value.copy(
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
    }
}
