package presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import presentation.composables.NavigationBarWrapper
import presentation.screens.login.LoginScreen
import presentation.screens.login.LoginViewModel
import presentation.screens.users.UsersScreen

@Composable
fun NavigationHost() {
   val navController = rememberNavController()

   NavHost(
      navController = navController,
      startDestination = Destinations.Login.route
   ) {
      composable(Destinations.Login.route) {
         val loginViewModel = koinViewModel<LoginViewModel>()
         LoginScreen(loginViewModel, navController)
      }

      composable(Destinations.Users.route) {
         NavigationBarWrapper(navController) {
            UsersScreen()
         }
      }
   }
}