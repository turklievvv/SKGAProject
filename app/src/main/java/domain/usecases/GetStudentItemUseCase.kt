package domain.usecases

import domain.entity.StudentItem
import domain.repository.StudentRepository

class GetStudentItemUseCase(private val studentRepository: StudentRepository) {

    suspend fun getStudentItemUseCase(studentId: Int): StudentItem {
        return studentRepository.getStudent(studentId)
    }

}