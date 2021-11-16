package com.crushtech.stateflowpractice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginScreenState(
    val name: String = "",
    val pwd: String = "",
    val isLoading: Boolean = false
)

sealed class LoginScreenEvents {
    data class ShowSnackBar(val message: String) : LoginScreenEvents()
}

class DemoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginScreenEvents>()
    val events = _events.asSharedFlow()

    fun onLoginPressed() = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(isLoading = true))
        delay(2000L)
        if (uiState.value.name == "android" && uiState.value.pwd == "correct") {
            _events.emit(LoginScreenEvents.ShowSnackBar("your credentials are pwd: ${uiState.value.pwd} name: ${uiState.value.name}"))
            _uiState.emit(uiState.value.copy(isLoading = false))
        } else {
            _events.emit(LoginScreenEvents.ShowSnackBar("invalid credentials"))
            _uiState.emit(uiState.value.copy(isLoading = false))
        }
    }

    fun onPwdChanged(pwd: String) = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(pwd = pwd))
    }

    fun onUserNameChanged(username: String) = viewModelScope.launch {
        _uiState.emit(uiState.value.copy(name = username))
    }
}
