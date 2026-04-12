package domain.usecases.forAdmin

import domain.entity.ScheduleItem
import domain.repository.AdminRepository

class GetGroupScheduleUseCase(private val repository: AdminRepository) {

    suspend fun getGroupSchedule(group: String): Result<List<ScheduleItem>>{
        return repository.getGroupSchedule(group)
    }

}