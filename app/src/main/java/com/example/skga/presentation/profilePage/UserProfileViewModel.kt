package com.example.skga.presentation.profilePage

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.application
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import data.local.StudentRepositoryImpl
import data.local.UserSessionManager
import domain.entity.UserProfile
import domain.usecases.forApp.UpdateStudentAvatarUrlUseCase
import kotlinx.coroutines.launch
import java.io.File

class UserProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudentRepositoryImpl(application)

    private val updateStudentAvatarUrlUseCase = UpdateStudentAvatarUrlUseCase(repository)
    private val userSessionManager = UserSessionManager(application.applicationContext)
    lateinit var student: UserProfile

    suspend fun uploadStudentPhoto(
        byteArray: ByteArray,
        view: ImageView,
        userProfile: UserProfile,
        context: Context
    ) {
        val fileName =
            "${userProfile.id}_profile_photo}"
        val imageUrl = repository.uploadAvatar(byteArray, fileName)
        if (imageUrl != null) {
            updateStudentAvatarUrlUseCase.updateAvatarUrl(userProfile.id, imageUrl)
            userSessionManager.updateAvatarUrl(imageUrl)
            Glide.with(context)
                .load(imageUrl)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .circleCrop()
                .into(view)
            Toast.makeText(
                context,
                "Фото обновлено!",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            Toast.makeText(
                context,
                "Ошибка загрузки",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun createTempUri(): Uri {
        // 1. Создаем временный файл в кэше приложения
        val tempFile = File.createTempFile(
            "avatar_capture_${System.currentTimeMillis()}",
            ".jpg",
            application.applicationContext.cacheDir
        ).apply {
            createNewFile()
            deleteOnExit() // Удалить файл, когда приложение закроется
        }

        // 2. Превращаем файл в безопасный Uri через FileProvider
        return FileProvider.getUriForFile(
            application.applicationContext,
            "com.example.skga.provider", // Строка должна совпадать с манифестом
            tempFile
        )
    }

    fun takeStudentProfile() {
        val userSession = UserSessionManager(application.applicationContext)
        viewModelScope.launch {
            userSession.userProfile.collect {
                if (it != null)
                    student = it
            }
        }
    }

    fun uriToByteArray(uri: Uri): ByteArray? {
        return try {
            // Открываем поток для чтения данных по Uri
            val inputStream = application.applicationContext.contentResolver.openInputStream(uri)

            // Читаем все байты и закрываем поток
            val bytes = inputStream?.use { it.readBytes() }
            bytes
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}