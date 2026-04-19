package data.local

import android.app.Application
import android.util.Log
import data.api.SignUpRequest
import data.api.SupabaseClient
import domain.entity.DayConfig
import domain.entity.FacultyItem
import domain.entity.ScheduleItem
import domain.entity.StudentEvents
import domain.entity.StudentItem
import domain.entity.UserProfile
import domain.repository.StudentRepository
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StudentRepositoryImpl(application: Application) : StudentRepository {

    private val map = Mapper()
    private val api = SupabaseClient.api
    private val supabaseClient = SupabaseClient


    private val sessionManager = UserSessionManager(application)

    override suspend fun getGroups(): List<String> {
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

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        return try {
            val authResponse = api.login(SupabaseClient.API_KEY, SignUpRequest(email, password))
            val uid = authResponse.user.id
            val token = authResponse.accessToken

            val profileDto =
                api.getStudentProfile(SupabaseClient.API_KEY, "Bearer $token", "eq.$uid").first()
            val facultyId = getFaculties().find { profileDto.facultyId == it.id }

            val domainProfile = UserProfile(
                id = profileDto.id,
                firstName = profileDto.firstName,
                lastName = profileDto.lastName,
                middleName = profileDto.middleName,
                group = profileDto.groupId,
                facultyId = facultyId?.id ?: 1,
                email = email,
                phone = profileDto.phone,
                course = calculateCourse(profileDto.groupId?:"нет"),
                subgroup = 1,
                avatarUrl = profileDto.avatar,
                role = profileDto.role,
                isAdmin = profileDto.isAdmin
            )

            sessionManager.saveStudentProfile(domainProfile)

            Result.success(domainProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadScheduleForStudent(): Result<List<ScheduleItem>> {
        return try {
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            val student = sessionManager.userProfile.first()
            val group = student?.group ?: return Result.failure(Exception("Группа не найдена"))
            Log.d("REPO_DEBUG", "Запрос на группу $group")
            val studentSubgroup = student.subgroup
            val facultyId = student.facultyId

            // 2. Делаем запрос в Supabase с фильтром по группе
            val response = api.getScheduleByGroup(
                apiKey = SupabaseClient.API_KEY,
                token = "Bearer $currentToken",
                group = "eq.$group"
            )


            val items = map.mapScheduleDtoToEntity(response)

            val filteredItems = items.filter { item ->
                item.subGroup == 0 || item.subGroup == studentSubgroup
            }

            Log.d("REPO_DEBUG", "Количество уроков ${filteredItems.size}")
            Result.success(filteredItems)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun loadEventForStudent(): Result<List<StudentEvents>> {
        return try {
            val currentToken = supabaseClient.client.auth.currentAccessTokenOrNull()
            val student = sessionManager.userProfile.first()
            val group = student?.group ?: return Result.failure(Exception("Группа не найдена"))
            val facultyId = student.facultyId
            Log.d("AUTH_DEBUG", "Отправляю токен: Bearer $currentToken")
            val filter = "or(group_id.eq.$group,faculty_id.eq.$facultyId,is_global.is.true)"

            val responseEvents = api.getEvents(
                SupabaseClient.API_KEY,
                currentToken ?: "",
                filter)
            Result.success(responseEvents)
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


    override suspend fun uploadAvatar(fileBytes: ByteArray, fileName: String): String? {
        return try {
            val bucket = SupabaseClient.client.storage.from("StudentAvatars")
            val path = "$fileName.jpg"
            Log.d("SUPABASE_DEBUG", "Начинаем загрузку. Размер файла: ${fileBytes.size} байт")

            bucket.upload(path = path, data = fileBytes, upsert = true)
            Log.d("SUPABASE_DEBUG", "Загрузка завершена успешно!")

            // 2. Получаем публичную ссылку
            // В новых версиях это делается через publicUrl()
            val url = bucket.publicUrl(path)
            Log.d("SUPABASE_DEBUG", "Публичная ссылка: $url")

            url
        } catch (e: Exception) {
            Log.e("SUPABASE_DEBUG", "КРАШ ПРИ ЗАГРУЗКЕ: ${e.message}")
            Log.e("SUPABASE_DEBUG", "Stacktrace: ${Log.getStackTraceString(e)}")
            null
        }
    }

    override suspend fun updateAvatarUrl(studentId: String, imageUrl: String) {
        SupabaseClient.client.postgrest.from("profiles").update(
            {
                set("avatar", imageUrl)
            }
        ) {
            filter {
                eq("id", studentId)
            }
        }
    }

    fun generateDaysList(howManyDays: Int): List<DayConfig> {
        val list = mutableListOf<DayConfig>()
        val calendar = Calendar.getInstance()
        val localeRU = Locale.forLanguageTag("ru")

        // Формат для даты (число и месяц)
        val dateFormat = SimpleDateFormat("d MMMM", localeRU)
        // Формат для краткого дня недели (Пн, Вт...)
        val dayFormat = SimpleDateFormat("EE", localeRU)

        repeat(howManyDays) {
            val date = calendar.time

            // 1. Получаем день недели (приводим к Пн=1, Вс=7)
            val calendarDay = calendar.get(Calendar.DAY_OF_WEEK)
            val dayOfWeek = if (calendarDay == Calendar.SUNDAY) 7 else calendarDay - 1

            // 2. Определяем тип недели (1 - числитель/нечетная, 2 - знаменатель/четная)
            // В РФ учебные недели обычно привязаны к номеру недели в году
            val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
            val currentWeekType = if (weekOfYear % 2 == 0) 2 else 1

            // 3. Формируем краткое название ("пт" -> "Пт")
            val shortName = dayFormat.format(date)
                .replaceFirstChar { it.uppercase() }
                .replace(".", "") // Убираем точки, если есть (например, "пт.")

            list.add(
                DayConfig(
                    date = date,
                    dayOfWeek = dayOfWeek,
                    weekType = currentWeekType,
                    dateText = dateFormat.format(date),
                    dayName = shortName,
                    weekOfYear = weekOfYear
                )
            )

            // Переходим к следующему дню
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return list
    }
}