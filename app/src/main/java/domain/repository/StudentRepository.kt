package domain.repository

import domain.entity.EventItem
import domain.entity.FacultyItem
import domain.entity.ScheduleItem
import domain.entity.StudentItem
import domain.entity.UserProfile

interface StudentRepository {


    suspend fun getGroups(): List<String>

    suspend fun getFaculties(): List<FacultyItem>

    suspend fun signUp(studentItem: StudentItem): Result<Unit>

    suspend fun login(email: String, password: String): Result<UserProfile>

    suspend fun loadScheduleForStudent():Result<List<ScheduleItem>>

    suspend fun loadEventForStudent():Result<List<EventItem>>

    suspend fun uploadAvatar(fileBytes: ByteArray, fileName: String): String?

    suspend fun updateAvatarUrl(studentId: String, imageUrl: String)
}