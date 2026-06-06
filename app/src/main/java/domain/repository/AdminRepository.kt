package domain.repository

import domain.entity.EventItem
import domain.entity.GroupItem
import domain.entity.ScheduleItem
import domain.entity.UserProfile

interface AdminRepository {

    suspend fun getGroupSchedule(group: String):Result<List<ScheduleItem>>

   suspend fun getGroups(): List<GroupItem>

    suspend fun getTeachersList(): Result<List<UserProfile>>

    suspend fun getTeacherLessons(teacherId: String): Result<List<ScheduleItem>>

    suspend fun getAllLessons(): Result<List<ScheduleItem>>

    suspend fun updateProfile(profile: UserProfile): Result<Unit>

    suspend fun createTeacherProfile(profile: UserProfile): Result<Unit>

    suspend fun getEventsList(): Result<List<EventItem>>

    suspend fun createEvent(event: EventItem): Result<Unit>

    suspend fun updateEvent(event: EventItem): Result<Unit>

    suspend fun createLesson(lesson: ScheduleItem): Result<Unit>

    suspend fun updateLesson(lesson: ScheduleItem): Result<Unit>

}