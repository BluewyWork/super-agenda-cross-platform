package data.models

import domain.models.AdminForUpdate
import kotlinx.serialization.Serializable

@Serializable
data class AdminForUpdateModel(
   val username: String
)

fun AdminForUpdateModel.toDomain() = AdminForUpdate(
   username = username
)

fun AdminForUpdate.toData() = AdminForUpdateModel(
   username = username
)