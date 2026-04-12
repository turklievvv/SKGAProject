package domain.entity

data class StudentProfile(
    val id: String, // UUID из Supabase Auth
    val firstName: String,
    val lastName: String,
    val middleName: String,
    val facultyId: Int?,
    val group: String?,
    val email: String,
    val phone: String,
    val course: Int?,
    val subgroup :Int?,
    val token : String,
    val avatarUrl: String?,
    val role : String?,
    val isAdmin: Boolean,
)