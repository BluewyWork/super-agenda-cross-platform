package presentation.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import presentation.Constants

class LoginViewModel : ViewModel() {
   private val _username = MutableStateFlow("")
   val username: StateFlow<String> = _username

   private val _password = MutableStateFlow("")
   val password: StateFlow<String> = _password

   private val _isLoggedIn = MutableStateFlow(false)
   val isLoggedIn: StateFlow<Boolean> = _isLoggedIn
      .onStart { isLoggedIn() }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT), false)

   private val _popupsQueue = MutableStateFlow<List<Pair<String, String>>>(emptyList())
   val popupsQueue: StateFlow<List<Pair<String, String>>> = _popupsQueue

   private fun isLoggedIn() {
      viewModelScope.launch {
         // simulation
         _isLoggedIn.value = false
         delay(3000L)
         _isLoggedIn.value = true
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
}