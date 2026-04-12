package com.example.skga.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.local.StudentRepositoryImpl
import domain.usecases.forApp.SignInUseCase
import kotlinx.coroutines.launch

class LogInViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentRepositoryImpl(application)

    private val login = SignInUseCase(repository)

    private val _errorLogin = MutableLiveData<Boolean>(null)
    val errorLogin: LiveData<Boolean> get() = _errorLogin

    private val _errorBlankLoginPassword = MutableLiveData<Boolean>(null)
    val errorBlankLoginPassword: LiveData<Boolean> get() = _errorBlankLoginPassword

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            if (validateInput(email, pass)) {
                _loading.value = true
                val result = login.login(email, pass) // Вызывает репозиторий
                if (result.isSuccess) {
                    _loginSuccess.value = true
            } else {
                    _errorMessage.value = "Ошибка: ${result.exceptionOrNull()?.message}"
            }
                _loading.value = false
            }
        }
    }

    private fun validateInput(login: String, password: String): Boolean {
        var result = true

        if (login.isBlank() || password.isBlank()) {
            _errorBlankLoginPassword.postValue(true)
            result = false
        }
        return result
    }

    fun reserError(){
        _errorBlankLoginPassword.value = false
        _errorLogin.value = false
    }
}

