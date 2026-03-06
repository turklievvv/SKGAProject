package data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import domain.entity.StudentItem
import domain.entity.StudentProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Расширение для контекста, чтобы создать DataStore один раз
private val Context.dataStore by preferencesDataStore(name = "user_session")

class UserSessionManager(private val context: Context) {
    private val gson = Gson()

    companion object {
        private val STUDENT_PROFILE_KEY = stringPreferencesKey("student_profile")
    }

    // СОХРАНЕНИЕ: Берем твой StudentItem и превращаем в JSON-строку
    suspend fun saveStudentProfile(student: StudentProfile) {
        val jsonString = gson.toJson(student)
        context.dataStore.edit { preferences ->
            preferences[STUDENT_PROFILE_KEY] = jsonString
        }
    }

    // ЧТЕНИЕ: Достаем строку и превращаем обратно в объект StudentItem
    val studentProfile: Flow<StudentProfile?> = context.dataStore.data
        .map { preferences ->
            val jsonString = preferences[STUDENT_PROFILE_KEY]
            if (jsonString != null) {
                gson.fromJson(jsonString, StudentProfile::class.java)
            } else {
                null
            }
        }

    // ВЫХОД: Очистка данных
    suspend fun clearSession() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}