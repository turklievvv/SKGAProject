package data.dtoEntity

import com.google.gson.annotations.SerializedName

data class ScheduleDto(
    @SerializedName("id") val id : String,
    @SerializedName("subject_name")val lessonName: String,
    @SerializedName("day_of_week")val dayOfWeek: Int,
    @SerializedName("lesson_number")val lessonNumber: Int,
    @SerializedName("room")val classRoom: String,
    @SerializedName("week_type")val weekType: Int,
    @SerializedName("start_time")val lessonStartTime: String,
    @SerializedName("end_time")val lessonEndTime: String,
    @SerializedName("subgroup")val subGroup: Int,
    @SerializedName("teacher_id")val teacherId: String,
    @SerializedName("group")val group: String
)