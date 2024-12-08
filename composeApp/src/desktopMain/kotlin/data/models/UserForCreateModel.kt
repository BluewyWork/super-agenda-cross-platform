package data.models

import domain.models.UserForCreate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import util.ObjectIdSerializer

@Serializable
data class UserForCreateModel(
   @SerialName(value = "_id")
   @Serializable(with = ObjectIdSerializer::class)
   val id: ObjectId,
   val username: String,
   val password: String
)

fun UserForCreateModel.toDomain() = UserForCreate(
   id = id,
   username = username,
   password = password
)

fun UserForCreate.toData() = UserForCreateModel (
   id = id,
   username = username,
   password = password
)