package domain

import data.AuthenticationRepository
import util.EmptyResult
import util.NetworkError
import util.Result
import util.onError

class AuthenticationUseCase(
   private val authenticationRepository: AuthenticationRepository
) {
   suspend fun login(username: String, password: String): EmptyResult<NetworkError> {
      val result = authenticationRepository.login(username, password)

      result.onError {
         return Result.Error(it)
      }

      return Result.Success(Unit)
   }
}