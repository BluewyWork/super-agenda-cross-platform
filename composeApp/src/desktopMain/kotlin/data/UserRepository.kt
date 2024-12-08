package data

import data.models.UserForUpdateModel
import data.models.toData
import data.models.toDomain
import data.network.Api
import domain.models.User
import domain.models.UserForAdminView
import domain.models.UserForUpdate
import kotlinx.serialization.json.Json
import org.bson.types.ObjectId
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

   suspend fun updateUserForUpdateAtApi(token: String, id: String, userForUpdate: UserForUpdate): AppResult<Unit> {
      return api.updateUserForUpdate(token, id, userForUpdate.toData())
   }

   suspend fun deleteUserAtApi(token: String, id: ObjectId): AppResult<Unit> {
      return api.deleteUser(token, id.toHexString())
   }

   suspend fun createUserAtApi(token: String, user: User): AppResult<Unit> {
      return api.createUser(token, user.toData())
   }
}