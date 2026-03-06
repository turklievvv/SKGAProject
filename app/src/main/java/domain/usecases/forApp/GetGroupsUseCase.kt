package domain.usecases.forApp

import domain.repository.StudentRepository

class GetGroupsUseCase(private val repository: StudentRepository) {

    suspend fun getGroups(query: String): List<String>{
       return repository.getGroups(query)
    }
}