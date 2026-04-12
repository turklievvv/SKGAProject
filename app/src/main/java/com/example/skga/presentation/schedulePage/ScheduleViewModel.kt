package com.example.skga.presentation.schedulePage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.local.StudentRepositoryImpl
import domain.entity.DayConfig
import domain.entity.ScheduleItem
import kotlinx.coroutines.launch
import java.util.Calendar

class ScheduleViewModel(application: Application): AndroidViewModel(application) {

    val repository = StudentRepositoryImpl(application)

    val daysList: List<DayConfig> by lazy { repository.generateDaysList(60) }
    private val _scheduleList = MutableLiveData<List<ScheduleItem>>()
    val scheduleList: LiveData<List<ScheduleItem>>
        get() = _scheduleList


    fun loadData() {
        viewModelScope.launch {
            val result = repository.loadScheduleForCurrentStudent()
            _scheduleList.value = result.getOrNull() ?: emptyList()
        }
    }

}

