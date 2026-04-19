package domain.usecases.forAdmin

import domain.entity.ScheduleItem
import domain.repository.AdminRepository

class GetTeacherLessonsUseCase(private val repository: AdminRepository) {

    suspend fun getLessons(teacherId: String): Result<List<ScheduleItem>>{
        return repository.getTeacherLessons(teacherId)
    }

}