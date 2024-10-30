package presentation.screens.users

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import presentation.composables.PopupDialog

@Composable
fun UsersScreen(usersViewModel: UsersViewModel) {
   val popupsQueue by usersViewModel.popupsQueue.collectAsStateWithLifecycle()

   if (popupsQueue.isNotEmpty()) {
      val item = popupsQueue.first()
      PopupDialog(item.first, item.second) {
         println("dismiss")
         println(popupsQueue.toString())
         usersViewModel.dismissPopup()
      }
   }

   Users(usersViewModel)
}

@Composable
fun Users(usersViewModel: UsersViewModel) {
   val users by usersViewModel.users.collectAsStateWithLifecycle()

   Column {

   }
}

@Composable
fun TableCell() {

}