package data

import domain.entity.FacultyItem
import domain.entity.ScheduleItem
import domain.entity.StudentItem

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
    ): StudentProfileDto {
        return StudentProfileDto(
            id = userId,
            firstName = entity.firstName,
            lastName = entity.lastName,
            middleName = entity.middleName,
            facultyId = facultyId,
            groupId = entity.group,
            role = "student",
            phone = entity.phone
        )
    }

    fun mapScheduleDtoToEntity(scheduleDto: List<ScheduleDto>): List<ScheduleItem> {
        return scheduleDto.map { scheduleDto ->
            ScheduleItem(
                lessonNumber = scheduleDto.lessonNumber,
                lessonName = scheduleDto.lessonName,
                lessonClassRoom = scheduleDto.classRoom,
                lessonTeacher = scheduleDto.teacherName,
                lessonEndTime = scheduleDto.lessonEndTime,
                lessonStartTime = scheduleDto.lessonStartTime,
                dayOfWeek = scheduleDto.dayOfWeek
            )
        }
    }
}