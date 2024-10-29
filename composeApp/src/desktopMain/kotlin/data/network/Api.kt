package data.network

import data.models.AdminForLoginModel
import data.network.models.ApiResponse
import data.network.models.TokenInResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
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
   // data type should be probably be transform in repository and not here
   suspend fun login(body: AdminForLoginModel): AppResult<String> {
      return safeApiCall(
         apiCall = {
            httpClient.post(urlString = Endpoints.LOGIN) {
               contentType(ContentType.Application.Json)
               setBody(body)
            }
         })
      {
         it.body<ApiResponse<TokenInResponse>>().data.token
      }
   }
}

// helper function
private inline fun <T> safeApiCall(
   apiCall: () -> HttpResponse,
   successHandler: (HttpResponse) -> T
): AppResult<T> {
   return try {
      val response = apiCall()

      when (response.status.value) {
         in 200..299 -> {
            val result = successHandler(response)
            Result.Success(result)
         }

         // TODO: this can be expanded
         401 -> Result.Error(AppError.NetworkError.UNAUTHORIZED)
         409 -> Result.Error(AppError.NetworkError.CONFLICT)
         408 -> Result.Error(AppError.NetworkError.REQUEST_TIMEOUT)
         413 -> Result.Error(AppError.NetworkError.PAYLOAD_TOO_LARGE)
         in 500..599 -> Result.Error(AppError.NetworkError.SERVER_ERROR)
         else -> Result.Error(AppError.NetworkError.UNKNOWN)
      }
   } catch (e: Exception) {
      when (e) {
         is UnresolvedAddressException -> Result.Error(AppError.NetworkError.NO_INTERNET)
         is SerializationException -> Result.Error(AppError.NetworkError.SERIALIZATION)
         else -> Result.Error(AppError.NetworkError.UNKNOWN)
      }
   }
}