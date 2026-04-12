package com.example.skga.presentation

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.local.StudentRepositoryImpl
import domain.entity.StudentItem
import domain.usecases.forApp.GetFacultiesUseCase
import domain.usecases.forApp.GetGroupsUseCase
import domain.usecases.forApp.SignUpUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentRepositoryImpl(application)

    private var searchJob: Job? = null

    private var allGroups: List<String> = emptyList()
    private var allFaculties = listOf<String>()

    private val signUp = SignUpUseCase(repository)
    val getGroups = GetGroupsUseCase(repository)

    val getFacultiesUseCase = GetFacultiesUseCase(repository)

    private val _facultyListLiveData = MutableLiveData<List<String>>()
    val facultyListLiveData: LiveData<List<String>>
        get() = _facultyListLiveData

    private val _groupsListLiveData = MutableLiveData<List<String>>()
    val groupsListLiveData: LiveData<List<String>>
        get() = _groupsListLiveData

    private val _errorInputFullName = MutableLiveData<Boolean>()
    val errorInputFullName: LiveData<Boolean>
        get() = _errorInputFullName
    private val _errorInputEmail = MutableLiveData<Boolean>()
    val errorInputEmail: LiveData<Boolean>
        get() = _errorInputEmail
    private val _errorInputCorrectEmail = MutableLiveData<Boolean>()
    val errorInputCorrectEmail: LiveData<Boolean>
        get() = _errorInputCorrectEmail
    private val _errorInputPhone = MutableLiveData<Boolean>()
    val errorInputPhone: LiveData<Boolean>
        get() = _errorInputPhone
    private val _errorInputCorrectPhone = MutableLiveData<Boolean>()
    val errorInputCorrectPhone: LiveData<Boolean>
        get() = _errorInputCorrectPhone
    private val _errorInputPassword = MutableLiveData<Boolean>()
    val errorInputPassword: LiveData<Boolean>
        get() = _errorInputPassword
    private val _errorInputCorrectPassword = MutableLiveData<Boolean>()
    val errorInputCorrectPassword: LiveData<Boolean>
        get() = _errorInputCorrectPassword
    private val _errorRepeatPassword = MutableLiveData<Boolean>()
    val errorRepeatPassword: LiveData<Boolean>
        get() = _errorRepeatPassword
    private val _errorInputGroup = MutableLiveData<Boolean>()
    val errorInputGroup: LiveData<Boolean>
        get() = _errorInputGroup
    private val _errorInputCorrectFaculties = MutableLiveData<Boolean>()
    val errorInputCorrectFaculties: LiveData<Boolean>
        get() = _errorInputCorrectFaculties
    private val _errorInputCorrectGroup = MutableLiveData<Boolean>()
    val errorInputCorrectGroup: LiveData<Boolean>
        get() = _errorInputCorrectGroup

    private val _errorInputFaculties = MutableLiveData<Boolean>()
    val errorInputFaculties: LiveData<Boolean>
        get() = _errorInputFaculties
    private val _isUserExists = MutableLiveData<Boolean>()
    val isUserExists: LiveData<Boolean>
        get() = _isUserExists

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading

    private val _isRegistrationSuccess = MutableLiveData<Boolean>()
    val isRegistrationSuccess: LiveData<Boolean>
        get() = _isRegistrationSuccess

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage





    fun register(studentItem: StudentItem, repeatPassword: String) {
        val parsedStudentItem = parseStudent(studentItem)
        if (validateInput(parsedStudentItem, repeatPassword)) {
            viewModelScope.launch {
                try {
                    _loading.value = true
                    val result = signUp.signUp(studentItem)
                    if (result.isSuccess) {
                        _isRegistrationSuccess.value = true
                    }
                } catch (e: HttpException) {
                    val errorBody = e.response()?.errorBody()?.string() ?: ""

                    when {
                        errorBody.contains("already registered") -> {
                            _errorInputEmail.value = true
                            _errorMessage.value = "Эта почта уже занята"
                        }

                        errorBody.contains("23505") -> { // Если телефон UNIQUE в базе
                            _errorInputPhone.value = true
                            _errorMessage.value = "Этот телефон уже используется"
                        }

                        errorBody.contains("23503") -> {
                            _errorInputCorrectGroup.value = true
                            _errorMessage.value = "Ошибка группы на сервере"
                        }

                        else -> {
                            _errorMessage.value = "Ошибка сервера: ${e.code()}"
                        }
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Нет интернета или ошибка сети"
                } finally {
                    _loading.value = false // Прячем ProgressBar
                }
            }
        }
    }


    fun loadInitData() {
        viewModelScope.launch {
            allGroups = getGroups.getGroups()
            _groupsListLiveData.value = allGroups
            allFaculties = getFacultiesUseCase.getFaculties().map { it.name }
            _facultyListLiveData.value = allFaculties
                }
    }


    private fun parseStudent(studentItem: StudentItem?): StudentItem {
        val parsedStudentItem = StudentItem(
            lastName = studentItem?.lastName?.trim() ?: "",
            firstName = studentItem?.firstName?.trim() ?: "",
            middleName = studentItem?.middleName?.trim() ?: "",
            group = studentItem?.group?.trim() ?: "",
            phone = studentItem?.phone?.trim() ?: "",
            email = studentItem?.email?.trim() ?: "",
            password = studentItem?.password?.trim() ?: "",
            faculties = studentItem?.faculties?.trim() ?: ""
        )
        return parsedStudentItem
    }

    fun resetErrorFullName() {
        _errorInputFullName.value = false
    }

    fun resetErrorGroup() {
        _errorInputGroup.value = false
    }

    fun resetErrorPassword() {
        _errorInputPassword.value = false
    }

    fun resetErrorEmail() {
        _errorInputEmail.value = false
    }

    fun resetErrorRepeatPassword() {
        _errorRepeatPassword.value = false
    }

    fun resetErrorPhone() {
        _errorInputPhone.value = false
    }


    private fun validateInput(studentItem: StudentItem, repeatPassword: String): Boolean {
        var result = true
        if (studentItem.middleName.isBlank() || studentItem.firstName.isBlank() || studentItem.lastName.isBlank()) {
            _errorInputFullName.value = true
            result = false
        }
        if (studentItem.group?.isBlank() ?: true) {
            _errorInputGroup.value = true
            result = false
        }
        if (studentItem.faculties?.isBlank() ?: true) {
            _errorInputFaculties.value = true
            result = false
        }
        if (!allGroups.contains(studentItem.group)) {
            _errorInputCorrectGroup.value = true
            result = false
        }
        if(!allFaculties.contains(studentItem.faculties)){
            _errorInputCorrectFaculties.value = true
            result = false
        }
        if (studentItem.phone.isBlank()) {
            _errorInputPhone.value = true
            result = false
        }
        if (!isValidRuPhone(studentItem.phone)) {
            _errorInputCorrectPhone.value = true
            result = false
        }
        if (studentItem.email.isBlank()) {
            _errorInputEmail.value = true
            result = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(studentItem.email).matches()) {
            _errorInputCorrectEmail.value = true
            result = false
        }

        if (studentItem.password.isBlank()) {
            _errorInputPassword.value = true
            result = false
        }
        if (!isLatinPassword(studentItem.password)) {
            _errorInputCorrectPassword.value = true
            result = false
        }
        if (repeatPassword != studentItem.password) {
            _errorRepeatPassword.value = true
            result = false
        }
        return result
    }


    private fun isValidRuPhone(phone: String): Boolean {
        val digits = normalizePhone(phone)

        if (digits.length != 11) return false
        if (!(digits.startsWith("8") || digits.startsWith("7"))) return false

        return true
    }

    private fun normalizePhone(phone: String): String {
        return phone.replace(Regex("[^0-9]"), "")
    }

    private fun isLatinPassword(password: String): Boolean {
        if (password.length < 6) return false

        return Regex("^[A-Za-z0-9!@#\$%^&*()_+=\\-{}\\[\\]:;\"'<>,.?/]+$")
            .matches(password)
    }
}