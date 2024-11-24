package data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(val success: T, val ok: Boolean)