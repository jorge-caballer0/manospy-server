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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.manospy.ui.navigation.Screen
import com.example.manospy.data.api.RetrofitClient
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalValidationPendingScreen(
    navController: NavController? = null,
    onBack: () -> Unit = {},
    onLogout: () -> Unit = { navController?.navigate("login") },
    mainViewModel: com.example.manospy.ui.viewmodel.MainViewModel? = null
) {
    // Colores del tema Stitch
    val primaryBlue = Color(0xFF0056D2)
    val primaryDark = Color(0xFF003A94)
    val backgroundLight = Color(0xFFF8FAFC)
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val greenCheck = Color(0xFF22C55E)
    val amberPending = Color(0xFFFCD34D)
    val amberPendingDark = Color(0xFFD97706)
    val borderGray = Color(0xFFE2E8F0)
    
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
    
    // Polling automático para verificar aprobación
    val currentUser = mainViewModel?.currentUser?.value
    LaunchedEffect(currentUser?.id) {
        if (currentUser?.id == null) return@LaunchedEffect
        
        try {
            while (true) {
                delay(15000) // Verificar cada 15 segundos
                
                try {
                    val response = RetrofitClient.apiService.getProfessionalStatus(currentUser.id)
                    if (response.isSuccessful) {
                        val statusData = response.body()
                        if (statusData?.status == "approved") {
                            // ✅ Profesional aprobado! Navegar a pantalla de aprobación
                            navController?.navigate(Screen.ProfessionalApproved.route) {
                                popUpTo(Screen.ProfessionalPending.route) { inclusive = true }
                            }
                            break // Salir del loop
                        }
                    }
                } catch (e: Exception) {
                    // Error en la verificación, continuar polling
                    continue
                }
            }
        } catch (e: Exception) {
            // Error fatal, no continuar polling
        }
    }
    
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
            
            // Contenido central
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icono animado flotante
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .offset(y = floatOffset.dp)
                        .padding(bottom = 32.dp)
                ) {
                    // Círculo exterior blanco
                    Surface(
                        modifier = Modifier
                            .size(160.dp)
                            .align(Alignment.Center),
                        color = Color.White,
                        shadowElevation = 16.dp,
                        shape = RoundedCornerShape(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            // Círculo punteado interior
                            Box(
                                modifier = Modifier
                                    .size(128.dp)
                                    .border(
                                        width = 2.dp,
                                        color = primaryBlue.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(40.dp)
                                    )
                            )
                            
                            // Icono reloj
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(80.dp)
                            )
                        }
                    }
                    
                    // Badge verde arriba
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = (-8).dp)
                            .clip(RoundedCornerShape(12.dp)),
                        color = greenCheck,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    
                    // Badge documento abajo
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.BottomStart)
                            .offset(x = (-8).dp, y = 8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .rotate(12f),
                        color = amberPending,
                        shadowElevation = 8.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                
                // Título y subtítulo
                Text(
                    "¡Registro Recibido!",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = primaryBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(primaryBlue, primaryDark)
                            )
                        )
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    "Estamos revisando tu perfil",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textSecondary,
                    textAlign = TextAlign.Center
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
                            .padding(20.dp)
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
                                "En Revisión",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        }
                        
                        // Badge pulsante
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp)),
                            color = Color(0xFFFEF3C7),
                            border = BorderStroke(1.dp, Color(0xFFFCD34D))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                // Puntito pulsante
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(amberPendingDark, shape = RoundedCornerShape(50))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "PENDIENTE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF92400E),
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
                    color = Color(0xFFEFF6FF),
                    border = BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            color = Color(0xFFDEEFFB)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = primaryBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column {
                            Text(
                                "Nuestro equipo verificará tus documentos en un plazo de ",
                                fontSize = 14.sp,
                                color = textSecondary,
                                lineHeight = 18.sp
                            )
                            
                            Text(
                                "24 a 48 horas",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary,
                                lineHeight = 18.sp
                            )
                            
                            Text(
                                ". Te notificaremos por email una vez que tu perfil sea aprobado.",
                                fontSize = 14.sp,
                                color = textSecondary,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botones
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
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
                        Text(
                            "Entendido",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                Button(
                    onClick = { navController?.navigate(Screen.Login.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = textSecondary
                    ),
                    border = BorderStroke(1.dp, borderGray)
                ) {
                    Text(
                        "Ir al Inicio",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
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
                    "Bienvenido a la red de profesionales",
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
