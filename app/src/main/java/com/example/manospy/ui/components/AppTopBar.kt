package com.example.manospy.ui.components

import androidx.compose.foundation.background
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

/**
 * Componente reutilizable para la TopAppBar de la app.
 * Sincroniza automáticamente el color de la barra de estado con el StyleTheme.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    title: String,
    onNavigationClick: (() -> Unit)? = null,
    showNavigationIcon: Boolean = true,
    actions: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFF2563EB), // Azul corporativo
    titleColor: Color = Color.White,
    navigationIconTint: Color = Color.White,
    applyStatusBarColor: Boolean = true
) {
    val systemUiController = rememberSystemUiController()
    
    // Sincronizar barra de estado con el color de la TopAppBar
    if (applyStatusBarColor) {
        LaunchedEffect(containerColor) {
            systemUiController.setStatusBarColor(
                color = containerColor,
                darkIcons = false
            )
        }
    }
    
    CenterAlignedTopAppBar(
        title = {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = titleColor
                ),
                maxLines = 1
            )
        },
        navigationIcon = if (showNavigationIcon && onNavigationClick != null) {
            {
                IconButton(onClick = onNavigationClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Atrás",
                        tint = navigationIconTint
                    )
                }
            }
        } else {
            {}
        },
        actions = { actions() },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor,
            scrolledContainerColor = containerColor
        ),
        modifier = modifier.shadow(2.dp)
    )
}

/**
 * Variante simplificada sin ícono de navegación.
 */
@Composable
fun AppTopBarSimple(
    title: String,
    actions: @Composable () -> Unit = {},
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFF2563EB),
    titleColor: Color = Color.White
) {
    AppTopBar(
        title = title,
        showNavigationIcon = false,
        actions = actions,
        modifier = modifier,
        containerColor = containerColor,
        titleColor = titleColor
    )
}
