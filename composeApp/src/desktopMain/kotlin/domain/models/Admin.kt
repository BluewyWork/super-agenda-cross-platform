package domain.models

import org.bson.types.ObjectId

data class Admin(
   val id: ObjectId,
   val username: String,
   val hashedPassword: String
)