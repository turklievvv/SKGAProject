package domain.usecases

import domain.entity.StudentItem
import domain.repository.StudentRepository

class AddStudentItemUseCase(private val studentRepository: StudentRepository) {

    suspend fun addStudentItemUseCase(studentItem: StudentItem){
        studentRepository.addStudentItem(studentItem)
    }

}