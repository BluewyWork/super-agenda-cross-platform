package presentation.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import presentation.Constants

@Composable
fun PopupDialog(title: String, message: String, error: String, onDismiss: () -> Unit) {
   AlertDialog(
      onDismissRequest = { onDismiss() },
      title = { Text(text = title) },
      text = {
         Column {
            if (error.isNotBlank()) {
               Text(error)
               Spacer(modifier = Modifier.size(Constants.SPACE.dp))
            }

            Text(text = message)
         }
      },

      confirmButton = {
         TextButton(onClick = { onDismiss() }) {
            Text(text = "OK")
         }
      },
   )
}