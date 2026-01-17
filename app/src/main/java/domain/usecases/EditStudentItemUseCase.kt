package domain.usecases

import domain.entity.StudentItem
import domain.repository.StudentRepository

class EditStudentItemUseCase(private val studentRepository: StudentRepository) {

    suspend fun editStudentItemUseCase(studentItem: StudentItem){
        studentRepository.editStudentItem(studentItem)
    }

}