package org.example.project.presentation.screens.example

import androidx.lifecycle.ViewModel

class ExampleViewModel(
   private val exampleDi: ExampleDi
) : ViewModel() {
   fun example(): String {
      return exampleDi.toString()
   }
}