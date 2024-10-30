package presentation.screens.admins

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import presentation.composables.PopupDialog
import presentation.composables.TableCell

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AdminsScreen(adminsViewModel: AdminsViewModel) {
   val popupsQueue by adminsViewModel.popupsQueue.collectAsStateWithLifecycle()

   if (popupsQueue.isNotEmpty()) {
      val item = popupsQueue.first()

      PopupDialog(item.first, item.second) {
         adminsViewModel.dismissPopup()
      }
   }

   val loadingNonInteractable by adminsViewModel.loadingNonInteractable.collectAsStateWithLifecycle()

   if (loadingNonInteractable) {
      AlertDialog(
         onDismissRequest = {},
         title = { Text("Loading...") },

         text = {
            Box(
               modifier = Modifier.fillMaxWidth(),
               contentAlignment = Alignment.Center
            ) {
               CircularProgressIndicator()
            }
         },

         confirmButton = {}
      )
   }

   Admins(adminsViewModel)
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Admins(
   adminsViewModel: AdminsViewModel,
) {
   val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
   val scope = rememberCoroutineScope()

   ModalBottomSheetLayout(
      sheetState = sheetState,

      sheetContent = {
         Row(
            modifier = Modifier.padding(10.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
         ) {
            Column {
               val usernameToEdit by adminsViewModel.usernameToEdit.collectAsStateWithLifecycle()

               OutlinedTextField(
                  label = { Text("Username") },
                  value = usernameToEdit,

                  onValueChange = {
                     adminsViewModel.onUsernameToEditChange(it)
                  }
               )
            }

            Column {
               Button(
                  onClick = { TODO() }
               ) {
                  Text("Update")
               }

               Button(
                  onClick = { TODO() }
               ) {
                  Text("Delete")
               }
            }
         }
      },

      sheetShape = MaterialTheme.shapes.large,
   ) {
      val admins by adminsViewModel.admins.collectAsStateWithLifecycle()

      val column1Weight = .1f
      val column2Weight = .3f
      val column3Weight = .2f

      LazyColumn(
         modifier = Modifier.fillMaxSize()
      ) {
         item {
            Row(
               modifier = Modifier.background(Color.Magenta)
            ) {
               TableCell(column1Weight) {
                  Text(" ")
               }

               TableCell(column2Weight) {
                  Text("ID")
               }

               TableCell(column2Weight) {
                  Text("USERNAME")
               }

               TableCell(column2Weight) {
                  Text("HASHED PASSWORD")
               }

               TableCell(column3Weight) {
                  Text(" ")
               }
            }

            Divider()
         }

         admins.forEachIndexed { index, admin ->
            item {
               Row(
                  modifier = Modifier.background(
                     if (index % 2 == 0) {
                        Color.LightGray
                     } else {
                        Color.White
                     }
                  )
               ) {
                  TableCell(column1Weight) {
                     Text("${index + 1}")
                  }

                  TableCell(column2Weight) {
                     Text(admin.id.toString())
                  }

                  TableCell(column2Weight) {
                     Text(admin.username)
                  }

                  TableCell(column2Weight) {
                     Text(admin.hashedPassword)
                  }

                  TableCell(column3Weight) {
                     Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                     ) {
                        Button(
                           onClick = {
                              adminsViewModel.onIdToEditChange(admin.id)
                              adminsViewModel.onUsernameToEditChange(admin.username)
                              scope.launch { sheetState.show() }
                           },
                        ) {
                           Text("Edit")
                        }
                     }
                  }
               }

               Divider()
            }
         }
      }
   }
}