package domain.usecases.forApp

import domain.entity.StudentEvents
import domain.repository.StudentRepository

class LoadEventsForCurrentStudentUseCase(private val repository: StudentRepository) {

    suspend fun loadEvents():Result<List<StudentEvents>>{
        return repository.loadEventForStudent()
    }

}