package presentation.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ExposedDropdownMenuBox
import androidx.compose.material.ExposedDropdownMenuDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun <T : Enum<T>> DropdownMenuGeneric(
   enumValues: Array<T>,
   initialSelection: T,
   onValueChange: (T) -> Unit
) {
   var expanded by remember { mutableStateOf(false) }
   var selectedValue by remember { mutableStateOf(initialSelection) }

   ExposedDropdownMenuBox(
      expanded = expanded,
      onExpandedChange = { expanded = !expanded }
   ) {
      OutlinedTextField(
         value = selectedValue.name,
         onValueChange = {},
         readOnly = true,
         modifier = Modifier
            .clickable { expanded = true },
         label = { Text("Select Value") },
         trailingIcon = {
            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
         },
         colors = ExposedDropdownMenuDefaults.textFieldColors()
      )

      ExposedDropdownMenu(
         expanded = expanded,
         onDismissRequest = { expanded = false }
      ) {
         enumValues.forEach { value ->
            DropdownMenuItem(
               onClick = {
                  selectedValue = value
                  expanded = false
                  onValueChange(value)
               }
            ) {
               Text(text = value.name)
            }
         }
      }
   }
}