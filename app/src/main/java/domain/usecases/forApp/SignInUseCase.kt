package domain.usecases.forApp

import domain.entity.UserProfile
import domain.repository.StudentRepository

class SignInUseCase(private val repository: StudentRepository) {

    suspend fun login(email: String,password:String): Result<UserProfile>{
        return repository.login(email,password)
    }

}