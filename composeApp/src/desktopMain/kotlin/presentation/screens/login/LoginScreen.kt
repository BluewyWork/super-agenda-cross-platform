package presentation.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import presentation.Destinations
import presentation.composables.PopupDialog

// header, footer setup...
@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavController) {
   val popupsQueue by loginViewModel.popupsQueue.collectAsStateWithLifecycle()
   val isLoggedIn by loginViewModel.isLoggedIn.collectAsStateWithLifecycle()

   if (popupsQueue.isNotEmpty()) {
      val item = popupsQueue.first()
      PopupDialog(item.first, item.second, item.third) {
         println("dismiss")
         println(popupsQueue.toString())
         loginViewModel.dismissPopup()
      }
   }

   if (isLoggedIn && popupsQueue.isEmpty()) {
      navController.navigate(Destinations.Users.route)
   }

   Login(loginViewModel, navController)
}

// this is the body
@Composable
fun Login(loginViewModel: LoginViewModel, navController: NavController) {
   val username by loginViewModel.username.collectAsStateWithLifecycle()
   val password by loginViewModel.password.collectAsStateWithLifecycle()

   Column(
      modifier = Modifier
         .fillMaxSize()
//         .background(oneDarkProBackground)
      ,
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
   ) {
      OutlinedTextField(
         label = { Text("Username ") },
         value = username,
         onValueChange = { loginViewModel.onUsernameChange(it) }
      )

      OutlinedTextField(
         label = { Text("Password") },
         value = password,
         onValueChange = { loginViewModel.onPasswordChange(it) }
      )

      val isProcessingLogin by loginViewModel.isProcessingLogin.collectAsState()

      Button(
         onClick = {
            loginViewModel.onLoginPress()
         },

         enabled = !isProcessingLogin
      ) {
         Text("Login")
      }
   }
}