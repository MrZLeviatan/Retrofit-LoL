package co.edu.uniquindio.retrofit_lol.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// La app usa siempre el esquema oscuro, como el cliente del juego.
private val LolColorScheme = darkColorScheme(
    primary = Gold,
    onPrimary = Abyss,
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary = Hextech,
    onSecondary = Abyss,
    secondaryContainer = HextechDark,
    onSecondaryContainer = GoldLight,
    background = Abyss,
    onBackground = GoldLight,
    surface = Navy,
    onSurface = GoldLight,
    surfaceVariant = NavyLight,
    onSurfaceVariant = Grey,
    surfaceContainer = Navy,
    surfaceContainerHigh = NavyLight,
    outline = Outline,
    error = Offline,
)

@Composable
fun RetrofitLoLTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LolColorScheme,
        typography = Typography,
        content = content
    )
}
