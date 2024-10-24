package presentation.screens.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import core.Destinations
import presentation.composables.PopupDialog

// header, footer setup...
@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavController) {
   val popupsQueue by loginViewModel.popupsQueue.collectAsState()
   val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()

   if (popupsQueue.isNotEmpty()) {
      val item = popupsQueue.first()
      PopupDialog(item.first, item.second) {
         println("dismiss")
         println(popupsQueue.toString())
         loginViewModel.dismissPopup()
      }
   }

   if (isLoggedIn && popupsQueue.isEmpty() ) {
      navController.navigate(Destinations.Example.route)
   }


   Login(loginViewModel, navController)
}

// this is the body
@Composable
fun Login(loginViewModel: LoginViewModel, navController: NavController) {
   val username by loginViewModel.username.collectAsState()
   val password by loginViewModel.password.collectAsState()

   Column() {
      Text("Username")

      OutlinedTextField(
         value = username,
         onValueChange = { loginViewModel.onUsernameChange(it) }
      )

      Text("Password")

      OutlinedTextField(
         value = password,
         onValueChange = { loginViewModel.onPasswordChange(it) }
      )
   }
}