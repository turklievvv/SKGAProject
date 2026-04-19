package domain.usecases.forApp

import domain.repository.StudentRepository

class UploadAvatarUseCase(private val repository: StudentRepository) {

    suspend fun uploadAvatar(fileBytes: ByteArray, fileName: String): String? {
        return repository.uploadAvatar(fileBytes, fileName)
    }

}