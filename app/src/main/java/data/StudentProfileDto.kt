package data

import com.google.gson.annotations.SerializedName

data class StudentProfileDto(
    @SerializedName("id") val id: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("middle_name") val middleName: String,
    @SerializedName("role") val role: String,
    @SerializedName("group_id") val groupId: String,
    @SerializedName("faculty_id") val facultyId: Int,
    @SerializedName("phone")val phone: String
)
