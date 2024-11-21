package domain.models

import org.bson.types.ObjectId

enum class Membership {
   FREE,
   PREMIUM
}

data class UserForAdminView(
   val id: ObjectId,
   val username: String,
   val tasksSize: Int,
   val membership: Membership
)
