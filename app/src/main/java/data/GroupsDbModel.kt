package data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "groups_db")
data class GroupsDbModel(
    @PrimaryKey(true)
    val id: Int = 0,
    val group: String,
    )

