package domain.usecases.forAdmin

import domain.entity.ScheduleItem
import domain.repository.AdminRepository

class UpdateLessonUseCase(private val repository: AdminRepository) {

    suspend operator fun invoke(scheduleItem: ScheduleItem): Result<Unit>{
        return repository.updateLesson(scheduleItem)
    }

}