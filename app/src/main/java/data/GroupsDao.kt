package data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface GroupsDao {
    @Query("SELECT `group` FROM groups_db WHERE `group` LIKE :query || '%'")
    suspend fun getGroupsStartingWith(query: String): List<String>

    @Insert
    suspend fun addGroup(group: GroupsDbModel)
}