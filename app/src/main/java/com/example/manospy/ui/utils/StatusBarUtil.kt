package com.example.manospy.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * Aplica dinámicamente el color de la barra de estado según el color de la cabecera
 * 
 * Características:
 * - Android 5.0+ (API 21+): Soporta colores personalizados
 * - Android 13+: Mejor integración visual
 * - Android 15+: Sincronización óptima
 * 
 * @param statusBarColor Color de la barra de estado
 * @param useDarkIcons Si true, los iconos de la barra serán oscuros (para fondos claros)
 * @param navigationBarColor Color de la barra de navegación (opcional)
 * @param useDarkNavIcons Si true, los iconos de navegación será oscuros
 */
@Composable
fun DynamicStatusBar(
    statusBarColor: Color,
    useDarkIcons: Boolean = false,
    navigationBarColor: Color? = null,
    useDarkNavIcons: Boolean = false
) {
    val systemUiController = rememberSystemUiController()
    
    LaunchedEffect(statusBarColor, useDarkIcons) {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = useDarkIcons
        )
    }
    
    // Sincronizar barra de navegación si se proporciona
    if (navigationBarColor != null) {
        LaunchedEffect(navigationBarColor, useDarkNavIcons) {
            systemUiController.setNavigationBarColor(
                color = navigationBarColor,
                darkIcons = useDarkNavIcons
            )
        }
    }
}

/**
 * Determina si se debe usar iconos oscuros basado en el brillo del color
 */
fun Color.isDark(): Boolean {
    // Calcular luminancia relativa según WCAG
    val r = this.red * 0.299f
    val g = this.green * 0.587f
    val b = this.blue * 0.114f
    return (r + g + b) < 0.5f
}

/**
 * Retorna el color de icono apropiado (blanco u oscuro) para una barra de fondo
 */
fun Color.getAppropriateIconColor(): Color {
    return if (this.isDark()) Color.White else Color.Black
}
