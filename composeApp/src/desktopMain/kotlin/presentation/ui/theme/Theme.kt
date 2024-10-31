package presentation.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable

private val OneDarkProColorPalette = darkColors(
   primary = oneDarkProPrimary,
   primaryVariant = oneDarkProPrimaryVariant,
   secondary = oneDarkProSecondary,
   background = oneDarkProBackground,
   surface = oneDarkProSurface,
   error = oneDarkProError,
   onPrimary = oneDarkProOnPrimary,
   onSecondary = oneDarkProOnSecondary,
   onBackground = oneDarkProOnBackground,
   onSurface = oneDarkProOnSurface,
   onError = oneDarkProOnError
)

@Composable
fun OneDarkProTheme(content: @Composable () -> Unit) {
   MaterialTheme(
      colors = OneDarkProColorPalette,
      content = { content() }
   )
}