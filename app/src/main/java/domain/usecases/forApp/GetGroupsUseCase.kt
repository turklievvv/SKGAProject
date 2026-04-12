package domain.usecases.forApp

import domain.repository.StudentRepository

class GetGroupsUseCase(private val repository: StudentRepository) {

    suspend fun getGroups(): List<String>{
       return repository.getGroups()
    }
}