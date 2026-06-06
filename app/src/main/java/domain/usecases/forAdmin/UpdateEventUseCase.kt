package domain.usecases.forAdmin

import domain.entity.EventItem
import domain.repository.AdminRepository

class UpdateEventUseCase(private val repository: AdminRepository) {

    suspend operator fun invoke(eventItem: EventItem): Result<Unit>{
        return repository.updateEvent(eventItem)
    }

}