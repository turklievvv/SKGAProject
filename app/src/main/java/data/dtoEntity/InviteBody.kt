package data.dtoEntity

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class InviteBody(
    @SerializedName("email") val email: String,
    @SerializedName("redirect_to") val redirectTo: String
)