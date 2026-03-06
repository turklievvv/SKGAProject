package domain.usecases.forApp

import domain.entity.StudentItem
import domain.repository.StudentRepository

class SignUpUseCase(private val repository: StudentRepository) {

    suspend fun signUp(student: StudentItem): Result<Unit>{
        return repository.signUp(student)
    }

}