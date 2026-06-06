package data.local

import android.util.Log
import data.dtoEntity.EventsDto
import data.dtoEntity.FacultyDto
import data.dtoEntity.GroupDto
import data.dtoEntity.ScheduleDto
import data.dtoEntity.UserProfileDto
import domain.entity.EventItem
import domain.entity.FacultyItem
import domain.entity.GroupItem
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

    fun mapStudentEntityToProfileDto(
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

    fun mapTeacherEntityToProfileDto(
        entity: UserProfile,
        userId: String,
        facultyId: Int
    ): UserProfileDto {
        return UserProfileDto(
            id = userId,
            firstName = entity.firstName,
            lastName = entity.lastName,
            middleName = entity.middleName,
            facultyId = facultyId,
            role = "teacher",
            phone = entity.phone,
            avatar = null,
            isAdmin = false,
            email = entity.email,
            course = null,
            groupId = null
        )
    }

    fun mapEventsDtoToEntity(eventsDto: EventsDto): EventItem {
        return EventItem(
            eventDate = eventsDto.eventDate,
            id = eventsDto.id,
            eventDescription = eventsDto.eventDescription,
            eventFaculties = eventsDto.eventFaculties,
            eventGroups = eventsDto.eventGroups,
            eventIsActual = eventsDto.eventIsActual,
            eventIsGlobal = eventsDto.eventIsGlobal,
            eventLocation = eventsDto.eventLocation,
            eventName = eventsDto.eventName,
            eventIsTeachers = eventsDto.eventIsTeachers,
            eventTime = eventsDto.eventTime,
            eventType = eventsDto.eventType
        )
    }

    fun mapEventsEntityToDto(eventsItem: EventItem): EventsDto {
        return EventsDto(
            eventDate = eventsItem.eventDate,
            id = eventsItem.id,
            eventDescription = eventsItem.eventDescription,
            eventFaculties = eventsItem.eventFaculties,
            eventGroups = eventsItem.eventGroups,
            eventIsActual = eventsItem.eventIsActual,
            eventIsGlobal = eventsItem.eventIsGlobal,
            eventLocation = eventsItem.eventLocation,
            eventName = eventsItem.eventName,
            eventIsTeachers = eventsItem.eventIsTeachers,
            eventTime = eventsItem.eventTime,
            eventType = eventsItem.eventType
        )
    }

    fun mapGroupDtoToEntity(groupDto: GroupDto): GroupItem {
        return GroupItem(
            id = groupDto.id,
            facultyId = groupDto.facultyId
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
            val teacher = teacherList.find { it.id == scheduleDto.teacherId }
            Log.d("DEBUG_TEACHER", "В расписание пришел $teacher")
            val teacherFullName = "${teacher?.lastName} ${teacher?.firstName} ${teacher?.middleName}"
            val teacherShortName =
                "${teacher?.lastName} ${teacher?.firstName?.firstOrNull()}. ${teacher?.middleName?.firstOrNull()}."
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
                lessonTeacherId = scheduleDto.teacherId,
                lessonTeacherShortName = teacherShortName
            )
        }
    }

    fun mapScheduleEntityToDto(item: ScheduleItem): ScheduleDto {
        return ScheduleDto(
            id = item.id,
            lessonName = item.lessonName,
            dayOfWeek = item.dayOfWeek,
            lessonNumber = item.lessonNumber,
            classRoom = item.lessonClassRoom,
            weekType = item.weekType,
            lessonStartTime = item.lessonStartTime,
            lessonEndTime = item.lessonEndTime,
            subGroup = item.subGroup,
            teacherId = item.lessonTeacherId,
            group = item.group
        )
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