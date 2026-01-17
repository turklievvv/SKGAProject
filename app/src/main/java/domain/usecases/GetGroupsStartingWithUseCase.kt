package domain.usecases

import androidx.lifecycle.LiveData
import domain.repository.StudentRepository

class GetGroupsStartingWithUseCase(private val repository: StudentRepository) {

    suspend fun getGroupStartWith(query: String): List<String>{
       return repository.getGroupsStartingWith(query)
    }
}