import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import presentation.NavigationHost
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
   MaterialTheme {
      NavigationHost()
   }
}