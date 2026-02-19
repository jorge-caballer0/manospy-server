package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
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
import com.example.manospy.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientOnboardingScreenStep3(
    onFinish: () -> Unit = {}
) {
    // Paleta de colores premium extraída de la imagen
    val primaryCyan = Color(0xFF00E5D1)
    val lightTealBg = Color(0xFFE6F9F8)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val starColor = Color(0xFFFBBF24) // Dorado para estrellas
    val dotInactive = Color(0xFFE5E7EB)
    val backgroundColor = Color(0xFFF9FAFB)

    AppScaffold(title = "MANOSPY", onBackClick = null) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Ilustración Central Premium (Escudo y Tarjeta de Calificación)
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .clip(CircleShape)
                    .background(lightTealBg),
                contentAlignment = Alignment.Center
            ) {
                // Círculo central con escudo
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(primaryCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(70.dp)
                    )
                }

                // Tarjeta de calificación flotante (Bottom)
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 40.dp)
                        .width(220.dp)
                        .height(80.dp)
                        .shadow(4.dp, RoundedCornerShape(40.dp)),
                    shape = RoundedCornerShape(40.dp),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar placeholder
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE5E7EB))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(verticalArrangement = Arrangement.Center) {
                            // Líneas de texto placeholder
                            Box(
                                modifier = Modifier
                                    .width(80.dp)
                                    .height(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE5E7EB))
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Estrellas
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                repeat(5) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = starColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))

            // Título
            Text(
                text = "Contrata con tranquilidad",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary,
                    fontSize = 32.sp,
                    letterSpacing = (-0.5).sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Descripción
            Text(
                text = "Trabajamos solo con profesionales verificados y opiniones reales para que tengas una experiencia segura.",
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

            // Botón principal "Comenzar"
            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(24.dp), 
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
