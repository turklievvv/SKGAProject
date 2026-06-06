package domain.usecases.forAdmin

import domain.entity.EventItem
import domain.repository.AdminRepository

class CreateEventUseCase(private val repository: AdminRepository) {

    suspend operator fun invoke(event: EventItem): Result<Unit> {
        return repository.createEvent(event)
    }
}
