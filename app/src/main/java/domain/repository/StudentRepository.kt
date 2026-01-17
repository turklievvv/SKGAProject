package domain.repository

import androidx.lifecycle.LiveData
import domain.entity.GroupsItem
import domain.entity.StudentItem

interface StudentRepository {

    suspend fun addStudentItem(studentItem: StudentItem)

    suspend fun deleteStudentItem(studentItem: StudentItem)

    suspend fun editStudentItem(studentItem: StudentItem)

    suspend fun getStudent(studentId: Int): StudentItem

    suspend fun getStudentList(): List<StudentItem>

    suspend fun getUserByLogin(emailOrPhone: String): StudentItem?

    suspend fun addNewGroup(group: GroupsItem)

    suspend fun getGroupsStartingWith(query: String): List<String>

}