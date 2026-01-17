package domain.usecases

import domain.entity.StudentItem
import domain.repository.StudentRepository

class DeleteStudentuseCase(private val studentRepository: StudentRepository) {

    suspend fun deleteStduentUseCase(studentItem: StudentItem){
        studentRepository.deleteStudentItem(studentItem)
    }

}