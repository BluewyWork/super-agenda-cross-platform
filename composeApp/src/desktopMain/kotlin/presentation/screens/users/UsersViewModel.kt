package presentation.screens.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.UserUseCase
import domain.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import presentation.Constants
import util.Result

class UsersViewModel(
   private val userUseCase: UserUseCase
) : ViewModel() {
   private val _popupsQueue = MutableStateFlow<List<Pair<String, String>>>(emptyList())
   val popupsQueue: StateFlow<List<Pair<String, String>>> = _popupsQueue

   private val _users = MutableStateFlow<List<User>>(emptyList())
   val users: StateFlow<List<User>> = _users
      .onStart {
         viewModelScope.launch {
            when (val usersResult = userUseCase.retrieveAllUsersFromApi()) {
               is Result.Error -> enqueuePopup("ERROR", usersResult.error.toString())
               is Result.Success -> _users.value = usersResult.data
            }
         }
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT), emptyList())

   fun enqueuePopup(title: String, description: String) {
      _popupsQueue.value = _popupsQueue.value.plus(Pair(title, description))
   }

   fun dismissPopup() {
      _popupsQueue.value = _popupsQueue.value.drop(1)
   }
}