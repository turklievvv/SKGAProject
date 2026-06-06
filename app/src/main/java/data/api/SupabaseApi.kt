package data.api

import data.dtoEntity.EventsDto
import data.dtoEntity.FacultyDto
import data.dtoEntity.GroupDto
import data.dtoEntity.ScheduleDto
import data.dtoEntity.UserProfileDto
import domain.entity.EventItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    @POST("rest/v1/profiles")
    suspend fun createProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Body profile: UserProfileDto
    )

    @GET("rest/v1/groups")
    suspend fun getGroups(
        @Header("apikey") apiKey: String,
        @Query("select") columns: String = "*" // Нам нужны только названия (ID)
    ): List<GroupDto>

    @GET("rest/v1/faculties")
    suspend fun getFaculties(
        @Header("apikey") apiKey: String,
        @Query("select") columns: String = "*"
    ): List<FacultyDto>

    @GET("rest/v1/profiles")
    suspend fun getStudentProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("id") id: String,
        @Query("select") select: String = "*"
    ): List<UserProfileDto>

    @GET("rest/v1/schedule")
    suspend fun getScheduleByGroup(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("group") group: String
    ): List<ScheduleDto>

    @GET("rest/v1/schedule")
    suspend fun getAllSchedule(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String
    ): List<ScheduleDto>

    @GET("rest/v1/events") // Твоя новая таблица
    suspend fun getEvents(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("or") filter: String? = null  // ← добавляем
    ): List<EventsDto>

    @GET("rest/v1/profiles")
    suspend fun getTeachers(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("role") roleFilter: String = "eq.teacher",
        @Query("select") select: String = "*"
    ): List<UserProfileDto>

    @GET("rest/v1/schedule")
    suspend fun getTeacherSchedule(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("teacher_id") teacherId: String,
        @Query("select") select: String = "*"
    ): List<ScheduleDto>

    @POST("rest/v1/events")
    suspend fun createEvent(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Body event: EventsDto
    )

    @PATCH("rest/v1/events")
    suspend fun updateEvent(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("id") id: String,
        @Body event: EventsDto
    )

    @POST("rest/v1/schedule")
    suspend fun createLesson(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Body lesson: ScheduleDto
    )

    @PATCH("rest/v1/schedule")
    suspend fun updateLesson(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("id") id: String,
        @Body lesson: ScheduleDto
    ): Response<Unit>
}