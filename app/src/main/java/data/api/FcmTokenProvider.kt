package data.api
import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class FcmTokenProvider {

    private val firebaseMessagingScope = "https://www.googleapis.com/auth/firebase.messaging"

    suspend fun getValidAccessToken(context: Context): String = withContext(Dispatchers.IO) {
        // 🚀 service_account.json — это файл приватного ключа, который ты качаешь из Firebase Console
        val stream: InputStream = context.assets.open("service_account.json")

        val credentials = GoogleCredentials
            .fromStream(stream)
            .createScoped(listOf(firebaseMessagingScope))

        // Заставляем обновить токен, если он протух
        credentials.refreshIfExpired()

        // Возвращаем чистую строку токена
        return@withContext credentials.accessToken.tokenValue
    }
}