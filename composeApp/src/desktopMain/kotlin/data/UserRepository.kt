package data

import data.models.toDomain
import data.network.Api
import domain.models.User
import util.AppResult
import util.map

class UserRepository(
   private val api: Api
) {
   suspend fun getAllUsersFromApi(token: String): AppResult<List<User>> {
      return api.fetchAllUsers(token).map { it -> it.map { it.toDomain() } }
   }
}