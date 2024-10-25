package data.network

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(val data: T, val ok: Boolean, val message: String)