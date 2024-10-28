package domain

import data.AuthenticationRepository
import util.AppResult
import util.Result
import util.onError

class AuthenticationUseCase(
   private val authenticationRepository: AuthenticationRepository
) {
   suspend fun login(username: String, password: String): AppResult<Unit> {
      val result = authenticationRepository.getTokenFromApi(username, password)

      result.onError {
         return Result.Error(it)
      }

      val dbResult = authenticationRepository.insertTokenToDatabase()

      dbResult.onError {
         return Result.Error(it)
      }

      return Result.Success(Unit)
   }
}