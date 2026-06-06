package com.example.skga.presentation.adminPage.scheduleManage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.local.AdminRepositoryImpl
import domain.entity.ScheduleItem
import domain.entity.UserProfile
import domain.usecases.forAdmin.CreateLessonUseCase
import domain.usecases.forAdmin.GetGroupsUseCase
import domain.usecases.forAdmin.GetTeacherListUseCase
import domain.usecases.forAdmin.UpdateLessonUseCase
import kotlinx.coroutines.launch

class AddAndEditLessonViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AdminRepositoryImpl(application)
    private val getGroupsUseCase = GetGroupsUseCase(repository)
    private val getTeacherListUseCase = GetTeacherListUseCase(repository)
    private val createLessonUseCase = CreateLessonUseCase(repository)
    private val updateLessonUseCase = UpdateLessonUseCase(repository)

    private var isEditMode = false

    private val _groupList = MutableLiveData<List<String>>()
    val groupList: LiveData<List<String>> get() = _groupList

    private val _teachersList = MutableLiveData<List<UserProfile>>()
    val teacherList: LiveData<List<UserProfile>> get() = _teachersList

    private val _saveResult = MutableLiveData<Result<Unit>>()
    val saveResult: LiveData<Result<Unit>> get() = _saveResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setEditMode(isEdit: Boolean) {
        isEditMode = isEdit
    }

    fun loadGroups() {
        viewModelScope.launch {
            _groupList.value = getGroupsUseCase.getGroups().map { it.id }
        }
    }

    fun loadTeachers() {
        viewModelScope.launch {
            _teachersList.value = getTeacherListUseCase.getTeacherList().getOrNull() ?: emptyList()
        }
    }

    fun saveLesson(lesson: ScheduleItem) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = if (isEditMode) {
                updateLessonUseCase(lesson)
            } else {
                createLessonUseCase(lesson)
            }
            _saveResult.value = result
            _isLoading.value = false
        }
    }
}