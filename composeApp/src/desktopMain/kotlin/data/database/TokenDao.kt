package data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface TokenDao {
   @Upsert
   suspend fun upsert(tokenEntity: TokenEntity)

//   @Delete
//   suspend fun delete()

   @Query("SELECT * FROM TokenEntity LIMIT 1")
   suspend fun get(): TokenEntity
}