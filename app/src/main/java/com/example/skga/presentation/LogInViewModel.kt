package com.example.skga.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.GroupsAppDatabase
import data.StudentRepositoryImpl
import domain.entity.StudentItem
import domain.usecases.AddNewGroupUseCase
import domain.usecases.GetUserByLoginUseCase
import kotlinx.coroutines.launch
import java.security.MessageDigest
import kotlin.math.log

class LogInViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentRepositoryImpl(application)
    private val getUserByLoginUseCase = GetUserByLoginUseCase(repository)

    private val _errorLogin = MutableLiveData<Boolean?>(null)
    val errorLogin: LiveData<Boolean?> get() = _errorLogin

    private val _errorBlankLoginPassword = MutableLiveData<Boolean?>(null)
    val errorBlankLoginPassword: LiveData<Boolean?> get() = _errorBlankLoginPassword

    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> get() = _loginSuccess

    fun login(login: String, password: String) {
        viewModelScope.launch {
            val user = getUserByLoginUseCase.getUserByLogin(login.trim())

            if (validateInput(login, password, user)) {
                _loginSuccess.postValue(true)
            } else {
                _loginSuccess.postValue(false)
            }
        }
    }

    private fun validateInput(login: String, password: String, user: StudentItem?): Boolean {
        var result = true

        if (login.isBlank() || password.isBlank()) {
            _errorBlankLoginPassword.postValue(true)
            result = false
        }

        if (user == null || user.password != hashPassword(password)) {
            _errorLogin.postValue(true)
            result = false
        }

        return result
    }

    fun reserError(){
        _errorBlankLoginPassword.value = false
        _errorLogin.value = false
    }

    private fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(password.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}

