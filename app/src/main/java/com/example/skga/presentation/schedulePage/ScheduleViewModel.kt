package com.example.skga.presentation.schedulePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.local.StudentRepositoryImpl
import domain.entity.DayConfig
import domain.entity.EventItem
import domain.entity.ScheduleItem
import domain.usecases.forApp.LoadEventsForCurrentStudentUseCase
import domain.usecases.forApp.LoadScheduleForCurrentStudentUseCase
import kotlinx.coroutines.launch
import java.util.Calendar

class ScheduleViewModel(application: Application): AndroidViewModel(application) {

    val repository = StudentRepositoryImpl(application)

    val loadScheduleUseCase = LoadScheduleForCurrentStudentUseCase(repository)

    val loadEventsForCurrentStudentUseCase = LoadEventsForCurrentStudentUseCase(repository)

    val daysList: List<DayConfig> by lazy { repository.generateDaysList(14) }
    private val _scheduleList = MutableLiveData<List<ScheduleItem>>()
    val scheduleList: LiveData<List<ScheduleItem>>
        get() = _scheduleList

    private val _eventList = MutableLiveData<List<EventItem>>()
    val eventList: LiveData<List<EventItem>>
        get() = _eventList


    fun loadData() {
        viewModelScope.launch {
            val result = loadScheduleUseCase.loadScheduleForStudent()
            _scheduleList.value = result.getOrNull() ?: emptyList()
            val eventsResult = loadEventsForCurrentStudentUseCase.loadEvents()
            _eventList.value = eventsResult.getOrNull() ?: emptyList()
        }
    }

}

