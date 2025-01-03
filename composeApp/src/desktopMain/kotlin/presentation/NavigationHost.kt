package presentation

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import presentation.screens.NavigationBarWrapper.NavigationBarWrapper
import presentation.screens.NavigationBarWrapper.NavigationWrapperViewModel
import presentation.screens.admins.AdminsScreen
import presentation.screens.admins.AdminsViewModel
import presentation.screens.initial.InitialScreen
import presentation.screens.initial.InitialViewModel
import presentation.screens.login.LoginScreen
import presentation.screens.login.LoginViewModel
import presentation.screens.users.UsersScreen
import presentation.screens.users.UsersViewModel

@Composable
fun NavigationHost() {
   val navController = rememberNavController()

   NavHost(
      navController = navController,
      startDestination = Destinations.Initial.route
   ) {
      composable(Destinations.Initial.route) {
         val initialViewModel = koinViewModel<InitialViewModel>()

         Scaffold {
            InitialScreen(initialViewModel, navController)
         }
      }

      composable(Destinations.Login.route) {
         val loginViewModel = koinViewModel<LoginViewModel>()

         // hmmm, custom background theme does not apply without scaffold?
         Scaffold {
            LoginScreen(loginViewModel, navController)
         }
      }

      composable(Destinations.Users.route) {
         val usersViewModel = koinViewModel<UsersViewModel>()
         val navigationWrapperViewModel = koinViewModel<NavigationWrapperViewModel>()

         NavigationBarWrapper(navigationWrapperViewModel, navController) {
            UsersScreen(usersViewModel)
         }
      }

      composable(Destinations.Admins.route) {
         val adminsViewModel = koinViewModel<AdminsViewModel>()
         val navigationWrapperViewModel = koinViewModel<NavigationWrapperViewModel>()

         NavigationBarWrapper(navigationWrapperViewModel, navController) {
            AdminsScreen(adminsViewModel)
         }
      }
   }
}