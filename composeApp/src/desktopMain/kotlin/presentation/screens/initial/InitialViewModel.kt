package presentation.screens.initial

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

class InitialViewModel(
   private val authenticationUseCase: AuthenticationUseCase
) : ViewModel() {
   private val _loggedIn = MutableStateFlow(false)

   val loggedIn: StateFlow<Boolean> = _loggedIn
      .onStart { checkIfLoggedIn() }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT), false)

   private fun checkIfLoggedIn() {
      viewModelScope.launch {
         _loggedIn.value = authenticationUseCase.checkIfLoggedIn()
      }
   }
}