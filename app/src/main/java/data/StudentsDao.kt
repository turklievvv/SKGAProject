package data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface StudentsDao {

        @Query("SELECT * FROM students_list")
        suspend fun getStudentItemList(): List<StudentItemDbModel>

        @Insert(onConflict = OnConflictStrategy.ABORT)
        suspend fun addStudentItem(studentItem: StudentItemDbModel)

        @Query("DELETE FROM students_list WHERE id=:studentItemId")
        suspend fun deleteStudentItem(studentItemId: Int)

        @Query("SELECT * FROM students_list WHERE id=:studentItemId LIMIT 1")
        suspend fun getStudentItem(studentItemId: Int): StudentItemDbModel

    @Query("""
        SELECT * FROM students_list
        WHERE email = :loginOrPhone
           OR phone = :loginOrPhone
        LIMIT 1
    """)
    suspend fun getUserByLogin(loginOrPhone: String): StudentItemDbModel?

}