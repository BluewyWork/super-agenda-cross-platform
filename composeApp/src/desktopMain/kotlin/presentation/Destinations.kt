package presentation

sealed class Destinations(
   val route: String
) {
   object Login : Destinations("login")
   object Users : Destinations("users")
   object Admins : Destinations("admins")
}