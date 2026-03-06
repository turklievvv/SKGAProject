package domain.usecases.forApp

import domain.entity.AuthResult
import domain.entity.StudentProfile
import domain.repository.StudentRepository

class SignInUseCase(private val repository: StudentRepository) {

    suspend fun login(email: String,password:String): Result<StudentProfile>{
        return repository.login(email,password)
    }

}