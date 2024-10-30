package presentation.screens.admins

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.AdminUseCase
import domain.models.Admin
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
   private val _popupsQueue = MutableStateFlow<List<Pair<String, String>>>(emptyList())
   val popupsQueue: StateFlow<List<Pair<String, String>>> = _popupsQueue

   private val _loadingNonInteractable = MutableStateFlow<Boolean>(false)
   val loadingNonInteractable: StateFlow<Boolean> = _loadingNonInteractable

   private val _admins = MutableStateFlow<List<Admin>>(emptyList())
   val admins: StateFlow<List<Admin>> = _admins
      .onStart {
         viewModelScope.launch {
            when (val usersResult = adminUseCase.retrieveAllAdminsFromApi()) {
               is Result.Error -> enqueuePopup("ERROR", usersResult.error.toString())
               is Result.Success -> _admins.value = usersResult.data
            }
         }
      }
      .stateIn(viewModelScope, SharingStarted.WhileSubscribed(Constants.FLOW_TIMEOUT), emptyList())

   private val _adminToEdit =
      MutableStateFlow<Admin>(Admin(ObjectId(), "placeholder", "placeholder"))
   val adminToEdit: StateFlow<Admin> = _adminToEdit

   private val _idToEdit = MutableStateFlow(ObjectId())
   val idToEdit: StateFlow<ObjectId> = _idToEdit

   private val _usernameToEdit = MutableStateFlow("")
   val usernameToEdit: StateFlow<String> = _usernameToEdit

   fun enqueuePopup(title: String, description: String) {
      _popupsQueue.value = _popupsQueue.value.plus(Pair(title, description))
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

   fun onDeletePress() {
      viewModelScope.launch {

      }
   }
}