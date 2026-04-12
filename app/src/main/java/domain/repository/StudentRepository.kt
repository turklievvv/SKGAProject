package domain.repository

import domain.entity.AuthResult
import domain.entity.FacultyItem
import domain.entity.StudentItem
import domain.entity.StudentProfile

interface StudentRepository {


    suspend fun getGroups(): List<String>

    suspend fun getFaculties(): List<FacultyItem>

    suspend fun signUp(studentItem: StudentItem): Result<Unit>

    suspend fun login(email: String, password: String): Result<StudentProfile>
}