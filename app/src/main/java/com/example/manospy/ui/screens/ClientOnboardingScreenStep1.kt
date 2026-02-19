package com.example.manospy.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.R

@Composable
fun ClientOnboardingScreenStep1(
    onNext: () -> Unit = {},
    onSkip: () -> Unit = {}
) {
    // Paleta de colores extraída del diseño premium de la imagen
    val primaryCyan = Color(0xFF00E5D1) 
    val lightTealBg = Color(0xFFE6F9F8) 
    val textPrimary = Color(0xFF111827) 
    val textSecondary = Color(0xFF6B7280) 
    val dotInactive = Color(0xFFE5E7EB) 
    val backgroundColor = Color(0xFFF9FAFB) 

    Scaffold(
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón Saltar en la parte superior derecha
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                TextButton(onClick = onSkip) {
                    Text(
                        text = "Saltar",
                        color = textPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Contenedor de la ilustración
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Forma cuadrada
                    .clip(RoundedCornerShape(40.dp))
                    .background(lightTealBg),
                contentAlignment = Alignment.Center
            ) {
                // Se utiliza un placeholder, reemplazar con la ilustración real de Manospy
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), 
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(0.8f),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Título principal
            Text(
                text = "Bienvenido a Manospy",
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
                text = "Encuentra profesionales confiables para resolver tus necesidades diarias de forma rápida y segura.",
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
                // Indicador activo (estilo píldora expandida)
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

            // Botón de acción principal "Siguiente"
            Button(
                onClick = onNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryCyan,
                    contentColor = textPrimary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Siguiente",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
