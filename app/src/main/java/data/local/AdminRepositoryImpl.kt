package data.local

import android.app.Application
import android.util.Log
import data.api.SupabaseClient
import domain.entity.EventItem
import domain.entity.GroupItem
import domain.entity.ScheduleItem
import domain.entity.UserProfile
import domain.repository.AdminRepository
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.first

class AdminRepositoryImpl(application: Application) : AdminRepository {

    private val map = Mapper()
    private val api = SupabaseClient.api
    private val supabaseClient = SupabaseClient

    private val sessionManager = UserSessionManager(application)

    private var cachedTeachers: List<UserProfile>? = null

    override suspend fun getGroups(): List<GroupItem> {
        return try {
            val list = api.getGroups(SupabaseClient.API_KEY)
            val items = list.map { map.mapGroupDtoToEntity(it) }
            return items
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    fun clearTeachersCache() {
        cachedTeachers = null
    }

    override suspend fun getTeachersList(): Result<List<UserProfile>> {
        return runCatching {
            cachedTeachers?.let { return Result.success(it) }
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            Log.d("AUTH_DEBUG", "Отправляю токен: Bearer $currentToken")
            val student = sessionManager.userProfile.first() // если этот flow пустой, может зависнуть, но в try поймается
            val list = api.getTeachers(
                SupabaseClient.API_KEY,
                "Bearer $currentToken"
            )

            val mappedList = list.map { map.mapProfileDtoToEntity(it) }
            cachedTeachers = mappedList
            mappedList
        }.onFailure { throwable ->
            Log.e("REPO_ERROR", "Ошибка в getTeachersList: ${throwable.message}")
            throwable.printStackTrace()
        }
    }

    override suspend fun getTeacherLessons(teacherId: String): Result<List<ScheduleItem>> {
        return runCatching {
            val teacherList = cachedTeachers ?: getTeachersList().getOrNull() ?: emptyList()
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            val list = api.getTeacherSchedule(
                SupabaseClient.API_KEY,
                "Bearer $currentToken",
                teacherId
            )
            map.mapScheduleDtoToEntity(list, teacherList)
        }.onFailure {
            it.printStackTrace()
        }
    }

    override suspend fun getAllLessons(): Result<List<ScheduleItem>> {
        // ОБЯЗАТЕЛЬНО пишем return перед runCatching!
        return runCatching {
            val teacherList = cachedTeachers ?: getTeachersList().getOrNull() ?: emptyList()
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            val response = api.getAllSchedule(
                apiKey = supabaseClient.API_KEY,
                token = "Bearer $currentToken",
            )

            map.mapScheduleDtoToEntity(response, teacherList)
        }.onFailure {
            Log.e("REPO_ERROR", "Таймаут или ошибка сети в getAllLessons")
            it.printStackTrace()
        }
    }

    override suspend fun getGroupSchedule(group: String): Result<List<ScheduleItem>> {
        return runCatching {
            val teacherList = cachedTeachers ?: getTeachersList().getOrNull() ?: emptyList()
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            Log.d("AUTH_DEBUG", "Отправляю токен: Bearer $currentToken")
            val response = api.getScheduleByGroup(
                apiKey = supabaseClient.API_KEY,
                token = "Bearer $currentToken",
                group = "eq.$group"
            )
            map.mapScheduleDtoToEntity(response, teacherList)
        }.onFailure {
            it.printStackTrace()
        }
    }


    override suspend fun updateProfile(profile: UserProfile): Result<Unit> {
        return runCatching {
            SupabaseClient.client.from("profiles").update(profile) {
                filter {
                    eq("id", profile.id)
                }
            }
            clearTeachersCache()
        }
    }

    override suspend fun createTeacherProfile(profile: UserProfile): Result<Unit> {
        return runCatching {
            val adminKey = SupabaseClient.ADMIN_KEY
            val cleanEmail = profile.email.trim()

            Log.d("REPO_DEBUG", "1. Отправляем инвайт через Retrofit...")

            val inviteResponse = SupabaseClient.api.inviteUser(
                apiKey = adminKey,
                token = "Bearer $adminKey",
                body = mapOf(
                    "email" to cleanEmail,
                    "redirectTo" to "skga://reset-password"
                )
            )

            Log.d("REPO_DEBUG", "Код ответа Auth: ${inviteResponse.code()}")

            if (!inviteResponse.isSuccessful) {
                val authError = inviteResponse.errorBody()?.string()
                throw Exception("Supabase Auth вернул ошибку, код: ${inviteResponse.code()}. Тело: $authError")
            }

            // 2. Читаем строку ответа из ResponseBody (метод .string() можно вызвать только ОДИН раз!)
            val responseString = inviteResponse.body()?.string() ?: ""
            Log.d("REPO_DEBUG", "Ответ сервера Auth: $responseString")

            if (responseString.isBlank()) {
                throw Exception("Сервер вернул пустой ответ")
            }

            // Парсим JSON и забираем настоящий UUID пользователя
            val jsonObject = org.json.JSONObject(responseString)
            val serverUserId = jsonObject.getString("id")
            Log.d("REPO_DEBUG", "Успешно получен ID от сервера: $serverUserId")

            // 3. Маппим DTO, подставляя НАСТОЯЩИЙ ID от сервера
            val profileDto = map.mapTeacherEntityToProfileDto(
                profile,
                serverUserId,
                profile.facultyId ?: 1
            )

            Log.d("REPO_DEBUG", "3. Записываем в pending_profiles...")

            // 4. Пишем в таблицу базы данных
            val dbResponse = SupabaseClient.api.createPendingProfile(
                apiKey = adminKey,
                token = "Bearer $adminKey",
                profile = profileDto
            )

            if (!dbResponse.isSuccessful) {
                val errorBody = dbResponse.errorBody()?.string()
                throw Exception("Ошибка записи в pending_profiles: $errorBody")
            }

            clearTeachersCache()
            Log.d("REPO_DEBUG", "Готово! Учитель успешно создан и ожидает регистрации.")
            Unit
        }.onFailure { throwable ->
            if (throwable is kotlinx.coroutines.CancellationException) throw throwable
            Log.e("REPO_ERROR", "Ошибка при создании учителя: ${throwable.message}")
        }
    }
    override suspend fun getEventsList(): Result<List<EventItem>> {
        return try {
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            Log.d("AUTH_DEBUG", "Отправляю токен: Bearer $currentToken")
            val responseEvents = api.getEvents(
                SupabaseClient.API_KEY,
                currentToken ?: "",
            )

            val item = responseEvents.map { eventsDto -> map.mapEventsDtoToEntity(eventsDto) }
            Log.d("EVENTS_ITEM" , "Список  $item")
            Result.success(item)
        } catch (e: Exception) {
            Log.d("EVENTS_ITEM" , "Ошибка $e")
            Result.failure(e)
        }
    }

    override suspend fun createEvent(event: EventItem): Result<Unit> {
        return runCatching {
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            val response = api.createEvent(
                apiKey = supabaseClient.API_KEY,
                token = "Bearer $currentToken",
                event = map.mapEventsEntityToDto(event)
            )
            Unit
        }.onFailure {exception ->
            if (exception is retrofit2.HttpException) {
                val errorBody = exception.response()?.errorBody()?.string()
                // 🚀 Этот лог покажет точную фразу, например: "column event_faculties is of type jsonb but expression is of type..."
                Log.e("REPO_ERROR", "Детали ошибки Supabase: $errorBody")
            } else {
                Log.e("REPO_ERROR", "Ошибка: ${exception.message}")
            }        }
    }

    override suspend fun updateEvent(event: EventItem): Result<Unit> {
        return runCatching {
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            api.updateEvent(
                apiKey = supabaseClient.API_KEY,
                token = "Bearer $currentToken",
                id = "eq.${event.id}",
                event = map.mapEventsEntityToDto(event)
            )
        }.onFailure {
            Log.e("REPO_ERROR", "Ошибка при обновлении события: ${it.message}")
        }
    }

    override suspend fun createLesson(lesson: ScheduleItem): Result<Unit> {
        return runCatching {
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            api.createLesson(
                apiKey = supabaseClient.API_KEY,
                token = "Bearer $currentToken",
                lesson = map.mapScheduleEntityToDto(lesson)
            )
        }.onFailure {
            Log.e("REPO_ERROR", "Ошибка при создании урока: ${it.message}")
        }
    }

    override suspend fun updateLesson(lesson: ScheduleItem): Result<Unit> {
        return runCatching {
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            Log.d("REPO_DEBUG", "Пытаемся обновить урок с ID: ${lesson.id}")
            Log.d("REPO_DEBUG", "Тело запроса (Dto): ${map.mapScheduleEntityToDto(lesson)}")
            val response = api.updateLesson(
                apiKey = supabaseClient.API_KEY,
                token = "Bearer $currentToken",
                id = "eq.${lesson.id}",
                lesson = map.mapScheduleEntityToDto(lesson)
            )
            Log.d("REPO_DEBUG", "Код ответа сервера: ${response.code()}")

            if (response.isSuccessful) {
                val responseBody = response.body()?.string()
                Log.d("REPO_DEBUG", "Что на самом деле ответила база: $responseBody")

                if (responseBody == "[]" || responseBody.isNullOrBlank()) {
                    Log.e("REPO_ERROR", "Внимание: Обновлено 0 строк! Проверь RLS или существование ID в базе.")
                }
            }
        }.onFailure {
            Log.e("REPO_ERROR", "Ошибка при обновлении урока: ${it.message}")
        }
    }
}
