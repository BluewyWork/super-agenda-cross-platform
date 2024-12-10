package presentation.screens.initial

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import presentation.Destinations

@Composable
fun InitialScreen(initialViewModel: InitialViewModel, navController: NavController) {
   val loggedIn by initialViewModel.loggedIn.collectAsStateWithLifecycle()

   when (loggedIn) {
      LoggedInState.LOADING -> {}

      LoggedInState.LOGGED_IN -> {
         navController.navigate(Destinations.Users.route)
      }

      LoggedInState.LOGGED_OUT -> {
         navController.navigate(Destinations.Login.route)
      }
   }
}