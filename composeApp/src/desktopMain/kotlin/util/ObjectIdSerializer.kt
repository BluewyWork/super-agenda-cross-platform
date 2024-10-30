package util

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import org.bson.types.ObjectId

object ObjectIdSerializer : KSerializer<ObjectId> {
   override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ObjectId") {
      element("\$oid", PrimitiveSerialDescriptor("oid", PrimitiveKind.STRING))
   }

   override fun serialize(encoder: Encoder, value: ObjectId) {
      encoder.encodeStructure(descriptor) {
         encodeStringElement(descriptor, 0, value.toHexString())
      }
   }

   override fun deserialize(decoder: Decoder): ObjectId {
      return decoder.decodeStructure(descriptor) {
         var oid: String? = null

         while (true) {
            when (val index = decodeElementIndex(descriptor)) {
               CompositeDecoder.DECODE_DONE -> break
               0 -> oid = decodeStringElement(descriptor, 0)
               else -> throw SerializationException("Unknown index $index")
            }
         }

         oid?.let { ObjectId(it) } ?: throw SerializationException("Missing \$oid")
      }
   }
}