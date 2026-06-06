package domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EventItem(
    val id: String,
    val eventName: String,
    val eventDate: String,
    val eventTime: String,
    val eventLocation: String,
    val eventGroups: List<String>?,
    val eventIsTeachers: Boolean,
    val eventFaculties: List<Int>?,
    val eventIsGlobal: Boolean,
    val eventDescription: String,
    val eventType: String,
    val eventIsActual: Boolean
): Parcelable