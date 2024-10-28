package data.database

import androidx.room.Database
import androidx.room.RoomDatabase

interface Workaround {
   fun clearAllTables()
}

@Database(
   entities = [TokenEntity::class],
   version = 1
)
abstract class AppDatabase : RoomDatabase(), Workaround {
   abstract fun tokenDao(): TokenDao

   override fun clearAllTables() {}
}