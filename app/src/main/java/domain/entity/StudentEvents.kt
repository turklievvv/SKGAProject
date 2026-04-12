package domain.entity

data class StudentEvents(
    val id: Int,
    val title: String,
    val eventDate: String, // Храним как "2026-03-24"
    val eventType: String,
    val description: String,
    val groupId: String? = null,  // TEXT -> String
    val facultyId: Int? = null,   // INT4 -> Int
    val isGlobal: Boolean = false
)