package data.dtoEntity

import com.google.gson.annotations.SerializedName

data class EventsDto(
    @SerializedName("id") val id: String,
    @SerializedName("event_name") val eventName: String,
    @SerializedName("event_date") val eventDate: String,
    @SerializedName("event_location") val eventLocation: String,
    @SerializedName("event_groups") val eventGroups: List<String>?,
    @SerializedName("event_faculties") val eventFaculties: List<Int>?,
    @SerializedName("event_is_teachers") val eventIsTeachers: Boolean,
    @SerializedName("event_is_global") val eventIsGlobal: Boolean,
    @SerializedName("event_description") val eventDescription: String,
    @SerializedName("event_type") val eventType: String,
    @SerializedName("event_is_actual") val eventIsActual: Boolean,
    @SerializedName("event_time") val eventTime: String,
    )
