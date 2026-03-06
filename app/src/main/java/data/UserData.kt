package data

import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("id") val id: String,
    @SerializedName("email") val email: String
)
