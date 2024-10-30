package data.models

import domain.models.Admin
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import util.ObjectIdSerializer

@Serializable
data class AdminModel(
   @SerialName(value = "_id")
   @Serializable(with = ObjectIdSerializer::class)
   val id: ObjectId,
   val username: String,
   @SerialName(value = "hashed_password")
   val hashedPassword: String
)

fun AdminModel.toDomain() = Admin(
   id = id,
   username = username,
   hashedPassword = hashedPassword
)

fun Admin.toData() = AdminModel(
   id = id,
   username = username,
   hashedPassword = hashedPassword
)