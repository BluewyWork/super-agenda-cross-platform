package presentation.screens.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.UserUseCase
import domain.models.Membership
import domain.models.UserForAdminView
import domain.models.UserForUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import presentation.Constants
import util.Result

class UsersViewModel(
   private val userUseCase: UserUseCase
) : ViewModel() {
   private val _popupsQueue = MutableStateFlow<List<Triple<String, String, String>>>(emptyList())
   val popupsQueue: StateFlow<List<Triple<String, String, String>>> = _popupsQueue

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

   private val _usernameToUpdate = MutableStateFlow("")
   val usernameToUpdate: StateFlow<String> = _usernameToUpdate

   private val _membershipToUpdate = MutableStateFlow(Membership.FREE)
   val membershipToUpdate: StateFlow<Membership> = _membershipToUpdate

   private val _selectedUserId = MutableStateFlow(ObjectId())

   fun enqueuePopup(title: String, description: String, error: String = "") {
      _popupsQueue.value = _popupsQueue.value.plus(Triple(title, description, error))
   }

   fun dismissPopup() {
      _popupsQueue.value = _popupsQueue.value.drop(1)
   }

   fun onUsernameToSearchChange(username: String) {
      _usernameToSearch.value = username
   }

   fun onSelectedUserChange(userId: ObjectId) {
      _selectedUserId.value = userId
   }

   fun onMembershipToUpdateChange(membership: Membership) {
      _membershipToUpdate.value = membership
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

   fun onUsernameToUpdateChange(username: String) {
      _usernameToUpdate.value = username
   }

   fun refreshUsers(callback: (List<UserForAdminView>) -> Unit) {
      viewModelScope.launch {
         when (val usersResult = userUseCase.retrieveAllUsersForAdminViewFromApi()) {
            is Result.Error -> enqueuePopup("ERROR", "Failed to refresh users...", usersResult.error.toString())
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

   fun onUpdatePress() {
      viewModelScope.launch {
         val selectedUser = _users.value.find { it.id === _selectedUserId.value }

         if (selectedUser == null) {
            enqueuePopup("ERROR", "User not found...")
            return@launch
         }

         val username = _usernameToUpdate.value.ifBlank {
            enqueuePopup("ERROR", "Username can't be empty...")
            return@launch
         }

         val membership = _membershipToUpdate.value

         when (
            val resultUpdateUserForUpdateAtApi = userUseCase.updateUserForUpdateAtApi(
               selectedUser.id,
               UserForUpdate(
                  username,
                  membership
               )
            )) {
            is Result.Error -> enqueuePopup("ERROR", "Failed to update...")
            is Result.Success -> enqueuePopup("INFO", "Successfully updated!")
         }
      }
   }

   fun onDeletePress() {
      viewModelScope.launch {
         val id = _selectedUserId.value

         when (val resultDeleteUserAtApi = userUseCase.deleteUserAtApi(id)) {
            is Result.Error -> enqueuePopup("ERROR", "Failed to delete user...")
            is Result.Success -> enqueuePopup("INFO", "Successfully deleted user!")
         }
      }
   }
}