package com.example.apprecetas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apprecetas.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface AuthResult {
    data object Idle : AuthResult
    data object Loading : AuthResult
    data object Success : AuthResult
    data class Error(val cause: Throwable) : AuthResult
}

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val _result = MutableStateFlow<AuthResult>(AuthResult.Idle)
    val result: StateFlow<AuthResult> = _result.asStateFlow()

    fun login(email: String, password: String) = runAuthentication {
        repository.login(email, password)
        try {
            repository.getUserProfile()
        } catch (exception: Exception) {
            repository.logout()
            throw exception
        }
    }

    fun register(nombre: String, email: String, password: String) = runAuthentication {
        repository.register(nombre, email, password)
    }

    fun reset() {
        _result.value = AuthResult.Idle
    }

    private fun runAuthentication(action: suspend () -> Unit) {
        viewModelScope.launch {
            _result.value = AuthResult.Loading
            _result.value = runCatching { action() }
                .fold(onSuccess = { AuthResult.Success }, onFailure = { AuthResult.Error(it) })
        }
    }
}
