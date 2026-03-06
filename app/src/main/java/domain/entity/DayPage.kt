package domain.entity

import java.util.Date


data class DayPage(
    val date: Date,
    val dayName: String,
    val dateDisplay: String,
    val dayOfWeek: Int
)
