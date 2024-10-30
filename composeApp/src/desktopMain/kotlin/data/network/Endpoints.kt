package data.network

object Endpoints {
   private const val BASE_URL = "http://127.0.0.1:8001"

   const val GET_TOKEN = "$BASE_URL/api/admin/auth/login"
   const val GET_ALL_USERS = "$BASE_URL/api/admin/user/show/all"

   const val CREATE_ADMIN = "$BASE_URL/api/admin/admin/new"
   const val GET_ALL_ADMINS = "$BASE_URL/api/admin/admin/show/all"
   const val UPDATE_ADMIN = "$BASE_URL/api/admin/admin/update"
   const val DELETE_ADMIN = "$BASE_URL/api/admin/admin/nuke"
}