package com.example.skga.presentation.adminPage.teacherPage

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.local.AdminRepositoryImpl
import domain.entity.ScheduleItem
import domain.entity.TeacherItem
import domain.entity.UserProfile
import domain.usecases.forAdmin.GetAllLessonsUseCase
import domain.usecases.forAdmin.GetTeacherListUseCase
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
class TeacherManageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AdminRepositoryImpl(application)
    private val getTeacherListUseCase = GetTeacherListUseCase(repository)
    private val getAllLessonsUseCase = GetAllLessonsUseCase(repository)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading
    private val _teachersUserProfile = MutableLiveData<List<UserProfile>>()

    private val _teacherItemList = MutableLiveData<List<TeacherItem>>()
    val teacherItemList: LiveData<List<TeacherItem>>
        get() = _teacherItemList

    val searchQuery = MutableLiveData("")
    private var allLessons: List<ScheduleItem> = emptyList()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // Логируем ошибку в консоль, чтобы видеть её, но не падать
        throwable.printStackTrace()

        // Передаем сообщение в Toast и запускаем перезапуск
        _isLoading.postValue(false)
        // Запускаем новый круг загрузки, так как этот упал
        viewModelScope.launch {
            delay(10000)
            loadInitialData()
        }
    }

    private val _teacherFilteredItems = MediatorLiveData<List<TeacherItem>>().apply {
        // Следим за обновлением основного списка учителей
        addSource(_teacherItemList) { teachers ->
            value = filterData(teachers, searchQuery.value ?: "")
        }
        // Следим за вводом текста в поисковую строку
        addSource(searchQuery) { query ->
            value = filterData(_teacherItemList.value ?: emptyList(), query)
        }
    }
    val teacherFilteredItems: LiveData<List<TeacherItem>>
        get() = _teacherFilteredItems

    init {
        loadInitialData()
    }

    private fun filterData(teachers: List<TeacherItem>, query: String): List<TeacherItem> {

        if (query.isBlank()) return teachers
        val cleanQuery = query.trim()

        return teachers.filter { teacher ->
            // Поиск по имени/фамилии
            val matchesName = teacher.userProfile.lastName.contains(
                cleanQuery,
                ignoreCase = true
            ) || teacher.userProfile.firstName.contains(cleanQuery, ignoreCase = true)

            val matchesSubject =
                teacher.teacherLessons.any { it.lessonName.contains(cleanQuery, ignoreCase = true) }

            // Поиск по группам
            val matchesGroup =
                teacher.teacherGroups.any { it.contains(cleanQuery, ignoreCase = true) }

            // Если хоть один критерий совпал — оставляем преподавателя
            matchesName || matchesSubject || matchesGroup
        }
    }

    fun getTeacherList() {
        viewModelScope.launch {
            val monthWeeks = getWeeksInCurrentMonth()
            val allTeachers = getTeacherListUseCase.getTeacherList().getOrNull()
            _teachersUserProfile.value = allTeachers ?: emptyList()
            if (allTeachers == null) {
                _teacherItemList.value = emptyList()
                return@launch
            }
            val teacherItem: List<TeacherItem> = allTeachers.map { teacher ->
                val allTeacherLessons = allLessons.filter { it.lessonTeacherId == teacher.id }

                val totalLessonsThisMonth = allTeacherLessons.sumOf { lesson ->
                    when (lesson.weekType) {
                        0 -> monthWeeks.oddWeeksCount + monthWeeks.evenWeeksCount
                        1 -> monthWeeks.oddWeeksCount
                        2 -> monthWeeks.evenWeeksCount
                        else -> 0
                    }
                }

                TeacherItem(
                    userProfile = teacher,
                    lessonCount = allTeacherLessons.distinctBy { it.id }.size,
                    totalHours = (totalLessonsThisMonth * 1.5).roundToInt(),
                    weekLessonCount = totalLessonsThisMonth,
                    teacherGroups = getTeacherGroups(teacher.id),
                    teacherLessons = allLessons
                )
            }
            _teacherItemList.value = teacherItem
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch(exceptionHandler) {
            _isLoading.value = true // Включаем полоску загрузки сверху
            try {
                val lessonsResult = getAllLessonsUseCase.getAllLessons()
                allLessons = lessonsResult.getOrNull() ?: emptyList()
                val allTeachers = getTeacherListUseCase.getTeacherList().getOrNull()

                if (allTeachers != null && allLessons.isNotEmpty()) {
                    getTeacherList()
                    _isLoading.value = false
                } else {
                    throw Exception("Empty data")
                }

            } catch (e: Exception) {
                _isLoading.value = false
                delay(10000)
                loadInitialData()
            }
        }
    }


    private fun getTeacherGroups(teacherId: String): List<String> {
        return allLessons.filter { it.lessonTeacherId == teacherId }.map { it.group }.distinct()
    }

    private fun getAllLessons() {
        viewModelScope.launch {
            allLessons = getAllLessonsUseCase.getAllLessons().getOrNull() ?: emptyList()
        }
    }


    data class MonthWeeksInfo(val oddWeeksCount: Int, val evenWeeksCount: Int)

    fun getWeeksInCurrentMonth(): MonthWeeksInfo {
        val now = LocalDate.now()
        val yearMonth = YearMonth.of(now.year, now.month)

        var oddCount = 0
        var evenCount = 0
        val weekFields = WeekFields.of(Locale.getDefault())
        val seenWeeks = mutableSetOf<Int>()

        for (day in 1..yearMonth.lengthOfMonth()) {
            val date = yearMonth.atDay(day)
            val weekOfYear = date.get(weekFields.weekOfWeekBasedYear())
            if (seenWeeks.add(weekOfYear)) {
                if (weekOfYear % 2 == 0) evenCount++ else oddCount++
            }
        }

        return MonthWeeksInfo(oddWeeksCount = oddCount, evenWeeksCount = evenCount)
    }

}