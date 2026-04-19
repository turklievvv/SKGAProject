package domain.entity

data class TeacherItem(
    val userProfile: UserProfile,
    val lessonCount: Int,
    val weekLessonCount: Int,
    val totalHours: Int,
    val teacherGroups: List<String>,
    val teacherLessons:List<ScheduleItem>
)