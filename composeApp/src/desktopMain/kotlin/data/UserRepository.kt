package data

import data.models.toDomain
import data.network.Api
import domain.models.User
import util.AppResult
import util.Result

class UserRepository(
   private val api: Api
) {
   suspend fun getAllUsersFromApi(token: String): AppResult<List<User>> {
      return when (val apiResult = api.fetchAllUsers(token)) {
         is Result.Error -> Result.Error(apiResult.error)
         is Result.Success -> Result.Success(apiResult.data.map { it.toDomain() })
      }
   }
}