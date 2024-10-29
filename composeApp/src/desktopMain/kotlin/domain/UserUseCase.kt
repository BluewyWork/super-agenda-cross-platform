package domain

import data.AuthenticationRepository
import data.UserRepository
import domain.models.User
import util.AppResult
import util.Result

class UserUseCase(
   private val authenticationRepository: AuthenticationRepository,
   private val userRepository: UserRepository
) {
   suspend fun retrieveAllUsersFromApi(): AppResult<List<User>> {
      val token = when (val tokenResult = authenticationRepository.getTokenFromDatabase()) {
         is Result.Error -> return Result.Error(tokenResult.error)
         is Result.Success -> tokenResult.data
      }

      return when (val usersResult = userRepository.getAllUsersFromApi(token)) {
         is Result.Error -> Result.Error(usersResult.error)
         is Result.Success -> Result.Success(usersResult.data)
      }
   }
}