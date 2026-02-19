package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Build
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
fun ProfessionalOnboardingScreenStep2(
    onNext: () -> Unit = {},
    onSkip: () -> Unit = {}
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
                title = "Paso 2",
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

            // Ilustración Central Premium (Perfil y Notificaciones)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                // Fondo circular decorativo
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.85f)
                        .clip(CircleShape)
                        .background(lightTealBg)
                )

                // Tarjeta de perfil principal
                Surface(
                    modifier = Modifier
                        .size(width = 180.dp, height = 220.dp)
                        .shadow(4.dp, RoundedCornerShape(40.dp)),
                    shape = RoundedCornerShape(40.dp),
                    color = Color.White
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(lightTealBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, tint = primaryCyan, modifier = Modifier.size(30.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.width(80.dp).height(8.dp).clip(CircleShape).background(dotInactive))
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.width(60.dp).height(8.dp).clip(CircleShape).background(dotInactive))
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(24.dp).clip(CircleShape).background(primaryCyan))
                    }
                }

                // Burbuja de Notificación (Top Right)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 20.dp, end = 10.dp)
                        .size(64.dp)
                        .shadow(8.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, primaryCyan.copy(alpha = 0.3f))
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Notifications, null, tint = primaryCyan, modifier = Modifier.size(28.dp))
                    }
                }

                // Tarjeta de servicio (Left Center)
                Surface(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .offset(x = (-10).dp, y = 40.dp)
                        .width(130.dp)
                        .height(60.dp)
                        .shadow(10.dp, RoundedCornerShape(30.dp)),
                    shape = RoundedCornerShape(30.dp),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(lightTealBg),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Build, null, tint = primaryCyan, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.width(50.dp).height(6.dp).clip(CircleShape).background(dotInactive))
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Título
            Text(
                text = "Ofrece tus servicios",
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
                text = "Crea tu perfil, publica tus servicios y recibe solicitudes de clientes cercanos a ti.",
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
                // Punto 2 (Activo)
                Box(
                    modifier = Modifier
                        .size(width = 28.dp, height = 8.dp)
                        .clip(CircleShape)
                        .background(primaryCyan)
                )
                // Punto 3 (Inactivo)
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dotInactive))
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Botón "Siguiente"
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
