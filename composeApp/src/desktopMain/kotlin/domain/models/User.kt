package domain.models

import org.bson.types.ObjectId

data class User(
   val id: ObjectId,
   val username: String,
   val hashedPassword: String,
)