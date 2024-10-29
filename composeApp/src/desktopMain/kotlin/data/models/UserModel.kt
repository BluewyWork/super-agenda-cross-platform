package data.models

import domain.models.User
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.types.ObjectId

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

object ObjectIdSerializer : KSerializer<ObjectId> {
   override val descriptor: SerialDescriptor =
      PrimitiveSerialDescriptor("ObjectId", PrimitiveKind.STRING)

   override fun serialize(encoder: Encoder, value: ObjectId) {
      encoder.encodeString(value.toHexString())
   }

   override fun deserialize(decoder: Decoder): ObjectId {
      return ObjectId(decoder.decodeString())
   }
}