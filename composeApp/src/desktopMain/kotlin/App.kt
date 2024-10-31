import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import presentation.NavigationHost
import presentation.ui.theme.OneDarkProTheme

@Composable
@Preview
fun App() {
   OneDarkProTheme {
      NavigationHost()
   }
}