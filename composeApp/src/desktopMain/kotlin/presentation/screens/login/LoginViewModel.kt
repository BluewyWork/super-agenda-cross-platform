package presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.AuthenticationUseCase
import kotlinx.coroutines.delay
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

   private val _isLoggedIn = MutableStateFlow(false)
   val isLoggedIn: StateFlow<Boolean> = _isLoggedIn
      .onStart { isLoggedIn() }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT), false)

   private val _isProcessingLogin = MutableStateFlow(false)
   val isProcessingLogin: StateFlow<Boolean> = _isProcessingLogin

   private val _popupsQueue = MutableStateFlow<List<Triple<String, String, String>>>(emptyList())
   val popupsQueue: StateFlow<List<Triple<String, String, String>>> = _popupsQueue

   private fun isLoggedIn() {
      viewModelScope.launch {
         _isLoggedIn.value = authenticationUseCase.checkIfLoggedIn()
      }
   }

   fun enqueuePopup(title: String, description: String, error: String =  "") {
      _popupsQueue.value = _popupsQueue.value.plus(Triple(title, description, error))
   }

   fun dismissPopup() {
      _popupsQueue.value = _popupsQueue.value.drop(1)
   }

   private suspend fun whenPopupsEmpty(code: () -> Unit) {
      while (popupsQueue.value.isNotEmpty()) {
         delay(Constants.POPUPS_INTERVAL)
      }

      code()
   }

   fun onUsernameChange(username: String) {
      _username.value = username
   }

   fun onPasswordChange(password: String) {
      _password.value = password
   }

   fun onLoginPress() {
      viewModelScope.launch {
         _isProcessingLogin.value = true

         when (val result = authenticationUseCase.login(_username.value, _password.value)) {
            is Result.Error -> {
               enqueuePopup("ERROR", "Failed to login...", result.error.toString())
            }

            is Result.Success -> {
               enqueuePopup("INFO", "Successfully logged in!")
               _isLoggedIn.value = true
            }
         }

         _isProcessingLogin.value = false
      }
   }
}