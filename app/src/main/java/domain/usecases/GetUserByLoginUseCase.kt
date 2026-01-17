package domain.usecases

import domain.entity.StudentItem
import domain.repository.StudentRepository

class GetUserByLoginUseCase(private val repository: StudentRepository) {

    suspend fun getUserByLogin(emailOrPhone: String): StudentItem?{
        return repository.getUserByLogin(emailOrPhone)
    }

}