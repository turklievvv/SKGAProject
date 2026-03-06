package domain.usecases.forApp

import domain.entity.FacultyItem
import domain.repository.StudentRepository

class GetFacultiesUseCase (private val studentRepository: StudentRepository) {

    suspend fun getFaculties(): List<FacultyItem>{
        return studentRepository.getFaculties()
    }

}