package data.api

import com.google.gson.annotations.SerializedName
import data.api.UserData

data class SignUpResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("user") val user: UserData
)