package data.models

import domain.models.Membership
import domain.models.UserForAdminView
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import util.ObjectIdSerializer

@Serializable
enum class MembershipModel {
   FREE,
   PREMIUM
}

@Serializable
data class UserForAdminViewModel(
   @Serializable(with = ObjectIdSerializer::class)
   val id: ObjectId,
   val username: String,
   @SerialName(value = "tasks_size") val tasksSize: Int,
   val membership: MembershipModel
)

fun UserForAdminViewModel.toDomain() = UserForAdminView(
   id = id,
   username = username,
   tasksSize = tasksSize,

   membership = when (membership) {
      MembershipModel.FREE -> Membership.FREE
      MembershipModel.PREMIUM -> Membership.PREMIUM
   }
)
