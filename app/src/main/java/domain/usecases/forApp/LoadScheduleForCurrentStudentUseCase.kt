package domain.usecases.forApp

import domain.entity.ScheduleItem
import domain.repository.StudentRepository

class LoadScheduleForCurrentStudentUseCase(private val repository: StudentRepository) {

    suspend fun loadScheduleForStudent():Result<List<ScheduleItem>>{
        return repository.loadScheduleForStudent()
    }

}