package data.local

import android.app.Application
import data.api.SupabaseClient
import domain.entity.ScheduleItem
import domain.repository.AdminRepository
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
    override suspend fun getGroupSchedule(group: String): Result<List<ScheduleItem>> {
        return try {
            val student = sessionManager.studentProfile.first()
            val response = api.getScheduleByGroup(
                apiKey = supabaseClient.API_KEY,
                token = "Bearer ${student?.token}",
                group = "eq.$group"
            )
            val items = map.mapScheduleDtoToEntity(response)

            return Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}