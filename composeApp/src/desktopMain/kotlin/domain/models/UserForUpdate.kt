package domain.models

import data.models.MembershipModel

data class UserForUpdate(
   val username: String?,
   val membership: Membership?
)