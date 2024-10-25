package data.models

import kotlinx.serialization.Serializable

@Serializable
data class AdminForLoginModel(
   val username: String,
   val password: String
)