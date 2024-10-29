package domain

import data.AuthenticationRepository
import util.AppResult
import util.Result

class AuthenticationUseCase(
   private val authenticationRepository: AuthenticationRepository
) {
   suspend fun login(username: String, password: String): AppResult<Unit> {
      val token =
         when (val apiResult = authenticationRepository.getTokenFromApi(username, password)) {
            is Result.Error -> return Result.Error(apiResult.error)
            is Result.Success -> apiResult.data
         }

      return when (val dbResult = authenticationRepository.insertTokenToDatabase(token)) {
         is Result.Error -> Result.Error(dbResult.error)
         is Result.Success -> Result.Success(Unit)
      }
   }

   suspend fun checkIfLoggedIn(): Boolean {
      return when (val dbResult = authenticationRepository.getTokenFromDatabase()) {
         is Result.Error -> false
         is Result.Success -> true
      }
   }
}