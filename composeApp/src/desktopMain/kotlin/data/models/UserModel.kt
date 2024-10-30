package data.models

import domain.models.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.bson.types.ObjectId
import util.ObjectIdSerializer

@Serializable
data class UserModel(
   @SerialName(value = "_id")
   @Serializable(with = ObjectIdSerializer::class)
   val id: ObjectId,
   val username: String,
   @SerialName(value = "hashed_password")
   val hashedPassword: String
)

fun UserModel.toDomain() = User(
   id = id,
   username = username,
   hashedPassword = hashedPassword
)