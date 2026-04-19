package data.local

import android.app.Application
import android.util.Log
import data.api.SupabaseClient
import domain.entity.ScheduleItem
import domain.entity.UserProfile
import domain.repository.AdminRepository
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.first

class AdminRepositoryImpl(application: Application) : AdminRepository {

    private val map = Mapper()
    private val api = SupabaseClient.api
    private val supabaseClient = SupabaseClient
    private val sessionManager = UserSessionManager(application)

    // Защищаем группы. Если нет сети — вернем пустой список, чтобы не крашить
    override suspend fun getGroups(): List<String> {
        return try {
            val list = api.getGroups(SupabaseClient.API_KEY)
            list.map { it.id }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Упаковываем в runCatching, чтобы сдержать SocketTimeoutException
    override suspend fun getTeachersList(): Result<List<UserProfile>> {
        return runCatching {
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            Log.d("AUTH_DEBUG", "Отправляю токен: Bearer $currentToken")
            val student = sessionManager.userProfile.first() // если этот flow пустой, может зависнуть, но в try поймается
            val list = api.getTeachers(
                SupabaseClient.API_KEY,
                "Bearer $currentToken"
            )
            list.map { map.mapProfileDtoToEntity(it) }
        }.onFailure { throwable ->
            Log.e("REPO_ERROR", "Ошибка в getTeachersList: ${throwable.message}")
            throwable.printStackTrace()
        }
    }

    override suspend fun getTeacherLessons(teacherId: String): Result<List<ScheduleItem>> {
        return runCatching {
            val teacherList = getTeachersList().getOrNull() ?: emptyList()
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
            val teacherList = getTeachersList().getOrNull() ?: emptyList()
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            val response = api.getAllSchedule(
                apiKey = supabaseClient.API_KEY,
                token = "Bearer $currentToken",
            )

            // Последней строчкой возвращаем чистый отмаппленный список объектов.
            // runCatching САМ обернет этот список в Result.success(items)
            map.mapScheduleDtoToEntity(response, teacherList)
        }.onFailure {
            Log.e("REPO_ERROR", "Таймаут или ошибка сети в getAllLessons")
            it.printStackTrace()
        }
    }

    override suspend fun getGroupSchedule(group: String): Result<List<ScheduleItem>> {
        return runCatching {
            val teacherList = getTeachersList().getOrNull() ?: emptyList()
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
}