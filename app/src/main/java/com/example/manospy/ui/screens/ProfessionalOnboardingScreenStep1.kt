package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.components.AppTopBar
import com.example.manospy.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalOnboardingScreenStep1(
    onNext: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    // Paleta de colores premium extraída del diseño de la imagen
    val primaryCyan = Color(0xFF00E5D1)
    val lightPeachBg = Color(0xFFFDF2E9) 
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val dotInactive = Color(0xFFE5E7EB)
    val backgroundColor = Color.White

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            AppTopBar(
                title = "Paso 1",
                onNavigationClick = { },
                showNavigationIcon = false,
                containerColor = AppColors.PrimaryBlue
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Contenedor de la ilustración central
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(48.dp))
                    .background(lightPeachBg),
                contentAlignment = Alignment.Center
            ) {
                // Mockup del canvas/gráfico
                Surface(
                    modifier = Modifier
                        .fillMaxSize(0.65f),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        // Representación simplificada del gráfico de barras y flecha de crecimiento
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().height(80.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Box(modifier = Modifier.weight(1f).height(30.dp).background(Color(0xFFF3F4F6)))
                                Box(modifier = Modifier.weight(1f).height(50.dp).background(Color(0xFFF3F4F6)))
                                Box(modifier = Modifier.weight(1f).height(40.dp).background(Color(0xFFF3F4F6)))
                                Box(modifier = Modifier.weight(1f).height(65.dp).background(Color(0xFFF3F4F6)))
                            }
                        }
                        // Icono de tendencia positiva en color cian
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = primaryCyan,
                            modifier = Modifier.fillMaxSize(0.6f).align(Alignment.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Título principal
            Text(
                text = "Haz crecer tu trabajo con Manospy",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary,
                    fontSize = 30.sp,
                    lineHeight = 38.sp,
                    letterSpacing = (-0.5).sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Texto descriptivo
            Text(
                text = "Manospy te ayuda a conectar con nuevos clientes y mostrar tus servicios de forma profesional.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = textSecondary,
                    lineHeight = 26.sp,
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // Indicadores de página (Dots)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Indicador activo (estilo píldora)
                Box(
                    modifier = Modifier
                        .size(width = 28.dp, height = 8.dp)
                        .clip(CircleShape)
                        .background(primaryCyan)
                )
                // Indicadores inactivos
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotInactive)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(dotInactive)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón de acción principal
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryCyan,
                    contentColor = textPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = "Siguiente",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
