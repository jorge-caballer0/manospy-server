package com.example.manospy.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Paleta de colores corporativa unificada.
 */
object AppColors {
    // Colores primarios
    val PrimaryBlue = Color(0xFF2563EB)       // Azul corporativo principal
    val PrimaryDarkBlue = Color(0xFF1E40AF)   // Azul oscuro para contraste
    val BrandBlue = Color(0xFF0EA5E9)         // Azul bril ante
    val AccentCyan = Color(0xFF06B6D4)        // Cian/Turquesa
    
    // Colores secundarios (acentos)
    val AccentGreen = Color(0xFF10B981)       // Verde menta
    val AccentCoral = Color(0xFFFF6B6B)       // Coral suave
    val AccentOrange = Color(0xFFFB923C)      // Naranja suave
    val AccentPurple = Color(0xFFA855F7)      // Púrpura suave
    
    // Escala de grises
    val TextPrimary = Color(0xFF000000)       // Negro puro (texto principal - visible)
    val TextSecondary = Color(0xFF64748B)     // Gris oscuro (texto secundario)
    val TextTertiary = Color(0xFF94A3B8)      // Gris medio (etiquetas/hints)
    val BorderGray = Color(0xFFE2E8F0)        // Gris claro (bordes)
    val BgLight = Color(0xFFF8FAFC)           // Fondo muy claro
    val BgWhite = Color.White
    
    // Estados especiales
    val SuccessGreen = Color(0xFF22C55E)      // Verde éxito
    val WarningYellow = Color(0xFFFCD34D)     // Amarillo advertencia
    val ErrorRed = Color(0xFFEF4444)          // Rojo error
    val DisabledGray = Color(0xFFC4B5FD)      // Gris deshabilitado
}

/**
 * Dimensiones y espaciados uniformes.
 */
object AppDimensions {
    // Espaciados
    val SpaceXSmall = 4.dp
    val SpaceSmall = 8.dp
    val SpaceMedium = 12.dp
    val SpaceLarge = 16.dp
    val SpaceXLarge = 24.dp
    val SpaceHuge = 32.dp
    
    // Radios de esquinas
    val RadiusSmall = 8.dp
    val RadiusMedium = 12.dp
    val RadiusLarge = 16.dp
    val RadiusXLarge = 24.dp
    
    // Elevaciones/Sombras
    val ElevationSmall = 1.dp
    val ElevationMedium = 2.dp
    val ElevationLarge = 4.dp
    val ElevationXLarge = 8.dp
    
    // Tamaños de iconos
    val IconSizeSmall = 16.dp
    val IconSizeMedium = 20.dp
    val IconSizeLarge = 24.dp
    val IconSizeXLarge = 32.dp
    
    // Alturas de componentes
    val ButtonHeight = 48.dp
    val TextFieldHeight = 48.dp
    val TopBarHeight = 56.dp
}

/**
 * Tamaños de tipografía personalizados.
 */
object AppTypography {
    // Tamaños de fuente
    val HeadlineLarge = 32.sp
    val HeadlineMedium = 28.sp
    val HeadlineSmall = 24.sp
    val TitleLarge = 22.sp
    val TitleMedium = 18.sp
    val TitleSmall = 16.sp
    val BodyLarge = 16.sp
    val BodyMedium = 14.sp
    val BodySmall = 12.sp
    val LabelLarge = 14.sp
    val LabelMedium = 12.sp
    val LabelSmall = 10.sp
}
