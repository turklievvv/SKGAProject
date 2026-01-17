package data

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [GroupsDbModel::class], version = 1)
abstract class GroupsAppDatabase : RoomDatabase() {

    abstract fun groupsDao(): GroupsDao

    companion object {
        private var INSTANCE: GroupsAppDatabase? = null
        private val LOCK = Any()
        private const val DB_NAME = "groups_db.db"


        fun getInstance(application: Application): GroupsAppDatabase {
            INSTANCE?.let {
                return it
            }
            synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }
                val db = Room.databaseBuilder(
                    application, GroupsAppDatabase::class.java, DB_NAME
                ).build()
                INSTANCE = db
                return db

            }
        }
    }
}
