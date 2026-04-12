package data.api

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SupabaseClient {
    // 1. Твои секретные данные (замени на свои из панели Supabase)
    private const val BASE_URL = "https://dmudryitskxaqgbnawpv.supabase.co"
    const val API_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRtdWRyeWl0c2t4YXFnYm5hd3B2Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzE2Mjk5MjAsImV4cCI6MjA4NzIwNTkyMH0.VTQuc9IRqT24xmygA3ShD4ekIfytiRb-GMjaCA3O3Xs"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = createSupabaseClient(
        supabaseUrl = BASE_URL,
        supabaseKey = API_KEY
    ) {

        install(Storage.Companion)
        install(Postgrest.Companion)
    }

    // 3. Создаем OkHttpClient (это "руки" нашего официанта)
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .addInterceptor { chain ->
            val original = chain.request()
            val request = original.newBuilder()
                // 1. Заголовок apikey
                .header("apikey", API_KEY.trim())
                // 2. Заголовок Authorization (БЕЗ НЕГО БУДЕТ 401!)
                .header("Authorization", "Bearer ${API_KEY.trim()}")
                .build()
            chain.proceed(request)
        }
        .build()

    // 4. Настраиваем сам Retrofit
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // Учим его читать JSON
        .client(httpClient) // Даем ему наши настроенные "руки"
        .build()

    // 5. Создаем готовую реализацию интерфейса
    // Теперь через SupabaseClient.api мы сможем вызывать методы регистрации и входа
    val api: SupabaseApi = retrofit.create(SupabaseApi::class.java)
}