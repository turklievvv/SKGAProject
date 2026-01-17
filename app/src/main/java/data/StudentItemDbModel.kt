package data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity("students_list")
data class StudentItemDbModel(
    @PrimaryKey(true)
    val id :Int = 0,
    val surName: String,
    val lastName: String,
    val middleName: String,
    val group : String,
    val phone: String,
    val email : String,
    val password:String
)
