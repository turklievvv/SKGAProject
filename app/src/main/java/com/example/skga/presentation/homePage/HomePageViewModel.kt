package com.example.skga.presentation.homePage

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

class HomePageViewModel(application: Application) : AndroidViewModel(application) {

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



    private val _nearestLesson = MutableLiveData<ScheduleItem>()
    val nearestLesson: LiveData<ScheduleItem>
        get() = _nearestLesson


    fun loadData() {
        viewModelScope.launch {
            val result = loadScheduleUseCase.loadScheduleForStudent()
            _scheduleList.value = result.getOrNull() ?: emptyList()
            val eventsResult = loadEventsForCurrentStudentUseCase.loadEvents()
            _eventList.value = eventsResult.getOrNull() ?: emptyList()
        }
    }

    fun findClosestLesson(): ScheduleItem? {
        val allLessons = _scheduleList.value ?: return null
        val now = Calendar.getInstance()

        val currentDay = if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) 7
        else now.get(Calendar.DAY_OF_WEEK) - 1

        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        return allLessons
            .filter { it.dayOfWeek == currentDay }
            .filter {
                val startTimeInMinutes = parseTimeToMinutes(it.lessonStartTime)
                startTimeInMinutes >= currentMinutes
            }
            .minByOrNull { parseTimeToMinutes(it.lessonStartTime) }
    }

    private fun parseTimeToMinutes(time: String): Int {
        return try {
            val parts = time.split(":")
            parts[0].toInt() * 60 + parts[1].toInt()
        } catch (e: Exception) {
            0
        }
    }

}