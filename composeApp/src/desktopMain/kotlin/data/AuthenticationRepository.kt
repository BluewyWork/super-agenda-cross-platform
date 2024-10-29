package data

import data.database.TokenDao
import data.database.TokenEntity
import data.models.AdminForLoginModel
import data.network.Api
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import util.AppError
import util.AppResult
import util.Result

class AuthenticationRepository(
   private val api: Api,
   private val tokenDao: TokenDao
) {
   suspend fun getTokenFromApi(username: String, password: String): AppResult<String> {
      val adminForLoginModel = AdminForLoginModel(username, password)
      return api.fetchToken(adminForLoginModel)
   }

   suspend fun insertTokenToDatabase(token: String): AppResult<Unit> {
      return withContext(Dispatchers.IO) {
         // might throw, ngl idk
         try {
            tokenDao.upsert(tokenEntity = TokenEntity(token))

            Result.Success(Unit)
         } catch (e: Exception) {
            Result.Error(AppError.DatabaseError.UNKNOWN)
         }
      }
   }

   suspend fun getTokenFromDatabase(): AppResult<String> {
      return withContext(Dispatchers.IO) {
         try {
            Result.Success(tokenDao.get().token)
         } catch (e: Exception) {
            Result.Error(AppError.DatabaseError.UNKNOWN)
         }
      }
   }
}