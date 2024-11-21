package presentation.screens.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.UserUseCase
import domain.models.UserForAdminView
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

   private val _users = MutableStateFlow<List<UserForAdminView>>(emptyList())
   val users: StateFlow<List<UserForAdminView>> = _users
      .onStart {
         refreshUsers {}
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT), emptyList())

   private val _usersFiltered = MutableStateFlow<List<UserForAdminView>>(emptyList())
   val usersFiltered: StateFlow<List<UserForAdminView>> = _usersFiltered

   private val _usernameToSearch = MutableStateFlow("")
   val usernameToSearch: StateFlow<String> = _usernameToSearch

   fun enqueuePopup(title: String, description: String) {
      _popupsQueue.value = _popupsQueue.value.plus(Pair(title, description))
   }

   fun dismissPopup() {
      _popupsQueue.value = _popupsQueue.value.drop(1)
   }

   fun onUsernameToSearchChange(username: String) {
      _usernameToSearch.value = username
   }

   fun onSearchPress() {
      _usernameToSearch.value.ifBlank {
         _usersFiltered.value = _users.value
         return
      }

      _usersFiltered.value =
         _users.value.filter {
            it.username.contains(_usernameToSearch.value, true)
         }
   }

   fun refreshUsers(callback: (List<UserForAdminView>) -> Unit) {
      viewModelScope.launch {
         when (val usersResult = userUseCase.retrieveAllUsersForAdminViewFromApi()) {
            is Result.Error -> enqueuePopup("ERROR", usersResult.error.toString())
            is Result.Success -> {
               _users.value = usersResult.data
               _usersFiltered.value = usersResult.data
               callback(usersResult.data)
            }
         }
      }
   }

   fun onRefreshPress() {
      refreshUsers {
         if (_usernameToSearch.value.isNotEmpty()) {
            onSearchPress()
         }
      }
   }

   fun onDeletePress() {
      TODO()
   }
}