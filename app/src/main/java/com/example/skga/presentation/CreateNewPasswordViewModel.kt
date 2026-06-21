package com.example.skga.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import data.api.SupabaseClient
import data.local.AdminRepositoryImpl
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.user.UserSession
import kotlinx.coroutines.launch

class CreateNewPasswordViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AdminRepositoryImpl(application)

    private val _result = MutableLiveData<Boolean>()
    val result: LiveData<Boolean> get() = _result

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    fun setupTeacherAccount(accessToken: String, refreshToken: String, newPassword: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("AUTH_DEBUG", "1. Импортируем полученную сессию...")
                SupabaseClient.client.auth.importSession(
                    UserSession(
                        accessToken = accessToken,
                        refreshToken = refreshToken,
                        expiresIn = 3600,
                        tokenType = "bearer",
                        user = null
                    )
                )

                Log.d("AUTH_DEBUG", "2. Запрашиваем данные пользователя с сервера...")
                val user = SupabaseClient.client.auth.retrieveUser(accessToken)
                val email = user.email ?: throw Exception("Email не найден в токене")
                Log.d("AUTH_DEBUG", "Пользователь определен: $email, ID: ${user.id}")

                Log.d("AUTH_DEBUG", "3. Обновляем пароль в системе аутентификации...")
                SupabaseClient.client.auth.updateUser {
                    password = newPassword
                }

                // Получаем свежий токен для запросов к базе данных
                val token = SupabaseClient.client.auth.currentAccessTokenOrNull() ?: accessToken

                Log.d("AUTH_DEBUG", "4. Ищем временный профиль для $email...")
                // ИСПРАВЛЕНИЕ: Так как возвращается чистый List, просто получаем его напрямую
                val pendingProfiles = SupabaseClient.api.getPendingProfile(
                    apiKey = SupabaseClient.API_KEY,
                    token = "Bearer $token",
                    email = "eq.$email"
                )

                val pendingProfile = pendingProfiles.firstOrNull()
                    ?: throw Exception("Профиль в pending_profiles не найден")

                Log.d("AUTH_DEBUG", "5. Переносим профиль в основную таблицу с ID: ${user.id}...")
                val finalProfile = pendingProfile.copy(id = user.id)

                // Вызываем создание финального профиля (если тут тоже чистый ответ без Response)
                SupabaseClient.api.createProfile(
                    apiKey = SupabaseClient.API_KEY,
                    token = "Bearer $token",
                    profile = finalProfile
                )

                Log.d("AUTH_DEBUG", "Все шаги успешно выполнены!")
                _result.value = true
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                Log.e("REPO_ERROR", "Ошибка при установке пароля: ${e.message}")
                _result.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
}