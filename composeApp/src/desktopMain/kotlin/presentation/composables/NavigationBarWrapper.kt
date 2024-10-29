package presentation.composables

import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import presentation.Destinations

@Composable
fun NavigationBarWrapper(navController: NavController, content: @Composable () -> Unit) {
   SideBar(navController)
   content()
}

@Composable
fun SideBar(navController: NavController) {
   NavigationRail {
      NavigationRailItem(
         selected = true,
         onClick = { navController.navigate(Destinations.Users.route) },
         icon = { Icons.Default.Person }
      )
   }
}