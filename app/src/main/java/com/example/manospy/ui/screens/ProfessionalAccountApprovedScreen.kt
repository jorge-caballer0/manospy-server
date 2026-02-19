package com.example.manospy.ui.screens

import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.manospy.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalAccountApprovedScreen(
    navController: NavController? = null,
    onNavigateToDashboard: () -> Unit = { 
        navController?.navigate(Screen.ProfessionalMain.route) { 
            popUpTo(Screen.ProfessionalMain.route) { inclusive = true } 
        }
    }
) {
    // Colores del tema Stitch
    val primaryBlue = Color(0xFF0056D2)
    val backgroundLight = Color(0xFFF8FAFC)
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val greenSuccess = Color(0xFF22C55E)
    val borderGray = Color(0xFFE2E8F0)
    val yellowConfetti = Color(0xFFFFD700)
    val redConfetti = Color(0xFFFF6B6B)
    val blueConfetti = Color(0xFF93C5FD)
    val greenConfetti = Color(0xFF86EFAC)
    
    // Animación flotante
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -10f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "float"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE0E9FF),
                        backgroundLight
                    ),
                    radius = 1000f
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header con logo
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = primaryBlue
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    "ManosPy",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryBlue,
                    letterSpacing = 0.5.sp
                )
            }
            
            // Contenido central con confetti
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Partículas confetti decorativas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    // Partícula amarilla rotada
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = (-80).dp, y = (-60).dp)
                            .background(yellowConfetti, shape = RoundedCornerShape(2.dp))
                            .rotate(45f)
                    )
                    
                    // Partícula roja
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .offset(x = (80).dp, y = (-40).dp)
                            .background(redConfetti, shape = RoundedCornerShape(50))
                    )
                    
                    // Partícula azul rotada
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .offset(x = (60).dp, y = (80).dp)
                            .background(blueConfetti, shape = RoundedCornerShape(2.dp))
                            .rotate(-12f)
                    )
                    
                    // Partícula verde
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .offset(x = (-100).dp, y = (60).dp)
                            .background(greenConfetti, shape = RoundedCornerShape(50))
                    )
                    
                    // Icono celebración
                    Icon(
                        Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = primaryBlue.copy(alpha = 0.2f),
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (20).dp, y = (-20).dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Icono animado flotante
                Box(
                    modifier = Modifier
                        .size(176.dp)
                        .offset(y = floatOffset.dp)
                ) {
                    // Círculo exterior blanco
                    Surface(
                        modifier = Modifier
                            .size(176.dp)
                            .align(Alignment.Center),
                        color = Color.White,
                        shadowElevation = 20.dp,
                        shape = RoundedCornerShape(88.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // Círculo interior con icono
                            Box(
                                modifier = Modifier
                                    .size(144.dp)
                                    .background(
                                        primaryBlue.copy(alpha = 0.05f),
                                        shape = RoundedCornerShape(72.dp)
                                    )
                                    .border(
                                        width = 2.dp,
                                        color = primaryBlue.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(72.dp)
                                    )
                            )
                            
                            // Icono shield verificado
                            Icon(
                                Icons.Default.VerifiedUser,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(104.dp)
                            )
                        }
                    }
                    
                    // Badge verde check
                    Surface(
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = (-8).dp, y = (-8).dp)
                            .clip(RoundedCornerShape(14.dp)),
                        color = greenSuccess,
                        shadowElevation = 12.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Título
                Text(
                    "¡Perfil Aprobado!",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryBlue,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Subtítulo
                Text(
                    "Bienvenido a la red de profesionales\nde ManosPy",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Card de estado
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    color = Color.White,
                    border = BorderStroke(1.dp, borderGray),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                "ESTADO DE LA CUENTA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary,
                                letterSpacing = 0.1.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Activa",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textPrimary
                            )
                        }
                        
                        // Badge verificada verde
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp)),
                            color = Color(0xFFF0FDF4),
                            border = BorderStroke(1.dp, Color(0xFFC6F6D5))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(greenSuccess, shape = RoundedCornerShape(50))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "VERIFICADA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF166534),
                                    letterSpacing = 0.05.sp
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Info box
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    color = primaryBlue.copy(alpha = 0.05f),
                    border = BorderStroke(1.dp, primaryBlue.copy(alpha = 0.1f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Ya puedes empezar a recibir solicitudes y ofrecer tus servicios a toda nuestra comunidad.",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = textSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botón principal
            Button(
                onClick = onNavigateToDashboard,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryBlue
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.RocketLaunch,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Ir a mi Panel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    borderGray,
                                    Color.Transparent
                                )
                            )
                        )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Tu futuro profesional comienza aquí",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.1.sp
                )
            }
        }
    }
}
