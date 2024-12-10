package presentation

sealed class Destinations(
   val route: String
) {
   object Initial: Destinations("initial")
   object Login : Destinations("login")
   object Users : Destinations("users")
   object Admins : Destinations("admins")
}