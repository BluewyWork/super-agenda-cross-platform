package presentation.screens.admins

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import presentation.Constants
import presentation.composables.PopupDialog
import presentation.composables.TableCell
import presentation.ui.theme.oneDarkProBackground
import presentation.ui.theme.oneDarkProSurface

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
         val bottomSheetContentState by adminsViewModel.bottomSheetContentState.collectAsStateWithLifecycle()

         when (bottomSheetContentState) {
            BottomSheetContentState.NONE -> Text("Nothing to see here...")
            BottomSheetContentState.CREATE -> BottomSheetContentForCreate(adminsViewModel)
            BottomSheetContentState.UPDATE -> BottomSheetContentForUpdate(adminsViewModel)
         }
      },

      sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
   ) {
      Column(
         modifier = Modifier
            .fillMaxSize()
      ) {
         Row(
            modifier = Modifier
               .fillMaxWidth()
               .padding(Constants.SPACE.dp),

            verticalAlignment = Alignment.CenterVertically
         ) {
            Row(
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.Center
            ) {
               val adminUsernameToSearch by adminsViewModel.adminUsernameToSearch.collectAsStateWithLifecycle()

               OutlinedTextField(
                  label = { Text("Search Username") },
                  value = adminUsernameToSearch,
                  onValueChange = { adminsViewModel.onAdminUsernameToSearchChange(it) },
               )

               Spacer(modifier = Modifier.size(Constants.SPACE.dp))

               Button(
                  onClick = { adminsViewModel.onSearchPress() },
               ) {
                  Icon(Icons.Default.Search, "Search")
               }

               Spacer(modifier = Modifier.size(Constants.SPACE.dp))

               Button(
                  onClick = {
                     adminsViewModel.onRefreshPress()
                  }
               ) {
                  Icon(Icons.Default.Refresh, "Refresh")
               }
            }

            Row(
               modifier = Modifier
                  .fillMaxWidth(),
               horizontalArrangement = Arrangement.End,
               verticalAlignment = Alignment.CenterVertically
            ) {
               Button(
                  onClick = {
                     adminsViewModel.onBottomSheetContentStateChange(BottomSheetContentState.CREATE)
                     scope.launch { sheetState.show() }
                  },
               ) {
                  Icon(Icons.Default.Add, "New Admin")
               }
            }
         }

         Spacer(modifier = Modifier.size(Constants.SPACE.dp))

         val column1Weight = .1f
         val column2Weight = .3f
         val column3Weight = .2f

         Row {
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

         // since it is a cold flow it needs something to be observing it to action
         val admins by adminsViewModel.admins.collectAsStateWithLifecycle()
         val adminsProcessed by adminsViewModel.adminsFiltered.collectAsStateWithLifecycle()

         LazyColumn(
            modifier = Modifier
               .fillMaxSize()
         ) {
            adminsProcessed.forEachIndexed { index, admin ->
               item {
                  Row(
                     modifier = Modifier.background(
                        if (index % 2 == 0) {
                           oneDarkProSurface
                        } else {
                           oneDarkProBackground
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
                                 adminsViewModel.onBottomSheetContentStateChange(
                                    BottomSheetContentState.UPDATE
                                 )

                                 adminsViewModel.onIdToEditChange(admin.id)
                                 adminsViewModel.onUsernameToEditChange(admin.username)
                                 scope.launch { sheetState.show() }
                              },
                           ) {
                              Icon(Icons.Default.Edit, "Edit")
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }
}

@Composable
fun BottomSheetContentForUpdate(adminsViewModel: AdminsViewModel) {
   Row(
      modifier = Modifier
         .padding(Constants.SPACE.dp)
         .fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
   ) {
      Column(
         modifier = Modifier
            .weight(.5f)
            .fillMaxWidth()
      ) {
         val usernameToEdit by adminsViewModel.usernameToEdit.collectAsStateWithLifecycle()

         OutlinedTextField(
            label = { Text("Username") },
            value = usernameToEdit,

            onValueChange = {
               adminsViewModel.onUsernameToEditChange(it)
            },

            modifier = Modifier
               .fillMaxWidth()
               .padding(Constants.SPACE.dp)
         )
      }

      Spacer(modifier = Modifier.width(Constants.SPACE.dp))

      Column(
         modifier = Modifier
            .weight(.5f)
            .fillMaxWidth(),
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         Button(
            onClick = {
               adminsViewModel.onUpdatePress()
            },

            modifier = Modifier
               .fillMaxWidth()
               .padding(start = Constants.SPACE.dp, end = Constants.SPACE.dp)
         ) {
            Text("Update")
         }

         Spacer(modifier = Modifier.width(Constants.SPACE.dp))

         Button(
            onClick = { adminsViewModel.onDeletePress() },

            modifier = Modifier
               .fillMaxWidth()
               .padding(start = Constants.SPACE.dp, end = Constants.SPACE.dp)
         ) {
            Text("Delete")
         }
      }
   }
}

@Composable
fun BottomSheetContentForCreate(adminsViewModel: AdminsViewModel) {
   Row(
      modifier = Modifier
         .padding(Constants.SPACE.dp)
         .fillMaxWidth(),

      verticalAlignment = Alignment.CenterVertically
   ) {
      Column(
         modifier = Modifier
            .weight(.5f)
            .fillMaxWidth()
      ) {
         val username by adminsViewModel.usernameToCreate.collectAsStateWithLifecycle()
         val password by adminsViewModel.passwordToCreate.collectAsStateWithLifecycle()
         val confirmPassword by adminsViewModel.confirmPasswordToCreate.collectAsStateWithLifecycle()

         OutlinedTextField(
            label = { Text("Username") },
            value = username,
            onValueChange = { adminsViewModel.onUsernameToCreateChange(it) }
         )

         OutlinedTextField(
            label = { Text("Password") },
            value = password,
            onValueChange = { adminsViewModel.onPasswordToCreateChange(it) }
         )

         OutlinedTextField(
            label = { Text("Confirm Password") },
            value = confirmPassword,
            onValueChange = { adminsViewModel.onConfirmPasswordToCreateChange(it) }
         )
      }

      Column(
         modifier = Modifier
            .weight(.5f)
            .fillMaxWidth()
      ) {
         Button(
            onClick = {
               adminsViewModel.onCreatePress()
            },

            modifier = Modifier
               .fillMaxWidth()
               .padding(Constants.SPACE.dp)
         ) {
            Text("Create")
         }
      }
   }
}