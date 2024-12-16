package presentation.screens.NavigationBarWrapper

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.AuthenticationUseCase
import kotlinx.coroutines.launch

class NavigationWrapperViewModel constructor(
private val authenticationUseCase: AuthenticationUseCase
) : ViewModel() {
   fun onLogoutPressed(onSuccess: () -> Unit) {
      viewModelScope.launch {
         authenticationUseCase.logout()
         onSuccess()
      }
   }
}