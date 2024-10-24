package core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import presentation.screens.example.ExampleScreen
import presentation.screens.example.ExampleViewModel
import org.koin.compose.viewmodel.koinViewModel
import presentation.screens.login.LoginViewModel
import presentation.screens.login.LoginScreen

@Composable
fun NavigationHost() {
   val navController = rememberNavController()

   NavHost(
      navController = navController,
      startDestination = Destinations.Login.route
   ) {
      composable(Destinations.Example.route) {
         val exampleViewModel = koinViewModel<ExampleViewModel>()
         ExampleScreen(exampleViewModel)
      }

      composable(Destinations.Login.route) {
         val loginViewModel = koinViewModel<LoginViewModel>()
         LoginScreen(loginViewModel, navController)
      }
   }
}