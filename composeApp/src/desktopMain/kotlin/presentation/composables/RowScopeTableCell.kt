package presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RowScope.TableCell(
   weight: Float,
   content: @Composable () -> Unit
) {
   Box(
      modifier = Modifier
         .weight(weight)
         .fillMaxWidth()
   ) {
      content()
   }
}