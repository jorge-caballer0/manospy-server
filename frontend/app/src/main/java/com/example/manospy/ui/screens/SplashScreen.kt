package com.example.manospy.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.R
import com.example.manospy.ui.viewmodel.SplashViewModel
import com.example.manospy.ui.viewmodel.SplashDestination
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    splashViewModel: SplashViewModel,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.8f) }

    val state by splashViewModel.state.collectAsState()

    // ✅ CORRECCIÓN 1: Iniciar verificación de sesión automáticamente
    LaunchedEffect(Unit) {
        splashViewModel.start()
    }

    LaunchedEffect(state.destination) {
        // Animación suave de entrada
        alpha.animateTo(1f, animationSpec = tween(1200, easing = EaseOutExpo))
        scale.animateTo(
            1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )

        delay(1000) // Pausa para branding

        when (val dest = state.destination) {
            is SplashDestination.GoLogin -> onNavigateToLogin()
            is SplashDestination.GoMain -> onNavigateToHome()
            is SplashDestination.Loading -> { /* mostramos solo la animación */ }
        }
    }

    Scaffold(containerColor = Color.White) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(alpha.value)
                    .scale(scale.value)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logomanospy),
                    contentDescription = "Logo Manospy",
                    modifier = Modifier.size(200.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "MERCADO DE SERVICIOS PREMIUM",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = Color(0xFF64748B),
                        letterSpacing = 1.5.sp
                    )
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .alpha(0.4f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "CONEXIÓN SEGURA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
