package data.models

import domain.models.Membership
import domain.models.UserForUpdate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserForUpdateModel(
   val username: String?,
   @SerialName(value = "membership") val membershipModel: MembershipModel?
)

fun UserForUpdate.toData() = UserForUpdateModel(
   username = username,
   membershipModel = when (membership) {
      Membership.FREE -> MembershipModel.FREE
      Membership.PREMIUM -> MembershipModel.PREMIUM
      null -> null
   }
)