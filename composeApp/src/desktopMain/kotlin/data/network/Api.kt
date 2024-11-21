package data.network

import data.models.AdminForLoginModel
import data.models.AdminForUpdateModel
import data.models.AdminModel
import data.models.UserForAdminViewModel
import data.models.UserModel
import data.network.models.ApiResponse
import data.network.models.TokenInResponse
import domain.models.AdminForUpdate
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerializationException
import util.AppError
import util.AppResult
import util.Result

class Api(
   private val httpClient: HttpClient
) {
   // data type should be probably be transform in repository and not here
   suspend fun fetchToken(body: AdminForLoginModel): AppResult<String> {
      return safeApiCall(
         apiCall = {
            httpClient.post(urlString = Endpoints.GET_TOKEN) {
               contentType(ContentType.Application.Json)
               setBody(body)
            }
         }
      ) {
         it.body<ApiResponse<TokenInResponse>>().data.token
      }
   }

   suspend fun fetchAllUsers(token: String): AppResult<List<UserModel>> {
      return safeApiCall(
         apiCall = {
            httpClient.get(urlString = Endpoints.GET_ALL_USERS) {
               header("Authorization", token)
            }
         }
      ) {
         it.body<ApiResponse<List<UserModel>>>().data
      }
   }

   suspend fun fetchAllUsersForAdminView(token: String): AppResult<List<UserForAdminViewModel>> {
      return safeApiCall(
         apiCall = {
            httpClient.get(urlString = Endpoints.GET_ALL_USERS) {
               header("Authorization", token)
            }
         }
      ) {
         it.body<ApiResponse<List<UserForAdminViewModel>>>().data
      }
   }

   suspend fun createAdmin(token: String, admin: AdminModel): AppResult<Unit> {
      return safeApiCall(
         apiCall = {
            httpClient.post(urlString = Endpoints.CREATE_ADMIN) {
               contentType(ContentType.Application.Json)
               setBody(admin)
               header("Authorization", token)
            }
         }
      ) {}
   }

   suspend fun readAllAdmins(token: String): AppResult<List<AdminModel>> {
      return safeApiCall(
         apiCall = {
            httpClient.get(urlString = Endpoints.GET_ALL_ADMINS) {
               header("Authorization", token)
            }
         }
      ) {
         it.body<ApiResponse<List<AdminModel>>>().data
      }
   }

   suspend fun updateAdmin(
      token: String,
      id: String,
      adminForUpdate: AdminForUpdateModel
   ): AppResult<Unit> {
      return safeApiCall(
         apiCall = {
            httpClient.patch(urlString = "${Endpoints.UPDATE_ADMIN}/$id") {
               header("Authorization", token)
               contentType(ContentType.Application.Json)
               setBody(adminForUpdate)
            }
         }
      ) {}
   }

   suspend fun deleteAdmin(token: String, adminId: String): AppResult<Unit> {
      return safeApiCall(
         apiCall = {
            httpClient.delete(urlString = "${Endpoints.DELETE_ADMIN}/$adminId") {
               header("Authorization", token)
            }
         }
      ) {}
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
      print("$e")

      when (e) {
         is SerializationException -> Result.Error(AppError.NetworkError.SERIALIZATION)
         else -> Result.Error(AppError.NetworkError.UNKNOWN)
      }
   }
}