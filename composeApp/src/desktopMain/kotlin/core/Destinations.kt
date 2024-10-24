package core

sealed class Destinations(
   val route: String
) {
   object Example : Destinations("example")
}