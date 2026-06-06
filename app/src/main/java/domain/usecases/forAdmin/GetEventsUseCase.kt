package domain.usecases.forAdmin

import domain.entity.EventItem
import domain.repository.AdminRepository

class GetEventsUseCase(private val repository: AdminRepository) {

    suspend fun getEventsList(): Result<List<EventItem>> {
        return repository.getEventsList()
    }

}