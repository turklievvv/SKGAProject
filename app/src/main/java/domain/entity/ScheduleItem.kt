package domain.entity

data class ScheduleItem (
    val lessonNumber: Int,
    val lessonName: String,
    val lessonStartTime: String,
    val lessonEndTime: String,
    val lessonClassRoom: String,
    val lessonTeacher: String,
    val dayOfWeek:Int
)