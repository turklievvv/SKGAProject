package domain.usecases.forAdmin

import data.dtoEntity.ScheduleDto
import domain.entity.ScheduleItem
import domain.repository.AdminRepository

class CreateLessonUseCase(private val repository: AdminRepository) {

    suspend operator fun invoke(scheduleItem: ScheduleItem): Result<Unit>{
        return repository.createLesson(scheduleItem)
    }


}