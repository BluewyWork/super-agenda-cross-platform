package data

import data.models.AdminForLoginModel
import data.network.Api
import util.NetworkError
import util.Result

class AuthenticationRepository(
   private val api: Api
) {
   suspend fun login(username: String, password: String): Result<String, NetworkError> {
      val adminForLoginModel = AdminForLoginModel(username, password)
      return api.login(adminForLoginModel)
   }
}