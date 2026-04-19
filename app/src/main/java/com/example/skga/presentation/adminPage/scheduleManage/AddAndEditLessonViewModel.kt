package com.example.skga.presentation.adminPage.scheduleManage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.local.AdminRepositoryImpl
import domain.entity.UserProfile
import domain.usecases.forAdmin.GetGroupsUseCase
import domain.usecases.forAdmin.GetTeacherListUseCase
import kotlinx.coroutines.launch

class AddAndEditLessonViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AdminRepositoryImpl(application)
    private val getGroupsUseCase = GetGroupsUseCase(repository)

    private val getTeacherListUseCase = GetTeacherListUseCase(repository)
    private val _groupList = MutableLiveData<List<String>>()
    val groupList: LiveData<List<String>>
        get() = _groupList

    private val _teachersList = MutableLiveData<List<UserProfile>>()
    val teacherList: LiveData<List<UserProfile>>
        get() = _teachersList



    fun loadGroups(){
        viewModelScope.launch {
            _groupList.value = getGroupsUseCase.getGroups()
        }
    }

    fun loadTeachers(){
        viewModelScope.launch {
            _teachersList.value = getTeacherListUseCase.getTeacherList().getOrNull()
        }
    }


}