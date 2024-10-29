package data.network

object Endpoints {
   private const val BASE_URL = "http://127.0.0.1:8001"

   const val LOGIN = "$BASE_URL/api/admin/auth/login"
   const val GET_ALL_USERS = "$BASE_URL/api/admin/user/show/all"
}