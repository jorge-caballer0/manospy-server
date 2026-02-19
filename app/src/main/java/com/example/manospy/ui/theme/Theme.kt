package com.example.manospy.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// Light mode color scheme - Corporate Colors
private val LightColorScheme = lightColorScheme(
    primary = CorporateBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E7FF),
    onPrimaryContainer = CorporateBlue,
    secondary = CorporateLightBlue,
    onSecondary = Color.White,
    background = Color.White,
    surface = Color.White,
    onSurface = DarkGray,
    onSurfaceVariant = Color(0xFF1E293B),
    outline = Color(0xFFE2E8F0),
    error = Error
)

// Dark mode color scheme - Corporate Colors
private val DarkColorScheme = darkColorScheme(
    primary = CorporateBlue.copy(alpha = 0.9f),
    onPrimary = Color.White,
    primaryContainer = CorporateBlue.copy(alpha = 0.3f),
    onPrimaryContainer = CorporateBlue,
    secondary = CorporateLightBlue,
    onSecondary = Color.White,
    background = Color(0xFF0F172A),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = Color(0xFF334155),
    error = Error
)

val DesignShapes = Shapes(
    extraSmall = RoundedCornerShape(12.dp), // Inputs
    small = RoundedCornerShape(14.dp),      // Buttons
    medium = RoundedCornerShape(16.dp),     // Cards
    large = RoundedCornerShape(24.dp)
)

@Composable
fun ManosPyTheme(
    darkTheme: Boolean = shouldUseDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    // Usar Accompanist SystemUiController para configurar la barra de estado
    val systemUiController = rememberSystemUiController()
    val statusBarColor = if (darkTheme) Color(0xFF0F172A) else CorporateBlue
    val navigationBarColor = if (darkTheme) Color(0xFF0F172A) else Color.White
    
    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = !darkTheme
        )
        systemUiController.setNavigationBarColor(
            color = navigationBarColor,
            darkIcons = !darkTheme
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = DesignShapes,
        content = content
    )
}

@Composable
private fun shouldUseDarkTheme(): Boolean {
    return isSystemInDarkTheme()
}
