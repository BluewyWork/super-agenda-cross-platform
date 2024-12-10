package presentation.screens.initial

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import presentation.Destinations

@Composable
fun InitialScreen(initialViewModel: InitialViewModel, navController: NavController) {
   val loggedIn by initialViewModel.loggedIn.collectAsStateWithLifecycle()

   if (loggedIn) {
      navController.navigate(Destinations.Users.route)
   } else {
      navController.navigate(Destinations.Login.route)
   }
}