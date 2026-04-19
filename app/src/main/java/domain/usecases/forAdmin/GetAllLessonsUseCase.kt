package domain.usecases.forAdmin

import domain.entity.ScheduleItem
import domain.repository.AdminRepository

class GetAllLessonsUseCase(private val repository: AdminRepository) {

    suspend fun getAllLessons(): Result<List<ScheduleItem>> {
        return repository.getAllLessons()
    }

}