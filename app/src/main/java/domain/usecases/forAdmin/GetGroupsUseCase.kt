package domain.usecases.forAdmin

import domain.repository.AdminRepository

class GetGroupsUseCase(private val repository: AdminRepository) {
    suspend fun getGroups(): List<String> {
        return repository.getGroups()
    }
}