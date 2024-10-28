package data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TokenEntity(
   @PrimaryKey(autoGenerate = false)
   val token: String
)