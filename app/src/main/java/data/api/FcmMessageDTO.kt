package data.api

import com.google.gson.annotations.SerializedName

data class FcmMessageDTO(
    @SerializedName("topic") val topic: String? = null,
    @SerializedName("condition") val condition: String? = null ,
    @SerializedName("data") val data: Map<String, String>
)
