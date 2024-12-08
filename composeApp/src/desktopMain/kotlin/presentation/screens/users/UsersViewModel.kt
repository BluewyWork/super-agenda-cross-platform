package presentation.screens.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.UserUseCase
import domain.models.Membership
import domain.models.UserForAdminView
import domain.models.UserForCreate
import domain.models.UserForUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
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
   val users: StateFlow<List<UserForAdminView>> = _users.onStart {
      refreshUsers {}
   }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT), emptyList())

   private val _usersFiltered = MutableStateFlow<List<UserForAdminView>>(emptyList())
   val usersFiltered: StateFlow<List<UserForAdminView>> = _usersFiltered

   private val _usernameToSearch = MutableStateFlow("")
   val usernameToSearch: StateFlow<String> = _usernameToSearch

   private val _userCreateState = MutableStateFlow(UserCreateState())
   val userCreateState: StateFlow<UserCreateState> = _userCreateState

   private val _userUpdateState = MutableStateFlow(UserUpdateState())
   val userUpdateState: StateFlow<UserUpdateState> = _userUpdateState

   private val _stateToShow = MutableStateFlow(StateToShow.NONE)
   val stateToShow: StateFlow<StateToShow> = _stateToShow

   // Utilities

   fun enqueuePopup(title: String, description: String, error: String = "") {
      _popupsQueue.value = _popupsQueue.value.plus(Triple(title, description, error))
   }

   fun dismissPopup() {
      _popupsQueue.value = _popupsQueue.value.drop(1)
   }

   // Main
   fun onStateToShowChanged(stateToShow: StateToShow) {
      _stateToShow.value = stateToShow
   }

   fun onUsernameToSearchChange(username: String) {
      _usernameToSearch.value = username
   }

   fun onUserForUpdateSelected(userId: ObjectId) {
      val user = _users.value.find { it.id == userId }

      if (user == null) {
         enqueuePopup("ERROR", "Data mismatch...")
         return
      }

      _userUpdateState.update {
         it.copy(
            id = userId, username = user.username, membership = user.membership
         )
      }
   }

   fun onSearchPress() {
      _usernameToSearch.value.ifBlank {
         _usersFiltered.value = _users.value
         return
      }

      _usersFiltered.value = _users.value.filter {
         it.username.contains(_usernameToSearch.value, true)
      }
   }

   fun onUsernameToUpdateChanged(username: String) {
      _userUpdateState.update {
         it.copy(username = username)
      }
   }

   fun onMembershipToUpdateChanged(membership: Membership) {
      _userUpdateState.update {
         it.copy(membership = membership)
      }
   }

   fun onUsernameForCreateChanged(username: String) {
      _userCreateState.update {
         it.copy(username = username)
      }
   }

   fun onPasswordForCreateChanged(password: String) {
      _userCreateState.update {
         it.copy(password = password)
      }
   }

   fun onPasswordConfirmForCreateChanged(passwordConfirm: String) {
      _userCreateState.update {
         it.copy(passwordConfirm = passwordConfirm)
      }
   }


   fun onRefreshPress() {
      refreshUsers {
         if (_usernameToSearch.value.isNotEmpty()) {
            onSearchPress()
         }
      }
   }

   fun onCreatePressed() {
      viewModelScope.launch {
         val username = userCreateState.value.username
         val password = userCreateState.value.password
         val passwordConfirm = userCreateState.value.passwordConfirm

         if (username.isBlank()) {
            enqueuePopup("ERROR", "Username can not be blank...")
            return@launch
         }

         if (password.isBlank()) {
            enqueuePopup("ERROR", "Password can not be blank...")
            return@launch
         }

         if (password != passwordConfirm) {
            enqueuePopup("ERROR", "Passwords does not match...")
            return@launch
         }

         val resultCreateUserForCreateAtApi =
            userUseCase.createUserAtApi(UserForCreate(ObjectId(), username, password))

         when (resultCreateUserForCreateAtApi) {
            is Result.Error -> enqueuePopup(
               "ERROR", "Failed to create user...", resultCreateUserForCreateAtApi.error.toString()
            )

            is Result.Success -> {
               enqueuePopup("INFO", "Successfully created user!")
               resetUserCreateState()
               refreshUsers {}
               _stateToShow.value = StateToShow.NONE
            }
         }
      }
   }

   fun onUpdatePress() {
      viewModelScope.launch {
         val id = _userUpdateState.value.id
         val username = _userUpdateState.value.username
         val membership = _userUpdateState.value.membership

         if (username.isBlank()) {
            enqueuePopup("ERROR", "Username can not be blank...")
            return@launch
         }

         when (val resultUpdateUserForUpdateAtApi = userUseCase.updateUserForUpdateAtApi(
            id, UserForUpdate(
               username, membership
            )
         )) {
            is Result.Error -> enqueuePopup(
               "ERROR", "Failed to update...", resultUpdateUserForUpdateAtApi.error.toString()
            )

            is Result.Success -> {
               enqueuePopup("INFO", "Successfully updated!")
               refreshUsers {}
               _stateToShow.value = StateToShow.NONE
            }
         }
      }
   }

   fun onDeletePress() {
      viewModelScope.launch {
         val id = _userUpdateState.value.id

         when (val resultDeleteUserAtApi = userUseCase.deleteUserAtApi(id)) {
            is Result.Error -> enqueuePopup(
               "ERROR", "Failed to delete user...", resultDeleteUserAtApi.error.toString()
            )

            is Result.Success -> {
               enqueuePopup("INFO", "Successfully deleted user!")
               refreshUsers {}
               _stateToShow.value = StateToShow.NONE
            }
         }
      }
   }

   fun refreshUsers(callback: (List<UserForAdminView>) -> Unit) {
      viewModelScope.launch {
         when (val usersResult = userUseCase.retrieveAllUsersForAdminViewFromApi()) {
            is Result.Error -> enqueuePopup(
               "ERROR", "Failed to refresh users...", usersResult.error.toString()
            )

            is Result.Success -> {
               _users.value = usersResult.data
               _usersFiltered.value = usersResult.data
               callback(usersResult.data)
            }
         }
      }
   }

   fun resetUserCreateState() {
      _userCreateState.update {
         it.copy(username = "", password = "", passwordConfirm = "")
      }
   }
}

enum class StateToShow {
   CREATE, UPDATE, NONE
}

data class UserCreateState(
   val username: String = "", val password: String = "", val passwordConfirm: String = ""
)

data class UserUpdateState(
   val id: ObjectId = ObjectId(),
   val username: String = "",
   val membership: Membership = Membership.FREE
)