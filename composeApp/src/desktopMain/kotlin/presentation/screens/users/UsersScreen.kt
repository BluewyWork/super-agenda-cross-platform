package presentation.screens.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

      users.forEachIndexed() { index, user ->
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
                  Text(user.id.toString())
               }

               TableCell(column2Weight) {
                  Text(user.username)
               }

               TableCell(column2Weight) {
                  Text(user.hashedPassword)
               }

               TableCell(column3Weight) {
                  Box(
                     modifier = Modifier.fillMaxSize(),
                     contentAlignment = Alignment.Center,
                  ) {
                     Button(
                        onClick = {},
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

@Composable
fun RowScope.TableCell(
   weight: Float,
   content: @Composable () -> Unit
) {
   Box(
      modifier = Modifier
         .weight(weight)
         .fillMaxSize()
   ) {
      content()
   }
}