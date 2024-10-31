package presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.DrawerState
import androidx.compose.material.DrawerValue
import androidx.compose.material.Icon
import androidx.compose.material.ModalDrawer
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import presentation.Destinations

@Composable
fun NavigationBarWrapper(navController: NavController, content: @Composable () -> Unit) {
   val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
   val scope = rememberCoroutineScope()

   CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
      ModalDrawer(
         drawerState = drawerState,
         drawerContent = {
            Text("It's Empty")
         },
      ) {
         CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
            Scaffold(
               topBar = {
                  TopBar(navController, scope, drawerState)
               }
            ) {
               Row {
                  SideBar(navController)
                  content()
               }
            }
         }
      }
   }
}

@Composable
fun TopBar(navController: NavController, scope: CoroutineScope, drawerState: DrawerState) {
   val currentBackStackEntry = navController.currentBackStackEntryAsState()
   val currentRoute = currentBackStackEntry.value?.destination?.route

   TopAppBar(
      title = {
         Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
         ) {
            if (currentRoute != null) {
               Text(currentRoute.uppercase())
            } else {
               Text("Unknown")
            }
         }
      },

      navigationIcon = {
         Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
         ) {
            Button(
               onClick = {
                  scope.launch {
                     drawerState.apply {
                        if (isClosed) open() else close()
                     }
                  }
               }
            ) {
               Icon(Icons.Default.Menu, "Menu")
            }
         }
      }
   )
}

@Composable
fun SideBar(navController: NavController) {
   val currentBackStackEntry = navController.currentBackStackEntryAsState()
   val currentRoute = currentBackStackEntry.value?.destination?.route

   NavigationRail(
      modifier = Modifier.width(100.dp)
   ) {
      NavigationRailItem(
         modifier = Modifier
            .fillMaxWidth(),

         selected = currentRoute == Destinations.Users.route,
         onClick = { navController.navigate(Destinations.Users.route) },
         icon = { Text("Users") }
      )

      NavigationRailItem(
         modifier = Modifier
            .fillMaxWidth(),

         selected = currentRoute == Destinations.Admins.route,
         onClick = { navController.navigate(Destinations.Admins.route) },
         icon = { Text("Admins") }
      )
   }
}