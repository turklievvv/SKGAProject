package data.dtoEntity

import com.google.gson.annotations.SerializedName

data class GroupDto(
    @SerializedName("id") val id: String,
    @SerializedName("faculty_id") val facultyId: Int
)