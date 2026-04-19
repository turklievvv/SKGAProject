package data.local

import data.dtoEntity.FacultyDto
import data.dtoEntity.ScheduleDto
import data.dtoEntity.UserProfileDto
import domain.entity.FacultyItem
import domain.entity.ScheduleItem
import domain.entity.StudentItem
import domain.entity.UserProfile
import java.util.Calendar

class Mapper {

    fun facultyDtoToFacultyItem(facultyDto: FacultyDto): FacultyItem {
        return FacultyItem(
            facultyDto.id,
            facultyDto.name
        )
    }

    fun mapEntityToProfileDto(
        entity: StudentItem,
        userId: String,
        facultyId: Int
    ): UserProfileDto {
        return UserProfileDto(
            id = userId,
            firstName = entity.firstName,
            lastName = entity.lastName,
            middleName = entity.middleName,
            facultyId = facultyId,
            groupId = entity.group,
            role = "student",
            phone = entity.phone,
            avatar = null,
            isAdmin = false,
            email = entity.email,
            course = calculateCourse(entity.group ?: "")
        )
    }

    fun mapProfileDtoToEntity(
        entityDto: UserProfileDto
    ): UserProfile {
        return UserProfile(
            id = entityDto.id,
            firstName = entityDto.firstName,
            lastName = entityDto.lastName,
            middleName = entityDto.middleName,
            facultyId = entityDto.facultyId,
            group = entityDto.groupId,
            role = entityDto.role,
            phone = entityDto.phone,
            avatarUrl = entityDto.avatar,
            isAdmin = entityDto.isAdmin,
            email = entityDto.email,
            course = calculateCourse(entityDto.groupId ?: ""),
            subgroup = 1,
        )
    }

    fun mapScheduleDtoToEntity(scheduleDto: List<ScheduleDto>,teacherList:List<UserProfile>): List<ScheduleItem> {
        return scheduleDto.map { scheduleDto ->
            val teacher = teacherList.find { it.id == scheduleDto.id }
            val teacherFullName = "${teacher?.lastName} ${teacher?.firstName} ${teacher?.middleName}"
            ScheduleItem(
                id = scheduleDto.id,
                lessonNumber = scheduleDto.lessonNumber,
                lessonName = scheduleDto.lessonName,
                lessonClassRoom = scheduleDto.classRoom,
                lessonTeacherFullName = teacherFullName,
                lessonEndTime = scheduleDto.lessonEndTime,
                lessonStartTime = scheduleDto.lessonStartTime,
                dayOfWeek = scheduleDto.dayOfWeek,
                weekType = scheduleDto.weekType,
                subGroup = scheduleDto.subGroup,
                group = scheduleDto.group,
                lessonTeacherId = scheduleDto.teacherId
            )
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