package presentation.screens.users

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
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
import domain.models.Membership
import kotlinx.coroutines.launch
import presentation.Constants
import presentation.composables.DropdownMenuGeneric
import presentation.composables.PopupDialog
import presentation.composables.TableCell
import presentation.ui.theme.oneDarkProBackground
import presentation.ui.theme.oneDarkProSurface

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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun Users(usersViewModel: UsersViewModel) {
   val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
   val scope = rememberCoroutineScope()

   ModalBottomSheetLayout(
      sheetState = sheetState,

      sheetContent = {
         BottomSheetContent(usersViewModel)
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
               val usernameToSearch by usersViewModel.usernameToSearch.collectAsStateWithLifecycle()

               OutlinedTextField(
                  label = { Text("Search Username") },
                  value = usernameToSearch,
                  onValueChange = { usersViewModel.onUsernameToSearchChange(it) }
               )

               Spacer(modifier = Modifier.size(Constants.SPACE.dp))

               Button(
                  onClick = {
                     usersViewModel.onSearchPress()
                  }
               ) {
                  Icon(Icons.Default.Search, "Search")
               }

               Spacer(modifier = Modifier.size(Constants.SPACE.dp))

               Button(
                  onClick = {
                     usersViewModel.onRefreshPress()
                  }
               ) {
                  Icon(Icons.Default.Refresh, "Refresh")
               }
            }
         }

         Spacer(modifier = Modifier.size(Constants.SPACE.dp))

         val users by usersViewModel.users.collectAsStateWithLifecycle()
         val usersProcessed by usersViewModel.usersFiltered.collectAsStateWithLifecycle()

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
               Text("NUM_TASKS")
            }

            TableCell(column2Weight) {
               Text("PLAN")
            }

            TableCell(column3Weight) {
               Text(" ")
            }
         }

         Divider()

         LazyColumn(
            modifier = Modifier.fillMaxSize()
         ) {
            usersProcessed.forEachIndexed { index, user ->
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
                        Text(user.id.toString())
                     }

                     TableCell(column2Weight) {
                        Text(user.username)
                     }

                     TableCell(column2Weight) {
                        Text("${user.tasksSize}")
                     }

                     TableCell(column2Weight) {
                        Text("${user.membership}")
                     }

                     TableCell(column3Weight) {
                        Box(
                           modifier = Modifier.fillMaxSize(),
                           contentAlignment = Alignment.Center,
                        ) {
                           Button(
                              onClick = {
                                 usersViewModel.onSelectedUserChange(user.id)
                                 usersViewModel.onUsernameToUpdateChange(user.username)

                                 scope.launch {
                                    sheetState.show()
                                 }
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
fun BottomSheetContent(usersViewModel: UsersViewModel) {
   Row(
      modifier = Modifier.fillMaxWidth().padding(Constants.SPACE.dp),
      verticalAlignment = Alignment.CenterVertically
   ) {
      Column(
         modifier = Modifier.fillMaxWidth().weight(.5f),
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         val usernameToUpdate: String by usersViewModel.usernameToUpdate.collectAsStateWithLifecycle()
         val membershipToUpdate: Membership by usersViewModel.membershipToUpdate.collectAsStateWithLifecycle()

         OutlinedTextField(
            label = { Text("Username") },
            value = usernameToUpdate,
            onValueChange = { usersViewModel.onUsernameToUpdateChange(it) },
         )

         DropdownMenuGeneric(
            Membership.entries.toTypedArray(),
            Membership.FREE,
            onValueChange = { usersViewModel.onMembershipToUpdateChange(it) })
      }

      Column(
         modifier = Modifier.fillMaxWidth().weight(.5f),
         horizontalAlignment = Alignment.CenterHorizontally
      ) {
         Button(
            onClick = {
               usersViewModel.onUpdatePress()
            },

            modifier = Modifier.fillMaxWidth()
               .padding(start = Constants.SPACE.dp, end = Constants.SPACE.dp)
         ) {
            Text("Update")
         }

         Button(
            onClick = {
               usersViewModel.onDeletePress()
            },

            modifier = Modifier.fillMaxWidth()
               .padding(start = Constants.SPACE.dp, end = Constants.SPACE.dp)
         ) {
            Text("Delete")
         }
      }
   }
}