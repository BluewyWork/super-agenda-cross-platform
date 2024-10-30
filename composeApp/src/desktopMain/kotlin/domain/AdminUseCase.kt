package domain

import data.AdminRepository
import data.AuthenticationRepository
import domain.models.Admin
import util.AppResult
import util.Result

class AdminUseCase(
   private val authenticationRepository: AuthenticationRepository,
   private val adminRepository: AdminRepository
) {
   suspend fun spawnAdminAtApi(admin: Admin): AppResult<Unit> {
      val token = when (val tokenResult = authenticationRepository.getTokenFromDatabase()) {
         is Result.Error -> return Result.Error(tokenResult.error)
         is Result.Success -> tokenResult.data
      }

      return adminRepository.createAdminAtApi(token, admin)
   }

   suspend fun retrieveAllAdminsFromApi(): AppResult<List<Admin>> {
      val token = when (val tokenResult = authenticationRepository.getTokenFromDatabase()) {
         is Result.Error -> return Result.Error(tokenResult.error)
         is Result.Success -> tokenResult.data
      }

      return adminRepository.getAllAdminsFromApi(token)
   }

   suspend fun modifyAdminAtApi(admin: Admin): AppResult<Unit> {
      val token = when (val tokenResult = authenticationRepository.getTokenFromDatabase()) {
         is Result.Error -> return Result.Error(tokenResult.error)
         is Result.Success -> tokenResult.data
      }

      return adminRepository.updateAdminAtApi(token, admin);
   }

   suspend fun destroyAdminAtApi(adminId: String): AppResult<Unit> {
      val token = when (val tokenResult = authenticationRepository.getTokenFromDatabase()) {
         is Result.Error -> return Result.Error(tokenResult.error)
         is Result.Success -> tokenResult.data
      }

      return adminRepository.deleteAdminAtApi(token, adminId)
   }
}