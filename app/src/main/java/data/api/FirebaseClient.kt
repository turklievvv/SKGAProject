package data.api
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object FirebaseClient {

    private const val BASE_URL = "https://fcm.googleapis.com/"

    // Настраиваем логирование и таймауты
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.HEADERS
        })
        .build()

    // Строим сам Retrofit
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create()) // Парсер JSON
        .build()

    // Создаем готовый API-интерфейс, который будем вызывать
    val api: FirebaseNotificationApi = retrofit.create(FirebaseNotificationApi::class.java)
}