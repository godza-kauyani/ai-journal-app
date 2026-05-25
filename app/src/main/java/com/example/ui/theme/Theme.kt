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
    primary = SlateDarkPrimary,
    secondary = SlateDarkSecondary,
    tertiary = SlateDarkTertiary,
    background = SlateDarkBg,
    surface = SlateDarkSurface,
    onBackground = Color(0xFFF1F5F9), // slate-100
    onSurface = Color(0xFFE2E8F0),     // slate-200
    surfaceVariant = Color(0xFF334155), // slate-700
    onSurfaceVariant = Color(0xFFCBD5E1) // slate-300
)

private val LightColorScheme = lightColorScheme(
    primary = SlateLightPrimary,
    secondary = SlateLightSecondary,
    tertiary = SlateLightTertiary,
    background = SlateLightBg,
    surface = SlateLightSurface,
    onBackground = Color(0xFF0F172A), // slate-900
    onSurface = Color(0xFF1E293B),     // slate-800
    surfaceVariant = Color(0xFFE2E8F0), // slate-200
    onSurfaceVariant = Color(0xFF475569) // slate-600
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable device dynamic system scheme to preserve our stunning slate theme identity
    content: @Composable () -> Unit,
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
