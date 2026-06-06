package domain.usecases.forAdmin

import domain.entity.UserProfile
import domain.repository.AdminRepository

class UpdateProfileUseCase(private val repository: AdminRepository) {

    suspend fun updateProfile(profile: UserProfile): Result<Unit> {
        return repository.updateProfile(profile)
    }

}