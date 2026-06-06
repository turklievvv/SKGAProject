package domain.usecases.forAdmin

import domain.entity.UserProfile
import domain.repository.AdminRepository

class CreateTeacherProfileUseCase(private val repository: AdminRepository) {

    suspend fun createProfile(profile: UserProfile): Result<Unit>{
        return repository.createTeacherProfile(profile)
    }

}