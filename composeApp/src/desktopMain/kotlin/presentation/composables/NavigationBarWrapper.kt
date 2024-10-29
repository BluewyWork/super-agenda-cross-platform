package presentation.composables

//import androidx.compose.material3.Icon
//import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.compose.material3.NavigationBarItem

@Composable
fun NavigationBarWrapper(navController: NavController, content: @Composable () -> Unit) {
   content()
}

@Composable
fun BottomBar(navController: NavController) {
   val currentBackStackEntry = navController.currentBackStackEntryAsState()
   val currentRoute = currentBackStackEntry.value?.destination?.route

//   NavigationBar(
//      modifier = Modifier
//         // because default size is way too big
//         .height(50.dp),
//   ) {
//      NavigationBarItem(
//         selected = currentRoute == Destinations.Users.route,
//         onClick = { navController.navigate(Destinations.Users.route) },
//
//         icon = {
//            Icon(
//               imageVector = Icons.Outlined.Home,
//               contentDescription = null
//            )
//         }
//      )
//   }
}