package data

import android.app.Application
import android.provider.SyncStateContract
import android.util.Log
import data.SupabaseClient.API_KEY
import data.local.UserSessionManager
import domain.entity.FacultyItem
import domain.entity.ScheduleItem
import domain.entity.StudentItem
import domain.entity.StudentProfile
import domain.repository.StudentRepository
import kotlinx.coroutines.flow.first
import java.util.Calendar

class StudentRepositoryImpl(application: Application) : StudentRepository {

    private val map = Mapper()
    private val api = SupabaseClient.api

    private val sessionManager = UserSessionManager(application)

//
//    override suspend fun deleteStudentItem(studentItem: StudentItem) {
//        studentsDao.deleteStudentItem(mapper.mapEntityToDbModel(studentItem).id)
//    }
//
//    override suspend fun editStudentItem(studentItem: StudentItem) {
//        studentsDao.addStudentItem(mapper.mapEntityToDbModel(studentItem))
//    }
//
//    override suspend fun getStudent(studentId: Int): StudentItem {
//        val dbModel = studentsDao.getStudentItem(studentId)
//        return mapper.mapDbModelToEntity(dbModel)
//    }
//
//    override suspend fun getStudentList(): List<StudentItem> {
//        return mapper.mapDbModelListToEntityList(studentsDao.getStudentItemList())
//    }
//
//    override suspend fun getUserByLogin(emailOrPhone: String): StudentItem? {
//        val dbModel = studentsDao.getUserByLogin(emailOrPhone)
//            ?: return null
//
//        return mapper.mapDbModelToEntity(dbModel)
//    }
//
//    override suspend fun addNewGroup(group: GroupsItem) {
//        groupsDao.addGroup(groupMapper.mapEntityToDbModel(group))
//    }

    override suspend fun getGroups(query: String): List<String> {
        val list = api.getGroups(SupabaseClient.API_KEY)
        return list.map { it.id }
    }

    override suspend fun getFaculties(): List<FacultyItem> {
        val list = api.getFaculties(SupabaseClient.API_KEY)
        return list.map { map.facultyDtoToFacultyItem(it) }
    }

    override suspend fun signUp(studentItem: StudentItem): Result<Unit> {
        return try {
            val authResponse = SupabaseClient.api.signUp(
                SupabaseClient.API_KEY,
                SignUpRequest(studentItem.email, studentItem.password)
            )

            val allFaculties = api.getFaculties(SupabaseClient.API_KEY)

            // Ищем ID факультета по имени, которое лежит в studentItem.faculties
            // Если studentItem.faculties это String, то сравниваем с it.name
            val foundFacultyId = allFaculties.find { it.name == studentItem.faculties }?.id ?: 0

            val profile = map.mapEntityToProfileDto(
                studentItem,
                authResponse.user.id,
                foundFacultyId
            )

            SupabaseClient.api.createProfile(
                apiKey = SupabaseClient.API_KEY,
                token = "Bearer ${authResponse.accessToken}",
                profile = profile
            )

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("Sign up error", "Error dutin sign up process", e)
            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<StudentProfile> {
        return try {
            val authResponse = api.login(API_KEY, SignUpRequest(email, password))
            val uid = authResponse.user.id
            val token = authResponse.accessToken

            val profileDto = api.getStudentProfile(API_KEY, "Bearer $token", "eq.$uid").first()
            val facultyId = getFaculties().find { profileDto.facultyId == it.id }

            val domainProfile = StudentProfile(
                id = profileDto.id,
                firstName = profileDto.firstName,
                lastName = profileDto.lastName,
                middleName = profileDto.middleName,
                group = profileDto.groupId,
                facultyId = facultyId?.id ?: 1,
                email = email,
                phone = profileDto.phone,
                course = calculateCourse(profileDto.groupId),
                subgroup = 1,
                token = authResponse.accessToken
            )

            sessionManager.saveStudentProfile(domainProfile)

            Result.success(domainProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun loadScheduleForCurrentStudent(): Result<List<ScheduleItem>> {
        return try {

            val student = sessionManager.studentProfile.first()
            val group = student?.group ?: return Result.failure(Exception("Группа не найдена"))

            // 2. Делаем запрос в Supabase с фильтром по группе
            val response = api.getScheduleByGroup(
                apiKey = SupabaseClient.API_KEY,
                token = "Bearer ${student.token}",
                groupId = "eq.$group"
            )

            // 3. Мапим DTO в доменную модель
            val items = map.mapScheduleDtoToEntity(response)
            Result.success(items)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateCourse(groupName: String): Int {
        val yearPart = groupName.filter { it.isDigit() }.take(2)
        val entryYear = yearPart.toIntOrNull() ?: return 1 // Год поступления (например, 22)

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR) % 100 // Текущий год (например, 24)
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // Месяцы в Calendar начинаются с 0

        var course = currentYear - entryYear

        if (currentMonth >= 9) {
            course += 1
        }

        return course.coerceIn(1, 6)
    }
}
