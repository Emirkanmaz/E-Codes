package com.emirkanmaz.ecodes.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<T: BaseNavigationEvent> : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<T>()
    val navigationEvent: SharedFlow<T> get() = _navigationEvent

    private val _loadingState: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val loadingState: StateFlow<Boolean> get() = _loadingState

    private val _errorState: MutableStateFlow<Pair<Boolean, String?>> = MutableStateFlow(false to null)
    val errorState: StateFlow<Pair<Boolean, String?>> get() = _errorState

    private var loadingJob = loadingCancel()

    private fun loadingCancel() = viewModelScope.launch {
        delay(150)
        _loadingState.value = false
    }

    fun setLoading(loading: Boolean) {
        if (loadingJob.isCompleted || loadingJob.isCancelled) {
            loadingJob = loadingCancel()
        }

        if (loading) {
            if (loadingJob.isActive) {
                loadingJob.cancel()
            }
            _loadingState.value = true
        } else {
            loadingJob.start()
        }
    }

    fun setError(error: Boolean, message: String? = null) {
        loadingCancel() // stop loading immediately
        _errorState.value = error to message
    }

    protected suspend fun navigateTo(event: T) {
        _navigationEvent.emit(event)
    }
}