package domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ScheduleItem (
    val id: String,
    val lessonNumber: Int,
    val lessonName: String,
    val lessonStartTime: String,
    val lessonEndTime: String,
    val lessonClassRoom: String,
    val lessonTeacherFullName: String,
    val lessonTeacherId: String,
    val dayOfWeek:Int,
    val weekType: Int,
    val subGroup:Int,
    val group: String
) : Parcelable