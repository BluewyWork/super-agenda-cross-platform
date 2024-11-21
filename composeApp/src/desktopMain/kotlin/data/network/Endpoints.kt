package data.network

object Endpoints {
   private const val BASE_URL = "http://127.0.0.1:8001/api"

   const val GET_TOKEN = "$BASE_URL/auth/admin/login"

   const val GET_ALL_USERS = "$BASE_URL/users"

   const val CREATE_ADMIN = "$BASE_URL/admins"
   const val GET_ALL_ADMINS = "$BASE_URL/admins"
   const val UPDATE_ADMIN = "$BASE_URL/admins"
   const val DELETE_ADMIN = "$BASE_URL/admins"
}