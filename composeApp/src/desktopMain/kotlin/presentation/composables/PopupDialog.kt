package presentation.composables

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun PopupDialog(title: String, message: String, onDismiss: () -> Unit) {
   AlertDialog(
      onDismissRequest = { onDismiss() },
      title = { Text(text = title) },
      text = { Text(text = message) },

      confirmButton = {
         TextButton(onClick = { onDismiss() }) {
            Text(text = "OK")
         }
      },
   )
}