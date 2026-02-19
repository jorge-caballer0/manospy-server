package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
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
fun ProfessionalOnboardingScreenStep3(
    onFinish: () -> Unit = {}
) {
    // Paleta de colores premium extraída del diseño de la imagen
    val primaryCyan = Color(0xFF00E5D1)
    val lightTealBg = Color(0xFFE6F9F8)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val dotInactive = Color(0xFFE5E7EB)
    val backgroundColor = Color.White

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            AppTopBar(
                title = "Paso 3",
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

            // Ilustración Central Premium (Reputación y Verificación)
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape)
                    .background(lightTealBg),
                contentAlignment = Alignment.Center
            ) {
                // Tarjeta de perfil/reputación
                Surface(
                    modifier = Modifier
                        .size(width = 180.dp, height = 180.dp)
                        .shadow(4.dp, RoundedCornerShape(40.dp)),
                    shape = RoundedCornerShape(40.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Icono de verificado
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(primaryCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Fila de estrellas de calificación
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            repeat(5) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = primaryCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Título principal
            Text(
                text = "Construye tu reputación",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary,
                    fontSize = 32.sp,
                    letterSpacing = (-0.5).sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Texto descriptivo
            Text(
                text = "Verificamos tu perfil y las valoraciones te ayudarán a generar más confianza y oportunidades.",
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
                // Punto 1 (Inactivo)
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dotInactive))
                // Punto 2 (Inactivo)
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dotInactive))
                // Punto 3 (Activo - Píldora)
                Box(
                    modifier = Modifier
                        .size(width = 28.dp, height = 8.dp)
                        .clip(CircleShape)
                        .background(primaryCyan)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón de acción principal "Comenzar"
            Button(
                onClick = onFinish,
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
                    text = "Comenzar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
