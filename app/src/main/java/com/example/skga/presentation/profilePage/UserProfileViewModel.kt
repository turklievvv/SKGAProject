package com.example.skga.presentation.profilePage

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _student = MutableLiveData<UserProfile>()
    val student: LiveData<UserProfile> get() = _student

    fun takeStudentProfile() {
        viewModelScope.launch {
            userSessionManager.userProfile.collect {
                if (it != null) _student.value = it
            }
        }
    }

    suspend fun uploadStudentPhoto(
        byteArray: ByteArray,
        view: ImageView,
        userProfile: UserProfile,
        context: Context
    ) {
        val fileName = "${userProfile.id}_profile_photo"  // заодно убрали лишнюю }
        val imageUrl = repository.uploadAvatar(byteArray, fileName)
        if (imageUrl != null) {
            updateStudentAvatarUrlUseCase.updateAvatarUrl(userProfile.id, imageUrl)
            userSessionManager.updateAvatarUrl(imageUrl)
            Glide.with(context)
                .load(imageUrl)
                .signature(ObjectKey(System.currentTimeMillis().toString()))
                .circleCrop()
                .into(view)
            Toast.makeText(context, "Фото обновлено!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Ошибка загрузки", Toast.LENGTH_SHORT).show()
        }
    }

    fun createTempUri(): Uri {
        val tempFile = File.createTempFile(
            "avatar_capture_${System.currentTimeMillis()}",
            ".jpg",
            application.applicationContext.cacheDir
        ).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            application.applicationContext,
            "com.example.skga.provider",
            tempFile
        )
    }

    fun uriToByteArray(uri: Uri): ByteArray? {
        return try {
            val inputStream = application.applicationContext.contentResolver.openInputStream(uri)
            inputStream?.use { it.readBytes() }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}