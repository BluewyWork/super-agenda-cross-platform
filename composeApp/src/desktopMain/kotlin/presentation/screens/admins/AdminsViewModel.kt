package presentation.screens.admins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.AdminUseCase
import domain.models.Admin
import domain.models.AdminForUpdate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.bson.types.ObjectId
import presentation.Constants
import util.Result

class AdminsViewModel(
   private val adminUseCase: AdminUseCase
) : ViewModel() {
   private val _popups = MutableStateFlow<List<Popup>>(emptyList())
   val popups: StateFlow<List<Popup>> = _popups

   private val _admins = MutableStateFlow<List<Admin>>(emptyList())
   val admins: StateFlow<List<Admin>> = _admins.onStart {
      viewModelScope.launch {
         refreshAdmins {}
      }
   }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT), emptyList())

   private val _adminsFiltered = MutableStateFlow<List<Admin>>(emptyList())
   val adminsFiltered: StateFlow<List<Admin>> = _adminsFiltered

   private val _idToEdit = MutableStateFlow(ObjectId())

   private val _usernameToEdit = MutableStateFlow("")
   val usernameToEdit: StateFlow<String> = _usernameToEdit

   private val _bottomSheetContentState = MutableStateFlow(BottomSheetContentState.NONE)
   val bottomSheetContentState: StateFlow<BottomSheetContentState> = _bottomSheetContentState

   private val _usernameToCreate = MutableStateFlow("")
   val usernameToCreate: StateFlow<String> = _usernameToCreate

   private val _passwordToCreate = MutableStateFlow("")
   val passwordToCreate: StateFlow<String> = _passwordToCreate

   private val _confirmPasswordToCreate = MutableStateFlow("")
   val confirmPasswordToCreate: StateFlow<String> = _confirmPasswordToCreate

   private val _adminUsernameToSearch = MutableStateFlow("")
   val adminUsernameToSearch: StateFlow<String> = _adminUsernameToSearch

   fun onIdToEditChange(id: ObjectId) {
      _idToEdit.value = id
   }

   fun onUsernameToEditChange(username: String) {
      _usernameToEdit.value = username
   }

   private fun refreshAdmins(callback: (List<Admin>) -> Unit) {
      viewModelScope.launch {
         when (val usersResult = adminUseCase.retrieveAllAdminsFromApi()) {
            is Result.Error ->
               _popups.value += Popup(
                  "ERROR",
                  "Failed to refresh admins...",
                  usersResult.error.toString()
               )

            is Result.Success -> {
               _admins.value = usersResult.data
               _adminsFiltered.value = usersResult.data

               callback(usersResult.data)
            }
         }
      }
   }

   fun onUpdatePress(onSuccess: () -> Unit) {
      viewModelScope.launch {
         val selectedAdmin = admins.value.find { it.id == _idToEdit.value }

         if (selectedAdmin == null) {
            _popups.value += Popup("ERROR", "Admin not found...")
            return@launch
         }

         val updatedAdmin = AdminForUpdate(usernameToEdit.value)

         when (val updateResult = adminUseCase.modifyAdminAtApi(selectedAdmin.id, updatedAdmin)) {
            is Result.Error -> _popups.value += Popup(
               "ERROR", "Failed to update admin", updateResult.error.toString()
            )

            is Result.Success -> {
               _usernameToCreate.value = ""
               _passwordToCreate.value = ""
               _confirmPasswordToCreate.value = ""
               refreshAdmins {}

               _popups.value += Popup("INFO", "Successfully updated admin!") {
                  onSuccess()
               }
            }
         }
      }
   }

   fun onDeletePress(onSuccess: () -> Unit) {
      viewModelScope.launch {
         when (val destroyResult = adminUseCase.destroyAdminAtApi(_idToEdit.value.toHexString())) {
            is Result.Error -> _popups.value += Popup(
               "ERROR", "Failed to delete user...", destroyResult.error.toString()
            )

            is Result.Success -> {
               _usernameToCreate.value = ""
               _passwordToCreate.value = ""
               _confirmPasswordToCreate.value = ""
               refreshAdmins {}

               _popups.value += Popup("INFO", "Successfully deleted admin!") {
                  onSuccess()
               }
            }
         }
      }
   }

   fun onBottomSheetContentStateChange(bottomSheetContentState: BottomSheetContentState) {
      _bottomSheetContentState.value = bottomSheetContentState
   }

   fun onUsernameToCreateChange(username: String) {
      _usernameToCreate.value = username
   }

   fun onPasswordToCreateChange(password: String) {
      _passwordToCreate.value = password
   }

   fun onConfirmPasswordToCreateChange(confirmPassword: String) {
      _confirmPasswordToCreate.value = confirmPassword
   }

   fun onAdminUsernameToSearchChange(username: String) {
      _adminUsernameToSearch.value = username
   }

   fun onSearchPress() {
      _adminUsernameToSearch.value.ifBlank {
         _adminsFiltered.value = _admins.value
         return
      }

      _adminsFiltered.value = _admins.value.filter {
         it.username.contains(_adminUsernameToSearch.value, true)
      }
   }

   fun onCreatePress(onSuccess: () -> Unit) {
      viewModelScope.launch {
         val username = usernameToCreate.value
         val password = _passwordToCreate.value
         val passwordConfirm = _confirmPasswordToCreate.value

         if (password != passwordConfirm) {
            _popups.value += Popup("ERROR", "Passwords does not match...")
            return@launch
         }

         val newAdmin = Admin(
            ObjectId(), username, password
         )

         when (val resultCreateAdminAtApi = adminUseCase.spawnAdminAtApi(newAdmin)) {
            is Result.Error ->
               _popups.value += Popup(
                  "ERROR",
                  "Failed to create admin...",
                  resultCreateAdminAtApi.error.toString()
               )

            is Result.Success -> {
               _usernameToCreate.value = ""
               _passwordToCreate.value = ""
               _confirmPasswordToCreate.value = ""
               refreshAdmins {}

               _popups.value += Popup("INFO", "Successfully created admin", "!") {
                  onSuccess()
               }
            }
         }
      }
   }

   fun onRefreshPress() {
      refreshAdmins {
         if (_adminUsernameToSearch.value.isNotBlank()) {
            onSearchPress()
         }
      }
   }

   fun onPopupDismissed() {
      _popups.value = _popups.value.drop(1)
   }
}

enum class BottomSheetContentState {
   NONE, CREATE, UPDATE
}

data class Popup(
   val title: String = "",
   val description: String = "",
   val error: String = "",
   val code: () -> Unit = {}
)