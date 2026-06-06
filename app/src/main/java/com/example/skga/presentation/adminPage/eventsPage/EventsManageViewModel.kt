package com.example.skga.presentation.adminPage.eventsPage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.local.AdminRepositoryImpl
import domain.entity.EventItem
import domain.usecases.forAdmin.GetEventsUseCase
import kotlinx.coroutines.launch

class EventsManageViewModel(private val application: Application) : AndroidViewModel(application) {

    private val repository = AdminRepositoryImpl(application)
    private val getEventsUseCase = GetEventsUseCase(repository)

    private val _eventList = MutableLiveData<List<EventItem>>()
    val eventList: LiveData<List<EventItem>>
        get() = _eventList

    fun getEventsList(){
        viewModelScope.launch {
            val list = getEventsUseCase.getEventsList().getOrNull()?:emptyList()
            _eventList.value = list
        }
    }

}