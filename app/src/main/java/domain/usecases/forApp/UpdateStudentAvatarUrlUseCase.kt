package domain.usecases.forApp

import domain.repository.StudentRepository

class UpdateStudentAvatarUrlUseCase(private val repository: StudentRepository) {

    suspend fun updateAvatarUrl(studentId: String, imageUrl: String) {
        repository.updateAvatarUrl(studentId, imageUrl)
    }

}