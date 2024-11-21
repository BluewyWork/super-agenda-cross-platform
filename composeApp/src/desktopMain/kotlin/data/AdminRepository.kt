package data

import data.models.toData
import data.models.toDomain
import data.network.Api
import domain.models.Admin
import domain.models.AdminForUpdate
import org.bson.types.ObjectId
import util.AppResult
import util.map

class AdminRepository(
   private val api: Api
) {
   suspend fun createAdminAtApi(token: String, admin: Admin): AppResult<Unit> {
      return api.createAdmin(token, admin.toData())
   }

   suspend fun getAllAdminsFromApi(token: String): AppResult<List<Admin>> {
      return api.readAllAdmins(token).map { it -> it.map { it.toDomain() } }
   }

   suspend fun updateAdminAtApi(
      token: String,
      id: ObjectId,
      admin: AdminForUpdate
   ): AppResult<Unit> {
      return api.updateAdmin(token, id.toHexString(), admin.toData())
   }

   suspend fun deleteAdminAtApi(token: String, adminId: String): AppResult<Unit> {
      return api.deleteAdmin(token, adminId)
   }
}