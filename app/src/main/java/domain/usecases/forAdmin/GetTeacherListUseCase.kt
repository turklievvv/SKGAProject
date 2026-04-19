package domain.usecases.forAdmin

import domain.entity.UserProfile
import domain.repository.AdminRepository

class GetTeacherListUseCase(private val repository: AdminRepository) {

    suspend fun getTeacherList(): Result<List<UserProfile>> {
        return repository.getTeachersList()
    }
}