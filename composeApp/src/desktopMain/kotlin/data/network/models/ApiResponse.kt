package data.network.models

import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(val result: T, val ok: Boolean)