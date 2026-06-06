package data.api

import com.google.gson.annotations.SerializedName

data class FcmMessageContainer(
    @SerializedName("message") val message: FcmMessageDTO
)
