package data.network

import data.models.AdminForLoginModel
import data.network.models.ApiResponse
import data.network.models.TokenInResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import util.NetworkError
import util.Result

class Api(
   private val httpClient: HttpClient
) {
   suspend fun exampleFunction(): Result<Unit, NetworkError> {
      val response = try {
         httpClient.get(
            urlString = "to be changed"
         )
      } catch (e: Exception) {
         return Result.Error(NetworkError.UNKNOWN)
      }

      return when (response.status.value) {
         in 200..299 -> {
            Result.Success(Unit)
         }

         401 -> Result.Error(NetworkError.UNAUTHORIZED)
         409 -> Result.Error(NetworkError.CONFLICT)
         408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
         413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
         in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
         else -> Result.Error(NetworkError.UNKNOWN)
      }
   }

   suspend fun login(body: AdminForLoginModel): Result<String, NetworkError> {
      val response = try {
         httpClient.post(
            urlString = Endpoints.LOGIN
         ) {
            contentType(ContentType.Application.Json)
            setBody(body)
         }
      } catch (e: UnresolvedAddressException) {
         return Result.Error(NetworkError.NO_INTERNET)
      } catch (e: SerializationException) {
         return Result.Error(NetworkError.SERIALIZATION)
      }

      println(response)

      return when (response.status.value) {
         in 200..299 -> {
            val payload = response.body<ApiResponse<TokenInResponse>>()
            Result.Success(payload.data.token)
         }

         401 -> Result.Error(NetworkError.UNAUTHORIZED)
         409 -> Result.Error(NetworkError.CONFLICT)
         408 -> Result.Error(NetworkError.REQUEST_TIMEOUT)
         413 -> Result.Error(NetworkError.PAYLOAD_TOO_LARGE)
         in 500..599 -> Result.Error(NetworkError.SERVER_ERROR)
         else -> Result.Error(NetworkError.UNKNOWN)
      }
   }
}