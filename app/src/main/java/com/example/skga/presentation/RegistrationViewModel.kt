package com.example.skga.presentation

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.StudentRepositoryImpl
import domain.entity.StudentItem
import domain.usecases.AddStudentItemUseCase
import domain.usecases.GetGroupsStartingWithUseCase
import kotlinx.coroutines.launch
import java.security.MessageDigest

class RegistrationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentRepositoryImpl(application)

    private var cachedGroups: List<String> = emptyList()

    private val addStudentItemUseCase = AddStudentItemUseCase(repository)
    val getGroupsStartingWithUseCase = GetGroupsStartingWithUseCase(repository)
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
    private val _errorInputCorrectGroup = MutableLiveData<Boolean>()
    val errorInputCorrectGroup: LiveData<Boolean>
        get() = _errorInputCorrectGroup
    private val _registrationSucces = MutableLiveData<Boolean>()
    val registrationSucces: LiveData<Boolean>
        get() = _registrationSucces

    fun register(studentItem: StudentItem, repeatPassword: String) {
        val parsedStudentItem = parseStudent(studentItem)

        if (validateInput(parsedStudentItem, repeatPassword)) {
            val hashedStudent = parsedStudentItem.copy(
                password = hashPassword(parsedStudentItem.password)
            )
            _registrationSucces.value = true
            viewModelScope.launch {
                addStudentItemUseCase.addStudentItemUseCase(hashedStudent)
            }
        }
    }


    fun loadGroups(query: String) {
        viewModelScope.launch {
            val groups = getGroupsStartingWithUseCase.getGroupStartWith(query)
            cachedGroups = groups
            _groupsListLiveData.postValue(groups)
        }
    }

    private fun parseStudent(studentItem: StudentItem?): StudentItem {
        val parsedStudentItem = StudentItem(
            lastName = studentItem?.lastName?.trim() ?: "",
            surName = studentItem?.surName?.trim() ?: "",
            middleName = studentItem?.middleName?.trim() ?: "",
            group = studentItem?.group?.trim() ?: "",
            phone = studentItem?.phone?.trim() ?: "",
            email = studentItem?.email?.trim() ?: "",
            password = studentItem?.password?.trim() ?: ""
        )
        return parsedStudentItem
    }

    fun hashPassword(password: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(password.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    fun resetErrorFullName() {
        _errorInputFullName.value = false
    }
    fun resetErrorGroup(){
        _errorInputGroup.value = false
    }
    fun resetErrorPassword(){
        _errorInputPassword.value = false
    }
    fun resetErrorEmail(){
        _errorInputEmail.value = false
    }
    fun resetErrorRepeatPassword(){
        _errorRepeatPassword.value = false
    }
    fun resetErrorPhone(){
        _errorInputPhone.value = false
    }



    private fun validateInput(studentItem: StudentItem,repeatPassword: String): Boolean {
        var result = true
        if (studentItem.middleName.isBlank()
            || studentItem.surName.isBlank()
            || studentItem.lastName.isBlank()) {
            _errorInputFullName.value = true
            result = false
        }
        if (studentItem.group.isBlank()) {
            _errorInputGroup.value = true
            result = false
        }
        if (!cachedGroups.contains(studentItem.group)) {
            _errorInputCorrectGroup.value = true
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
        if (!isLatinPassword(studentItem.password)){
            _errorInputCorrectPassword.value = true
            result = false
        }
        if (repeatPassword != studentItem.password){
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