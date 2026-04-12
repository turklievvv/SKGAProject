package com.example.skga.presentation.adminPage.scheduleManage

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import data.local.AdminRepositoryImpl
import domain.entity.ScheduleItem
import domain.usecases.forAdmin.GetGroupScheduleUseCase
import domain.usecases.forAdmin.GetGroupsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.Calendar

class ScheduleManageViewModel(application: Application) : AndroidViewModel(application) {

    private var searchJob: Job? = null

    var allLessons: List<ScheduleItem> = emptyList()

    private val repository = AdminRepositoryImpl(application)
    private val getScheduleUseCase = GetGroupScheduleUseCase(repository)

    private val getGroupsUseCase = GetGroupsUseCase(repository)

    private val sharedPrefs =
        application.getSharedPreferences("schedule_cache", Context.MODE_PRIVATE)
    private val gson = Gson()


    private val _scheduleList = MutableLiveData<List<ScheduleItem>>()
    val scheduleList: LiveData<List<ScheduleItem>>
        get() = _scheduleList
    private val _groupList = MutableLiveData<List<String>>()
    val groupList: LiveData<List<String>>
        get() = _groupList

    private val _group = MutableLiveData<String>()
    val group: LiveData<String>
        get() = _group


    fun getGroups() {
        viewModelScope.launch {
            _groupList.value = getGroupsUseCase.getGroups()
        }
    }

    fun getGroupSchedule(group: String, dayOfWeekCount: Int) {
        viewModelScope.launch {
            allLessons = getScheduleUseCase.getGroupSchedule(group).getOrNull() ?: emptyList()
            if (allLessons.isNotEmpty()) {
                saveScheduleListAndGroup(allLessons, group)
            }
            filterByDay(dayOfWeekCount)
        }

    }

    private fun saveScheduleListAndGroup(list: List<ScheduleItem>, group: String) {
        val json = gson.toJson(list)
        sharedPrefs.edit {
            putString("last_schedule", json)
            putString("last_group", group)
        }
    }

    fun loadScheduleFromCache(dayOfWeekCount: Int) {
        val json = sharedPrefs.getString("last_schedule", null)
        if (json != null) {
            val type = object : TypeToken<List<ScheduleItem>>() {}.type
            allLessons = gson.fromJson(json, type)
            filterByDay(dayOfWeekCount)
        }
        val jsonGroup = sharedPrefs.getString("last_group", null)
        if (jsonGroup != null) {
            _group.value = jsonGroup
        }
    }

    fun filterByDay(dayOfWeekCount: Int) {
        _scheduleList.value = allLessons
            .filter { it.dayOfWeek == dayOfWeekCount }
            .sortedBy { it.lessonNumber }
    }

    fun getDayOfWeekStringFromCalendar(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayString = when (dayOfWeek) {
            Calendar.MONDAY -> "Понедельник"
            Calendar.TUESDAY -> "Вторник"
            Calendar.WEDNESDAY -> "Среда"
            Calendar.THURSDAY -> "Четверг"
            Calendar.FRIDAY -> "Пятница"
            Calendar.SATURDAY -> "Суббота"
            Calendar.SUNDAY -> "Воскресенье"
            else -> "Понедельник"
        }
        return dayString
    }

    fun getDayOfWeekIntFromCalendar(): Int {
        val calendar = Calendar.getInstance()
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val dayString = when (dayOfWeek) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
        return dayString
    }


}