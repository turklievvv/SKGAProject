package data.dtoEntity

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class StudentEventsDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("event_date") val eventDate: String, // Формат "2026-03-24"
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("event_type") val eventType: String = "info",
    @SerializedName("groupd_id") val group: String? = null,
    @SerializedName("facylty_id") val facultyId:Int? = null,
    @SerializedName("is_global")val isGlobal: Boolean = false
)
