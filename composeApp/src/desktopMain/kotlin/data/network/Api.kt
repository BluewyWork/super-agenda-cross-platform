package data.network

import data.models.AdminForLoginModel
import data.network.models.ApiResponse
import data.network.models.TokenInResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import util.AppError
import util.AppResult
import util.Result

class Api(
   private val httpClient: HttpClient
) {
   suspend fun login(body: AdminForLoginModel): AppResult<String> {
      val response = try {
         httpClient.post(
            urlString = Endpoints.LOGIN
         ) {
            contentType(ContentType.Application.Json)
            setBody(body)
         }
      } catch (e: UnresolvedAddressException) {
         return Result.Error(AppError.NetworkError.NO_INTERNET)
      } catch (e: SerializationException) {
         return Result.Error(AppError.NetworkError.SERIALIZATION)
      }

      println(response)

      return when (response.status.value) {
         in 200..299 -> {
            val payload = response.body<ApiResponse<TokenInResponse>>()
            Result.Success(payload.data.token)
         }

         401 -> Result.Error(AppError.NetworkError.UNAUTHORIZED)
         409 -> Result.Error(AppError.NetworkError.CONFLICT)
         408 -> Result.Error(AppError.NetworkError.REQUEST_TIMEOUT)
         413 -> Result.Error(AppError.NetworkError.PAYLOAD_TOO_LARGE)
         in 500..599 -> Result.Error(AppError.NetworkError.SERVER_ERROR)
         else -> Result.Error(AppError.NetworkError.UNKNOWN)
      }
   }
}