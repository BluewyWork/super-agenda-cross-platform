package presentation

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import presentation.composables.NavigationBarWrapper
import presentation.screens.admins.AdminsScreen
import presentation.screens.admins.AdminsViewModel
import presentation.screens.login.LoginScreen
import presentation.screens.login.LoginViewModel
import presentation.screens.users.UsersScreen
import presentation.screens.users.UsersViewModel
import presentation.ui.theme.OneDarkProTheme

@Composable
fun NavigationHost() {
   val navController = rememberNavController()

   NavHost(
      navController = navController,
      startDestination = Destinations.Login.route
   ) {
      composable(Destinations.Login.route) {
         val loginViewModel = koinViewModel<LoginViewModel>()

         // hmmm, custom background theme does not apply without scaffold?
         Scaffold {
            LoginScreen(loginViewModel, navController)
         }
      }

      composable(Destinations.Users.route) {
         val usersViewModel = koinViewModel<UsersViewModel>()

         NavigationBarWrapper(navController) {
            UsersScreen(usersViewModel)
         }
      }

      composable(Destinations.Admins.route) {
         val adminsViewModel = koinViewModel<AdminsViewModel>()

         NavigationBarWrapper(navController) {
            AdminsScreen(adminsViewModel)
         }
      }
   }
}