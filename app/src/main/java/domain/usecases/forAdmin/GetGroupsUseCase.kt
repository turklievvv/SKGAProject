package domain.usecases.forAdmin

import domain.entity.GroupItem
import domain.repository.AdminRepository

class GetGroupsUseCase(private val repository: AdminRepository) {
    suspend fun getGroups(): List<GroupItem> {
        return repository.getGroups()
    }
}