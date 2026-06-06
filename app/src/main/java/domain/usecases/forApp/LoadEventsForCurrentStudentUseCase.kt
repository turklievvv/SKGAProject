package domain.usecases.forApp

import domain.entity.EventItem
import domain.repository.StudentRepository

class LoadEventsForCurrentStudentUseCase(private val repository: StudentRepository) {

    suspend fun loadEvents():Result<List<EventItem>>{
        return repository.loadEventForStudent()
    }

}