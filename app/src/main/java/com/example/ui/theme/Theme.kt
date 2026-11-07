package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimarySnack,
    secondary = SecondarySnack,
    tertiary = TertiarySnack,
    background = Color(0xFF1E1715),
    surface = Color(0xFF2B211E),
    surfaceVariant = Color(0xFF3E312B),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFFFFF0EA),
    onSurface = Color(0xFFFFF0EA)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimarySnack,
    secondary = SecondarySnack,
    tertiary = TertiarySnack,
    background = BackgroundWarm,
    surface = SurfaceWarm,
    surfaceVariant = SurfaceCard,
    onPrimary = Color.White,
    onSecondary = TextDark,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep snack branded colors
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
