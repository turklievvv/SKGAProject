package data.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface FirebaseNotificationApi {

    @POST("v1/projects/{projectId}/messages:send")
    suspend fun sendDataNotification(
        @Path("projectId") projectId: String,
        @Header("Authorization") bearerToken: String, // Сюда передадимAccessToken
        @Body messageContainer: FcmMessageContainer
    ): retrofit2.Response<Unit>
}