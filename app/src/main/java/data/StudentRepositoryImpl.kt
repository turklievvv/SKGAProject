package data

import android.app.Application
import androidx.lifecycle.LiveData
import domain.entity.GroupsItem
import domain.entity.StudentItem
import domain.repository.StudentRepository
import java.security.MessageDigest

class StudentRepositoryImpl(application: Application) : StudentRepository {

    private val studentsDao = AppDataBase.getInstance(application).studentListDao()
    private val groupsDao = GroupsAppDatabase.getInstance(application).groupsDao()

    private val mapper = StudentsMapper()
    private val groupMapper = GroupsMapper()

    override suspend fun addStudentItem(studentItem: StudentItem) {
        studentsDao.addStudentItem(mapper.mapEntityToDbModel(studentItem))
    }

    override suspend fun deleteStudentItem(studentItem: StudentItem) {
        studentsDao.deleteStudentItem(mapper.mapEntityToDbModel(studentItem).id)
    }

    override suspend fun editStudentItem(studentItem: StudentItem) {
        studentsDao.addStudentItem(mapper.mapEntityToDbModel(studentItem))
    }

    override suspend fun getStudent(studentId: Int): StudentItem {
        val dbModel = studentsDao.getStudentItem(studentId)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override suspend fun getStudentList(): List<StudentItem> {
        return mapper.mapDbModelListToEntityList(studentsDao.getStudentItemList())
    }

    override suspend fun getUserByLogin(emailOrPhone: String): StudentItem?{
        val dbModel = studentsDao.getUserByLogin(emailOrPhone)
            ?: return null

        return mapper.mapDbModelToEntity(dbModel)
    }

    override suspend fun addNewGroup(group: GroupsItem) {
        groupsDao.addGroup(groupMapper.mapEntityToDbModel(group))
    }

    override suspend fun getGroupsStartingWith(query: String): List<String> {
        return groupsDao.getGroupsStartingWith(query)
    }

    suspend fun addGroupList(groups: List<GroupsItem>){
        for (group in groups){
            addNewGroup(group)
        }
    }
}