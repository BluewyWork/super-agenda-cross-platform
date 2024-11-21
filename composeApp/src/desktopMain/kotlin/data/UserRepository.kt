package data

import data.models.toDomain
import data.network.Api
import domain.models.User
import domain.models.UserForAdminView
import util.AppResult
import util.map

class UserRepository(
   private val api: Api
) {
   suspend fun getAllUsersFromApi(token: String): AppResult<List<User>> {
      return api.fetchAllUsers(token).map { it -> it.map { it.toDomain() } }
   }

   suspend fun getAllUsersForAdminViewFromApi(token: String): AppResult<List<UserForAdminView>> {
      return api.fetchAllUsersForAdminView(token).map { it -> it.map { it.toDomain() } }
   }
}