package domain.usecases

import domain.entity.GroupsItem
import domain.repository.StudentRepository

class AddNewGroupUseCase(private val repository: StudentRepository) {

    suspend fun addNewGroup(group: GroupsItem) {
        repository.addNewGroup(group)
    }
}