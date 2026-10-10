package dz.boulanger.app

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// 2026 baseline: system-following dark mode + Android 12+ dynamic color,
// static bakery-amber fallback scheme on older devices.
private val LightScheme = lightColorScheme(
    primary = Color(0xFF7A5200),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFFDDB3),
    onPrimaryContainer = Color(0xFF291800),
    secondary = Color(0xFF6B5D3F),
    tertiary = Color(0xFF4C662B)
)

private val DarkScheme = darkColorScheme(
    primary = Color(0xFFFFB950),
    onPrimary = Color(0xFF422D00),
    primaryContainer = Color(0xFF5C4000),
    onPrimaryContainer = Color(0xFFFFDDB3),
    secondary = Color(0xFFD3C5A0),
    tertiary = Color(0xFFA9C787)
)

@Composable
fun BoulangerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val scheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkScheme
        else -> LightScheme
    }
    MaterialTheme(colorScheme = scheme, typography = Typography(), content = content)
}
