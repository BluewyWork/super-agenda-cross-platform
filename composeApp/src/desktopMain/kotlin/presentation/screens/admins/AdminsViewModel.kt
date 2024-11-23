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
   private val _popupsQueue = MutableStateFlow<List<Triple<String, String, String>>>(emptyList())
   val popupsQueue: StateFlow<List<Triple<String, String, String>>> = _popupsQueue

   // could create a function to wrap code to automatically handle start and end of this
   private val _loadingNonInteractable = MutableStateFlow(false)
   val loadingNonInteractable: StateFlow<Boolean> = _loadingNonInteractable

   private val _admins = MutableStateFlow<List<Admin>>(emptyList())
   val admins: StateFlow<List<Admin>> = _admins
      .onStart {
         viewModelScope.launch {
            refreshAdmins {}
         }
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT), emptyList())

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

   private suspend fun <T> withLoadingNonInteractable(block: suspend () -> T): T {
      _loadingNonInteractable.value = true

      return try {
         block()
      } finally {
         _loadingNonInteractable.value = false
      }
   }

   fun enqueuePopup(title: String, description: String, error: String = "") {
      _popupsQueue.value = _popupsQueue.value.plus(Triple(title, description, error))
   }

   fun dismissPopup() {
      _popupsQueue.value = _popupsQueue.value.drop(1)
   }

   fun onIdToEditChange(id: ObjectId) {
      _idToEdit.value = id
   }

   fun onUsernameToEditChange(username: String) {
      _usernameToEdit.value = username
   }

   private fun refreshAdmins(callback: (List<Admin>) -> Unit) {
      viewModelScope.launch {
         when (val usersResult = adminUseCase.retrieveAllAdminsFromApi()) {
            is Result.Error -> enqueuePopup("ERROR", "Failed to refresh admins...", usersResult.error.toString())
            is Result.Success -> {
               _admins.value = usersResult.data
               _adminsFiltered.value = usersResult.data

               callback(usersResult.data)
            }
         }
      }
   }

   fun onUpdatePress() {
      viewModelScope.launch {
         _loadingNonInteractable.value = true

         val selectedAdmin = admins.value.find { it.id == _idToEdit.value }

         if (selectedAdmin == null) {
            _loadingNonInteractable.value = false
            enqueuePopup("ERROR", "Admin not found...")
            return@launch
         }

         val updatedAdmin =
            AdminForUpdate(usernameToEdit.value)

         val updateResult = adminUseCase.modifyAdminAtApi(selectedAdmin.id, updatedAdmin)
         _loadingNonInteractable.value = false

         when (updateResult) {
            is Result.Error -> enqueuePopup(
               "ERROR",
               "Failed to update admin",
               updateResult.error.toString()
            )

            is Result.Success -> {
               _usernameToCreate.value = ""
               _passwordToCreate.value = ""
               _confirmPasswordToCreate.value = ""
               refreshAdmins {}
               enqueuePopup("INFO", "Successfully updated admin!")
            }
         }
      }
   }

   fun onDeletePress(onSuccess: () -> Unit) {
      viewModelScope.launch {
         _loadingNonInteractable.value = true

         val destroyResult = adminUseCase.destroyAdminAtApi(_idToEdit.value.toHexString())
         _loadingNonInteractable.value = false

         when (destroyResult) {
            is Result.Error -> enqueuePopup("ERROR", "Failed to delete user...", destroyResult.error.toString())

            is Result.Success -> {
               _usernameToCreate.value = ""
               _passwordToCreate.value = ""
               _confirmPasswordToCreate.value = ""
               refreshAdmins {}
               enqueuePopup("INFO", "Successfully deleted admin!")
               onSuccess()
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

      _adminsFiltered.value =
         _admins.value.filter {
            it.username.contains(_adminUsernameToSearch.value, true)
         }
   }

   fun onCreatePress() {
      viewModelScope.launch {
         _loadingNonInteractable.value = true

         val username = usernameToCreate.value
         val password = _passwordToCreate.value
         val passwordConfirm = _confirmPasswordToCreate.value

         if (password != passwordConfirm) {
            enqueuePopup("ERROR", "Passwords does not match...")
            _loadingNonInteractable.value = false
            return@launch
         }

         val newAdmin = Admin(
            ObjectId(),
            username,
            password
         )

         val deleteResult = adminUseCase.spawnAdminAtApi(newAdmin)
         _loadingNonInteractable.value = false

         when (deleteResult) {
            is Result.Error -> enqueuePopup("ERROR", "Failed to delete admin...", deleteResult.error.toString())

            is Result.Success -> {
               _usernameToCreate.value = ""
               _passwordToCreate.value = ""
               _confirmPasswordToCreate.value = ""
               refreshAdmins {}
               enqueuePopup("INFO", "Successfully created admin!")
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
}