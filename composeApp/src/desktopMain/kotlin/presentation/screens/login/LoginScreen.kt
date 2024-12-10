package presentation.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import presentation.Destinations

// header, footer setup...
@Composable
fun LoginScreen(loginViewModel: LoginViewModel, navController: NavController) {
   val popups by loginViewModel.popups.collectAsStateWithLifecycle()

   if (popups.isNotEmpty()) {
      val popup = popups.first()

      AlertDialog(onDismissRequest = {
         popup.code()
         loginViewModel.onPopupDismissed()
      },

         title = { Text(popup.title) },

         text = {
            Column {
               if (popup.error.isNotBlank()) {
                  Text(popup.error)
               }

               Text(popup.description)
            }
         },

         confirmButton = {
            Button(onClick = {
               popup.code()
               loginViewModel.onPopupDismissed()
            }) {
               Text("OK")
            }
         })
   }

   Login(loginViewModel, navController)
}

// this is the body
@Composable
fun Login(loginViewModel: LoginViewModel, navController: NavController) {
   val username by loginViewModel.username.collectAsStateWithLifecycle()
   val password by loginViewModel.password.collectAsStateWithLifecycle()

   Column(
      modifier = Modifier.fillMaxSize(),

      verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
   ) {
      OutlinedTextField(label = { Text("Username ") },
         value = username,
         onValueChange = { loginViewModel.onUsernameChange(it) })

      OutlinedTextField(
         label = { Text("Password") },
         value = password,
         onValueChange = { loginViewModel.onPasswordChange(it) },
         visualTransformation = PasswordVisualTransformation(),
         keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
      )

      Button(
         onClick = {
            loginViewModel.onLoginPress {
               navController.navigate(Destinations.Users.route)
            }
         },
      ) {
         Text("Login")
      }
   }
}