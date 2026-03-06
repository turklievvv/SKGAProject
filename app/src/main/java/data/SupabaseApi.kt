package data

import domain.entity.ScheduleItem
import domain.entity.StudentProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {

    // 1. Регистрация нового пользователя (Email + Password)
    @POST("auth/v1/signup")
    suspend fun signUp(
        @Header("apikey") apiKey: String,
        @Body body: SignUpRequest
    ): SignUpResponse

    // 2. Вход (Email + Password)
    @POST("auth/v1/token?grant_type=password")
    suspend fun login(
        @Header("apikey") apiKey: String,
        @Body body: SignUpRequest
    ): SignUpResponse

    // 3. Создание записи в нашей таблице profiles после регистрации
    @POST("rest/v1/profiles")
    suspend fun createProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Body profile: StudentProfileDto
    )

    @GET("rest/v1/groups")
    suspend fun getGroups(
        @Header("apikey") apiKey: String,
        @Query("select") columns: String = "id" // Нам нужны только названия (ID)
    ): List<GroupDto>

    @GET("rest/v1/faculties")
    suspend fun getFaculties(
        @Header("apikey") apiKey: String,
        @Query("select") columns: String = "name"
    ): List<FacultyDto>

    @GET("rest/v1/profiles")
    suspend fun getStudentProfile(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("id") id: String,
        @Query("select") select: String = "*"
    ): List<StudentProfileDto>

    @GET("rest/v1/schedule")
    suspend fun getScheduleByGroup(
        @Header("apikey") apiKey: String,
        @Header("Authorization") token: String,
        @Query("group_id") groupId: String
    ): List<ScheduleDto>
}