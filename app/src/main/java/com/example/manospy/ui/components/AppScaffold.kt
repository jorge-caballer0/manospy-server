package com.example.manospy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.manospy.ui.theme.CorporateBlue
import com.example.manospy.ui.utils.DynamicStatusBar
import com.example.manospy.ui.utils.isDark
import com.example.manospy.ui.utils.getAppropriateIconColor

/**
 * AppScaffold - Layout base reutilizable para todas las pantallas
 *
 * Características:
 * - Cabecera con color dinámico (azul corporativo por defecto)
 * - Barra de estado sincronizada con el color de la cabecera
 * - Contenedor blanco con esquinas redondeadas (24dp) debajo de la cabecera
 * - Contenido scrolleable dentro del contenedor blanco
 * - Responsivo para móviles, tablets y layouts con navbar inferior
 * - Soporta Android 5.0+ con sincronización óptima en Android 13+
 *
 * @param title Título que se muestra en la cabecera
 * @param modifier Modificador opcional para personalizar el widget
 * @param contentModifier Modificador opcional para el contenedor de contenido
 * @param hasBottomNavigation Si es true, ajusta el padding inferior para la navbar
 * @param onBackClick Callback opcional para el botón atrás
 * @param onRightIconClick Callback opcional para el icono derecho (ej: campanita)
 * @param rightIcon Icono derecho opcional a mostrar en la cabecera
 * @param headerBackgroundColor Color dinámico de la cabecera (se sincroniza con status bar)
 * @param headerTextColor Color del texto de la cabecera
 * @param content Composable lambda que contiene el contenido de la pantalla
 */
@Composable
fun AppScaffold(
    title: String,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    hasBottomNavigation: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    onRightIconClick: (() -> Unit)? = null,
    rightIcon: ImageVector? = null,
    leftContent: (@Composable () -> Unit)? = null,
    subtitle: String? = null,
    headerBackgroundColor: Color = CorporateBlue,
    headerTextColor: Color = Color.White,
    content: @Composable (PaddingValues) -> Unit
) {
    // Sincroniza dinámicamente la barra de estado con el color de la cabecera
    val useDarkIcons = !headerBackgroundColor.isDark()
    DynamicStatusBar(
        statusBarColor = headerBackgroundColor,
        useDarkIcons = useDarkIcons
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(headerBackgroundColor)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // ===== HEADER =====
            Header(
                title = title,
                subtitle = subtitle,
                onBackClick = onBackClick,
                onRightIconClick = onRightIconClick,
                rightIcon = rightIcon,
                leftContent = leftContent,
                headerBackgroundColor = headerBackgroundColor,
                headerTextColor = headerTextColor
            )

            // ===== CONTENT CONTAINER =====
            Surface(
                modifier = contentModifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                shadowElevation = 0.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(
                            bottom = if (hasBottomNavigation) 80.dp else 0.dp
                        )
                ) {
                    content(
                        PaddingValues(
                            top = 0.dp,
                            bottom = if (hasBottomNavigation) 0.dp else 0.dp,
                            start = 0.dp,
                            end = 0.dp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Header - Cabecera corporativa reutilizable
 *
 * @param title Texto del título
 * @param modifier Modificador opcional
 * @param onBackClick Callback para el botón atrás
 * @param onRightIconClick Callback para el icono derecho
 * @param rightIcon Icono derecho a mostrar
 * @param headerBackgroundColor Color de fondo de la cabecera
 * @param headerTextColor Color del texto de la cabecera
 */
@Composable
private fun Header(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null,
    onRightIconClick: (() -> Unit)? = null,
    rightIcon: ImageVector? = null,
    leftContent: (@Composable () -> Unit)? = null,
    headerBackgroundColor: Color = CorporateBlue,
    headerTextColor: Color = Color.White
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        color = headerBackgroundColor,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            // Contenido a la izquierda (foto de perfil, botón atrás, etc.)
            Row(
                modifier = Modifier
                    .align(Alignment.CenterStart),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón atrás O contenido personalizado (foto)
                if (leftContent != null) {
                    leftContent()
                } else if (onBackClick != null) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = headerTextColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Título (y subtítulo opcional) centrado / con padding
            Column(
                modifier = Modifier
                    .padding(start = if (leftContent != null || onBackClick != null) 56.dp else 0.dp)
                    .padding(end = if (rightIcon != null && onRightIconClick != null) 56.dp else 0.dp)
                    .align(if (leftContent != null) Alignment.CenterStart else if (onBackClick != null) Alignment.CenterStart else Alignment.Center)
            ) {
                Text(
                    text = title,
                    fontSize = if (subtitle.isNullOrEmpty()) 20.sp else 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Default,
                    color = headerTextColor,
                    maxLines = 2
                )

                if (!subtitle.isNullOrEmpty()) {
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = headerTextColor.copy(alpha = 0.95f),
                        maxLines = 1
                    )
                }
            }

            // Icono derecho (opcional - lado derecho, ej: campanita de notificaciones)
            if (rightIcon != null && onRightIconClick != null) {
                IconButton(
                    onClick = onRightIconClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(48.dp)
                ) {
                    Icon(
                        imageVector = rightIcon,
                        contentDescription = "Acción derecha",
                        tint = headerTextColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

/**
 * SimpleAppScaffold - Versión simplificada sin container redondeado
 * Útil para pantallas que requieren un layout más minimalista
 *
 * @param title Título que se muestra en la cabecera
 * @param modifier Modificador opcional para personalizar el widget
 * @param content Composable lambda que contiene el contenido de la pantalla
 */
@Composable
fun SimpleAppScaffold(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CorporateBlue)
    ) {
        // ===== HEADER =====
        Header(title = title)

        // ===== CONTENT =====
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            content(PaddingValues(0.dp))
        }
    }
}
