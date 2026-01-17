package domain.usecases

import domain.entity.StudentItem
import domain.repository.StudentRepository

class GetStudentsListUseCase(private val studentRepository: StudentRepository) {

    suspend fun getStudentListUseCase(): List<StudentItem>{
        return studentRepository.getStudentList()
    }

}