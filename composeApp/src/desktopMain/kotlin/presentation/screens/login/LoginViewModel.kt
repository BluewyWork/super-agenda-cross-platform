package presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.AuthenticationUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import presentation.Constants
import util.Result

class LoginViewModel(
   private val authenticationUseCase: AuthenticationUseCase
) : ViewModel() {
   private val _username = MutableStateFlow("")
   val username: StateFlow<String> = _username

   private val _password = MutableStateFlow("")
   val password: StateFlow<String> = _password


   private val _popups = MutableStateFlow<List<Popup>>(emptyList())
   val popups: StateFlow<List<Popup>> = _popups


   fun onPopupDismissed() {
      _popups.value = _popups.value.drop(1)
   }

   fun onUsernameChange(username: String) {
      _username.value = username
   }

   fun onPasswordChange(password: String) {
      _password.value = password
   }

   fun onLoginPress(onSuccess: () -> Unit) {
      viewModelScope.launch {
         when (val result = authenticationUseCase.login(_username.value, _password.value)) {
            is Result.Error -> {
               _popups.value += Popup("ERROR", "Failed to login...", result.error.toString())
            }

            is Result.Success -> {
               _popups.value += Popup("INFO", "Successfully logged in!") {
                  onSuccess()
               }
            }
         }
      }
   }
}

data class Popup(
   val title: String = "",
   val description: String = "",
   val error: String = "",
   val code: () -> Unit = {}
)