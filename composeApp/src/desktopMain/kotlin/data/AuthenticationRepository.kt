package data

import data.models.AdminForLoginModel
import data.network.ApiClient
import util.NetworkError
import util.Result

class AuthenticationRepository(
   private val apiClient: ApiClient
) {
   suspend fun login(username: String, password: String): Result<String, NetworkError> {
      val adminForLoginModel = AdminForLoginModel(username, password)
      return apiClient.login(adminForLoginModel)
   }
}