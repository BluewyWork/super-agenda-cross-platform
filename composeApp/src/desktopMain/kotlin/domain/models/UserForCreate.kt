package domain.models

import org.bson.types.ObjectId

data class UserForCreate(
   val id: ObjectId,
   val username: String,
   val password: String,
)