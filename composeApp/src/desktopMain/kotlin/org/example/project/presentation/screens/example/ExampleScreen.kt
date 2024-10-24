package org.example.project.presentation.screens.example

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun ExampleScreen(exampleViewModel: ExampleViewModel) {
   var counter by remember { mutableStateOf(0) }

   Button(
      onClick = {
         counter++
      }
   ) {
      Text("$counter")
   }

   Text(exampleViewModel.example())
}