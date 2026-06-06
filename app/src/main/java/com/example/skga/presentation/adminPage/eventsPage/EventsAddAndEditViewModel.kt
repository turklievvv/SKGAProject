package com.example.skga.presentation.adminPage.eventsPage

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.api.FcmMessageContainer
import data.api.FcmMessageDTO
import data.api.FcmTokenProvider
import data.api.FirebaseClient
import data.local.AdminRepositoryImpl
import data.local.StudentRepositoryImpl
import domain.entity.EventItem
import domain.entity.FacultyItem
import domain.entity.GroupItem
import domain.usecases.forAdmin.CreateEventUseCase
import domain.usecases.forAdmin.GetGroupsUseCase
import domain.usecases.forAdmin.UpdateEventUseCase
import domain.usecases.forApp.GetFacultiesUseCase
import kotlinx.coroutines.launch

class EventsAddAndEditViewModel(private val application: Application) :
    AndroidViewModel(application) {

    private val repository = AdminRepositoryImpl(application)
    private val firebaseClient = FirebaseClient
    private val studentRepository = StudentRepositoryImpl(application)
    private val getGroupsUseCase = GetGroupsUseCase(repository)
    private val getFacultiesUseCase = GetFacultiesUseCase(studentRepository)
    private val createEventUseCase = CreateEventUseCase(repository)
    private val fcmTokenProvider = FcmTokenProvider()
    private var allGroupsList: List<GroupItem> = emptyList()
    private val _facultyList = MutableLiveData<List<FacultyItem>>()
    val facultyList: LiveData<List<FacultyItem>>
        get() = _facultyList

    private val _filteredGroups = MutableLiveData<List<String>>()
    val filteredGroups: LiveData<List<String>>
        get() = _filteredGroups

    private val _saveStatus = MutableLiveData<Boolean>()
    val saveStatus: LiveData<Boolean> = _saveStatus
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val updateEventUseCase = UpdateEventUseCase(repository)

    private var isEditMode = false
    private var currentEventId: String? = null

    fun setEditMode(eventItem: EventItem) {
        isEditMode = true
        currentEventId = eventItem.id
    }

    init {
        getGroups()
        getFaculties()
    }

    fun getGroups() {
        viewModelScope.launch {
            val groups = getGroupsUseCase.getGroups()
            allGroupsList = groups
            _filteredGroups.value = groups.map { it.id }
        }
    }

    fun getFaculties() {
        viewModelScope.launch {
            val faculties = getFacultiesUseCase.getFaculties()
            _facultyList.value = faculties
        }
    }

    fun createEvent(eventItem: EventItem) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = if (isEditMode) {
                updateEventUseCase(eventItem)
            } else {
                createEventUseCase(eventItem)
            }
            if (result.isSuccess) {
                sendNotificationToMultipleTargets(
                    facultyIds = eventItem.eventFaculties ?: emptyList(),
                    groupNames = eventItem.eventGroups ?: emptyList(),
                    isGlobal = eventItem.eventIsGlobal,
                    isTeachers = eventItem.eventIsTeachers,
                    title = "Новое событие ${eventItem.eventName}",
                    body = "В ${eventItem.eventDate} ${eventItem.eventTime} в ${eventItem.eventLocation}"
                )
            }
            _saveStatus.value = result.isSuccess
            _isLoading.value = false
        }
    }

    fun filterGroupsByMultipleFaculties(facultyIds: List<Int>) {
        _filteredGroups.value = allGroupsList
            .filter { group -> facultyIds.contains(group.facultyId) }
            .map { it.id }
    }

    fun showAllGroups() {
        _filteredGroups.value = allGroupsList.map { it.id }
    }


    suspend fun sendNotificationToMultipleTargets(
        facultyIds: List<Int>,
        groupNames: List<String>,
        isGlobal: Boolean,      // Добавляем флаг "Весь институт"
        isTeachers: Boolean,    // Добавляем флаг "Все учителя"
        title: String,
        body: String
    ) {
        try {
            val dataPayload = mapOf(
                "title" to title,
                "body" to body
            )

            val topicsList = mutableListOf<String>()
            var pureTopic: String? = null

            // 1️⃣ ПРОВЕРКА ГЛОБАЛЬНЫХ ТОПИКОВ
            if (isGlobal) {
                // Если выбран весь институт, шлем в один общий топик
                pureTopic = "all_institute"
            } else if (isTeachers) {
                // Если только учителя — в топик учителей
                pureTopic = "teachers"
            } else {
                // 2️⃣ СБОР ОГРАНИЧЕННЫХ ТОПИКОВ (Если глобальные флаги не стоят)
                facultyIds.forEach { id ->
                    topicsList.add("'faculty_$id' in topics")
                }
                groupNames.forEach { name ->
                    if (name.isNotBlank()) {
                        topicsList.add("'group_$name' in topics")
                    }
                }
            }

            // Формируем контейнер запроса в зависимости от условий
            val requestBody = when {
                // Вариант А: Отправка в один конкретный топик (all_institute, teachers или если выбран всего 1 факультет/группа)
                pureTopic != null -> {
                    FcmMessageContainer(
                        message = FcmMessageDTO(
                            topic = pureTopic,
                            data = dataPayload
                        )
                    )
                }

                topicsList.size == 1 -> {
                    val singleCondition = topicsList.first()
                    // Извлекаем чистое имя топика из строки "'topic_name' in topics"
                    val cleanedTopic = singleCondition.substringAfter("'").substringBefore("'")
                    FcmMessageContainer(
                        message = FcmMessageDTO(
                            topic = cleanedTopic,
                            data = dataPayload
                        )
                    )
                }
                // Вариант Б: Выбрано несколько таргетов -> объединяем через ИЛИ (||)
                topicsList.isNotEmpty() -> {
                    val conditionExpression = topicsList.joinToString(" || ")
                    FcmMessageContainer(
                        message = FcmMessageDTO(
                            condition = conditionExpression,
                            data = dataPayload
                        )
                    )
                }

                else -> {
                    Log.e("FCM_ADMIN", "Не выбрано ни одного получателя для пуша")
                    return
                }
            }

            val projectId = "skga-notifications"
            val oauth2Token = fcmTokenProvider.getValidAccessToken(application)
            Log.d("TOKEN",oauth2Token)

            val response = firebaseClient.api.sendDataNotification(
                projectId = projectId,
                bearerToken = "Bearer $oauth2Token",
                messageContainer = requestBody
            )

            if (response.isSuccessful) {
                Log.d(
                    "FCM_ADMIN",
                    "Пуш успешно отправлен! Условие/Топик: ${requestBody.message.condition ?: requestBody.message.topic}"
                )
            } else {
                Log.e("FCM_ADMIN", "Ошибка Firebase: ${response.errorBody()?.string()}")
            }

        } catch (e: Throwable) {
            Log.e("FCM_ADMIN", "Сбой сети при отправке пуша", e)
        }
    }
}
