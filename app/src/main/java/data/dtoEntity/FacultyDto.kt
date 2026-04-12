package data.dtoEntity

import com.google.gson.annotations.SerializedName

data class FacultyDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)