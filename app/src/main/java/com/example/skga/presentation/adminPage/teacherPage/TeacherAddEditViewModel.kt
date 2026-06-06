package com.example.skga.presentation.adminPage.teacherPage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.local.AdminRepositoryImpl
import data.local.StudentRepositoryImpl
import domain.entity.FacultyItem
import domain.entity.UserProfile
import domain.usecases.forAdmin.CreateTeacherProfileUseCase
import domain.usecases.forAdmin.UpdateProfileUseCase
import domain.usecases.forApp.GetFacultiesUseCase
import kotlinx.coroutines.launch

class TeacherAddEditViewModel(private val application: Application) :
    AndroidViewModel(application) {

    private val repository = AdminRepositoryImpl(application)

    private val studentRepository = StudentRepositoryImpl(application)
    private val updateProfile = UpdateProfileUseCase(repository)
    private val createTeacherProfileUseCase = CreateTeacherProfileUseCase(repository)

    private val getAllFacultiesUseCase = GetFacultiesUseCase(studentRepository)

    val _allFaculties = MutableLiveData<List<FacultyItem>>()
    val allFaculties: LiveData<List<FacultyItem>>
        get() = _allFaculties

    fun updateTeacher(profile: UserProfile){
        viewModelScope.launch {
            updateProfile.updateProfile(profile)
        }
    }

    fun inviteTeacher(profile: UserProfile){
        viewModelScope.launch {
            createTeacherProfileUseCase.createProfile(profile)
        }
    }

    fun getFaculties(){
        viewModelScope.launch {
            _allFaculties.value = getAllFacultiesUseCase.getFaculties()
        }
    }

}