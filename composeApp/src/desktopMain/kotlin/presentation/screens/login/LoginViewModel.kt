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
import util.NetworkError
import util.onError
import util.onSuccess

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

   private val _isProccessingLogin = MutableStateFlow(false)
   val isProccesingLogin: StateFlow<Boolean> = _isProccessingLogin

   private val _popupsQueue = MutableStateFlow<List<Pair<String, String>>>(emptyList())
   val popupsQueue: StateFlow<List<Pair<String, String>>> = _popupsQueue

   private fun isLoggedIn() {
      viewModelScope.launch {

      }
   }

   fun enqueuePopup(title: String, description: String) {
      _popupsQueue.value = _popupsQueue.value.plus(Pair(title, description))
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
         _isProccessingLogin.value = true
         val result = authenticationUseCase.login(_username.value, _password.value)

         result.onError {
            when (it) {
               NetworkError.REQUEST_TIMEOUT -> enqueuePopup("ERROR", "Request Timeout...")
               NetworkError.UNAUTHORIZED -> enqueuePopup("ERROR", "Unauthorized...")
               NetworkError.CONFLICT -> enqueuePopup("ERROR", "Conflict...")
               NetworkError.TOO_MANY_REQUESTS -> enqueuePopup("ERROR", "Too many requests...")
               NetworkError.NO_INTERNET -> enqueuePopup("ERROR", "No Internet")
               NetworkError.PAYLOAD_TOO_LARGE -> enqueuePopup("ERROR", "Paylaod too big...")
               NetworkError.SERVER_ERROR -> enqueuePopup("ERROR", "Server Error")
               NetworkError.SERIALIZATION -> enqueuePopup("ERROR", "Serialization...")
               NetworkError.UNKNOWN -> enqueuePopup("ERROR", "Uknowon...")
            }
         }

         result.onSuccess {
            enqueuePopup("INFO", "Successfully logged in!")
         }

         _isProccessingLogin.value = false
      }
   }
}