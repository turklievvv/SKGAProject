package domain.entity

import java.util.Date

data class DayConfig(
    val date: Date,
    val dayOfWeek: Int,
    val dateText: String,
    val weekOfYear:Int,
    val weekType:Int,
    val dayName: String
) {
    val dayNameShort: String
        get() = when (dayOfWeek) {
            1 -> "Пн"
            2 -> "Вт"
            3 -> "Ср"
            4 -> "Чт"
            5 -> "Пт"
            6 -> "Сб"
            7 -> "Вс"
            else -> ""
        }

    val currentWeekType: Int
        get() = if (weekOfYear % 2 == 0) 2 else 1
}
