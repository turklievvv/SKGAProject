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

    override suspend fun getGroups(): List<String> {
        val list = api.getGroups(SupabaseClient.API_KEY)
        return list.map { it.id }
    }

    override suspend fun getTeachersList(): Result<List<UserProfile>> {
        val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
        Log.d("AUTH_DEBUG", "Отправляю токен: Bearer $currentToken")
        val student = sessionManager.userProfile.first()
        val list = api.getTeachers(
            SupabaseClient.API_KEY,
            "Bearer $currentToken"
        )
        val items = list.map {
            map.mapProfileDtoToEntity(it)
        }
        return Result.success(items)
    }

    override suspend fun getGroupSchedule(group: String): Result<List<ScheduleItem>> {
        return try {
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            Log.d("AUTH_DEBUG", "Отправляю токен: Bearer $currentToken")
            val student = sessionManager.userProfile.first()
            val response = api.getScheduleByGroup(
                apiKey = supabaseClient.API_KEY,
                token = "Bearer $currentToken",
                group = "eq.$group"
            )
            val items = map.mapScheduleDtoToEntity(response)

            return Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}