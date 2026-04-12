package domain.repository

import domain.entity.ScheduleItem

interface AdminRepository {

    suspend fun getGroupSchedule(group: String):Result<List<ScheduleItem>>

   suspend fun getGroups(): List<String>

}