package org.example.project.core

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.example.project.presentation.screens.example.ExampleScreen
import org.example.project.presentation.screens.example.ExampleViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NavigationHost() {
   val navController = rememberNavController()

   NavHost(
      navController = navController,
      startDestination = Destinations.Example.route
   ) {
      composable(Destinations.Example.route) {
         val exampleViewModel = koinViewModel<ExampleViewModel>()
         ExampleScreen(exampleViewModel)
      }
   }
}