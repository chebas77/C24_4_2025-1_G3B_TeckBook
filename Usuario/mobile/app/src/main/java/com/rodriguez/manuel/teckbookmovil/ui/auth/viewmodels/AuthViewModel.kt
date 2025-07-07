package com.rodriguez.manuel.teckbookmovil.ui.auth.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rodriguez.manuel.teckbookmovil.data.models.auth.LoginResponse
import com.rodriguez.manuel.teckbookmovil.data.repositories.AuthRepository
import com.rodriguez.manuel.teckbookmovil.data.services.AuthService
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // 🔑 Estados públicos para observar en LoginActivity
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    /**
     * Login tradicional
     */
    fun login(email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.login(email, password)
            result.onSuccess {
                _loginResult.postValue(Result.success(it))
            }.onFailure {
                _loginResult.postValue(Result.failure(it))
                _error.postValue(it.localizedMessage ?: "Error desconocido")
            }
            _isLoading.postValue(false)
        }
    }

    /**
     * Login con Google (necesario para `processGoogleAuth`)
     */
    fun processGoogleAuth(idToken: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.loginWithGoogle(idToken) // 👈 Asegúrate que exista en tu repo
            result.onSuccess {
                _loginResult.postValue(Result.success(it))
            }.onFailure {
                _loginResult.postValue(Result.failure(it))
                _error.postValue(it.localizedMessage ?: "Error desconocido")
            }
            _isLoading.postValue(false)
        }
    }

    /**
     * Factory para ViewModelProvider
     */
    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
