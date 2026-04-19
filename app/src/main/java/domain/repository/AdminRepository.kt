package domain.repository

import domain.entity.ScheduleItem
import domain.entity.UserProfile

interface AdminRepository {

    suspend fun getGroupSchedule(group: String):Result<List<ScheduleItem>>

   suspend fun getGroups(): List<String>

    suspend fun getTeachersList(): Result<List<UserProfile>>

    suspend fun getTeacherLessons(teacherId: String): Result<List<ScheduleItem>>

    suspend fun getAllLessons(): Result<List<ScheduleItem>>

}