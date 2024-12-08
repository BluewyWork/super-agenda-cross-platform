package data.network

import data.models.AdminForLoginModel
import data.models.AdminForUpdateModel
import data.models.AdminModel
import data.models.UserForAdminViewModel
import data.models.UserForUpdateModel
import data.models.UserForCreateModel
import data.network.models.ApiResponse
import data.network.models.TokenInResponse
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
         it.body<ApiResponse<TokenInResponse>>().result.token
      }
   }

   suspend fun fetchAllUsers(token: String): AppResult<List<UserForCreateModel>> {
      return safeApiCall(
         apiCall = {
            httpClient.get(urlString = Endpoints.GET_ALL_USERS) {
               header("Authorization", token)
            }
         }
      ) {
         it.body<ApiResponse<List<UserForCreateModel>>>().result
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
         it.body<ApiResponse<List<UserForAdminViewModel>>>().result
      }
   }

   suspend fun updateUserForUpdate(
      token: String,
      id: String,
      userForUpdateModel: UserForUpdateModel
   ): AppResult<Unit> {
      return safeApiCall(
         apiCall = {
            httpClient.patch(urlString = "${Endpoints.UPDATE_USER}/$id") {
               header("Authorization", token)
               contentType(ContentType.Application.Json)
               setBody(userForUpdateModel)
            }
         }
      ) {}
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
         it.body<ApiResponse<List<AdminModel>>>().result
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

   suspend fun deleteUser(token: String, id: String): AppResult<Unit> {
      return safeApiCall(
         apiCall = {
            httpClient.delete(urlString = "${Endpoints.DELETE_USER}/$id") {
               header("Authorization", token)
            }
         }
      ) {}
   }

   suspend fun createUser(token: String, userForCreateModel: UserForCreateModel): AppResult<Unit> {
      return safeApiCall(
         apiCall = {
            httpClient.post(urlString = Endpoints.CREATE_USER) {
               header("Authorization", token)
               contentType(ContentType.Application.Json)
               setBody(userForCreateModel)
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
      print("${response}")

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
      print("ERROR: $e")

      when (e) {
         is SerializationException -> Result.Error(AppError.NetworkError.SERIALIZATION)
         else -> Result.Error(AppError.NetworkError.UNKNOWN)
      }
   }
}