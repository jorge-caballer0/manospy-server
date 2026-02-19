package com.example.manospy.ui.screens

import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.example.manospy.ui.theme.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.manospy.ui.navigation.Screen
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalHomeScreen(
    navController: NavController? = null,
    userName: String = "Carlos",
    userRating: Double = 4.9,
    userReviewCount: Int = 128,
    userPhotoUrl: String = "",
    totalIncome: String = "Gs. 4.850.000",
    bookings: Int = 42,
    growth: String = "+12%",
    serviceViewModel: com.example.manospy.ui.viewmodel.ServiceViewModel? = null,
    userCities: List<String>? = null
) {
    // Colores del tema Stitch
    val primaryBlue = Color(0xFF0056D2)
    val primaryDark = Color(0xFF003A94)
    val accentAmber = Color(0xFFF59E0B)
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val bgLight = Color(0xFFF0F7FF)
    val borderGray = Color(0xFFE2E8F0)
    val greenSuccess = Color(0xFF10B981)
    val blueLight = Color(0xFFEFF6FF)
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgLight)
    ) {
        // Si se nos pasa el ViewModel y las ciudades del profesional, cargar solicitudes y filtrarlas
        if (serviceViewModel != null && userCities != null) {
            LaunchedEffect(userCities) {
                serviceViewModel.getPendingRequests()
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Header con perfil
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Perfil
                        Row {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = textSecondary
                            )
                        }
                        
                        Column {
                            Text(
                                "¡Hola, $userName!",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = textPrimary
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = accentAmber,
                                    modifier = Modifier.size(14.sp.value.dp)
                                )
                                Text(
                                    userRating.toString(),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                                Text(
                                    "($userReviewCount reseñas)",
                                    fontSize = 11.sp,
                                    color = textSecondary
                                )
                            }
                        }
                        
                        // Notificaciones
                        Box {
                            Surface(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape),
                                color = Color.White,
                                shadowElevation = 2.dp
                            ) {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Notifications,
                                        contentDescription = null,
                                        tint = textSecondary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFFEF4444), shape = CircleShape)
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = (4).dp)
                                    .border(2.dp, Color.White, CircleShape)
                            )
                        }
                    }
                    
                    // Card ingresos
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp)),
                        color = primaryBlue,
                        shadowElevation = 12.dp
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp)
                        ) {
                            // Icono fondo
                            Icon(
                                Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = primaryBlue.copy(alpha = 0.1f),
                                modifier = Modifier
                                    .size(120.dp)
                                    .align(Alignment.TopEnd)
                            )
                            
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    "Ingresos Totales",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFBFDBFE),
                                    letterSpacing = 0.1.sp
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    totalIncome,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                            color = Color.White.copy(alpha = 0.2f)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.TaskAlt,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        
                                        Column {
                                            Text(
                                                "Reservas",
                                                fontSize = 11.sp,
                                                color = Color(0xFFBFDBFE)
                                            )
                                            Text(
                                                bookings.toString(),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(32.dp)
                                            .background(Color.White.copy(alpha = 0.2f))
                                    )
                                    
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(12.dp)),
                                            color = Color.White.copy(alpha = 0.2f)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    Icons.Default.TrendingUp,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                        
                                        Column {
                                            Text(
                                                "Crecimiento",
                                                fontSize = 11.sp,
                                                color = Color(0xFFBFDBFE)
                                            )
                                            Text(
                                                growth,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            // Solicitudes Recientes
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Solicitudes Recientes",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        
                        Text(
                            "Ver todas",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue,
                            modifier = Modifier.clickable {
                                navController?.navigate(Screen.ProfessionalRequests.route)
                            }
                        )
                    }
                    
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Si se dispone del viewModel, mostrar solicitudes filtradas por ciudades
                        if (serviceViewModel != null && userCities != null) {
                            val requestsState by serviceViewModel.pendingRequests.collectAsState()
                            when (requestsState) {
                                is com.example.manospy.util.NetworkResult.Success -> {
                                    val data = (requestsState as com.example.manospy.util.NetworkResult.Success).data
                                    val filtered = data.filter { req ->
                                        val loc = req.location?.lowercase() ?: ""
                                        userCities.any { city -> loc.contains(city.lowercase()) }
                                    }

                                    if (filtered.isEmpty()) {
                                        Text(
                                            "No hay solicitudes nuevas en tus ciudades",
                                            fontSize = 14.sp,
                                            color = textSecondary
                                        )
                                    } else {
                                        filtered.forEach { req ->
                                            RequestCard(
                                                name = req.clientName ?: "Cliente",
                                                service = req.category,
                                                status = req.status.name,
                                                time = req.createdAt ?: "",
                                                icon = Icons.Default.Build,
                                                iconBg = blueLight,
                                                iconColor = primaryBlue,
                                                statusBg = blueLight,
                                                statusColor = primaryBlue
                                            )
                                        }
                                    }
                                }
                                is com.example.manospy.util.NetworkResult.Loading -> {
                                    CircularProgressIndicator(color = primaryBlue, modifier = Modifier.size(24.dp))
                                }
                                is com.example.manospy.util.NetworkResult.Error -> {
                                    Text(
                                        "Error cargando solicitudes",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                                else -> {
                                    // Estado Idle u otros
                                    Text(
                                        "Cargando...",
                                        fontSize = 14.sp,
                                        color = textSecondary
                                    )
                                }
                            }
                        } else {
                            // Fallback: mostrar tarjetas de ejemplo si no hay inyección de ViewModel
                            RequestCard(
                                name = "Juan Pérez",
                                service = "Reparación de Cañería",
                                status = "NUEVA",
                                time = "14:30",
                                icon = Icons.Default.Plumbing,
                                iconBg = blueLight,
                                iconColor = primaryBlue,
                                statusBg = blueLight,
                                statusColor = primaryBlue
                            )
                            
                            RequestCard(
                                name = "María García",
                                service = "Instalación Eléctrica",
                                status = "PENDIENTE",
                                time = "Ayer",
                                icon = Icons.Default.ElectricalServices,
                                iconBg = Color(0xFFF0FDF4),
                                iconColor = greenSuccess,
                                statusBg = Color(0xFFF3F4F6),
                                statusColor = textSecondary
                            )
                        }
                    }
                }
            }
            
            // Reservas Activas
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Reservas Activas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        
                        Button(
                            onClick = { navController?.navigate("professional_bookings") },
                            modifier = Modifier
                                .height(32.dp)
                                .padding(0.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Icon(
                                Icons.Default.CalendarMonth,
                                contentDescription = null,
                                modifier = Modifier.size(14.sp.value.dp),
                                tint = textSecondary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Ver calendario",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary
                            )
                        }
                    }
                    
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp)),
                        color = Color.White,
                        border = BorderStroke(1.dp, borderGray),
                        shadowElevation = 2.dp
                    ) {
                        Column {
                            // Header reserva
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .border(1.dp, borderGray),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape),
                                        color = Color(0xFFF3F4F6)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                Icons.Default.Person,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                                tint = textSecondary
                                            )
                                        }
                                    }
                                    
                                    Column {
                                        Text(
                                            "Ana Martínez",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                        
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .background(greenSuccess, shape = CircleShape)
                                            )
                                            Text(
                                                "EN CAMINO",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = greenSuccess
                                            )
                                        }
                                    }
                                }
                                
                                Surface(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    color = primaryBlue
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Default.Navigation,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp),
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                            
                            // Footer reserva
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .background(Color(0xFFF9FAFB)),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "Inicio",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB0B9C3)
                                        )
                                        Text(
                                            "09:00",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                    }
                                    
                                    Box(
                                        modifier = Modifier
                                            .width(1.dp)
                                            .height(24.dp)
                                            .background(Color(0xFFE5E7EB))
                                    )
                                    
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            "Ubicación",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFB0B9C3)
                                        )
                                        Text(
                                            "Villa Morra",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                    }
                                }
                                
                                Text(
                                    "Detalles",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryBlue,
                                    modifier = Modifier.clickable {
                                        navController?.navigate("professional_bookings")
                                    }
                                )
                            }
                        }
                    }
                }
            }
            
            // Botones acceso rápido
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        QuickActionCard(
                            title = "Ver métricas",
                            subtitle = "Estadísticas de mes",
                            icon = Icons.Default.Analytics,
                            iconBg = blueLight,
                            iconColor = primaryBlue,
                            onClick = { navController?.navigate("professional_metrics") },
                            modifier = Modifier.weight(1f)
                        )
                        
                        QuickActionCard(
                            title = "Editar perfil",
                            subtitle = "Información y servicios",
                            icon = Icons.Default.Person,
                            iconBg = Color(0xFFF3F4F6),
                            iconColor = Color(0xFF4B5563),
                            onClick = { navController?.navigate("professional_profile") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
        
        // Bottom navigation
        BottomNavBar(
            navController = navController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun RequestCard(
    name: String,
    service: String,
    status: String,
    time: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconColor: Color,
    statusBg: Color,
    statusColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    color = iconBg
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = iconColor,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Column {
                    Text(
                        name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        service,
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp)),
                    color = statusBg
                ) {
                    Text(
                        status,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        letterSpacing = 0.05.sp
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    time,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconBg: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp)),
                color = iconBg
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(28.sp.value.dp)
                    )
                }
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )
                Text(
                    subtitle,
                    fontSize = 10.sp,
                    color = Color(0xFF9CA3AF)
                )
            }
        }
    }
}

@Composable
private fun BottomNavBar(
    navController: NavController?,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color.White.copy(alpha = 0.8f),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(
                label = "Panel",
                icon = Icons.Default.GridView,
                isActive = true,
                onClick = { }
            )
            NavBarItem(
                label = "Historial",
                icon = Icons.Default.History,
                isActive = false,
                onClick = { navController?.navigate("professional_history") }
            )
            NavBarItem(
                label = "Pagos",
                icon = Icons.Default.Payments,
                isActive = false,
                onClick = { navController?.navigate("professional_payments") }
            )
            NavBarItem(
                label = "Perfil",
                icon = Icons.Default.AccountCircle,
                isActive = false,
                onClick = { navController?.navigate("professional_profile") }
            )
        }
    }
}

@Composable
private fun NavBarItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isActive) Color(0xFF0056D2) else Color(0xFF9CA3AF),
            modifier = Modifier.size(20.dp)
        )
        Text(
            label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (isActive) Color(0xFF0056D2) else Color(0xFF9CA3AF),
            letterSpacing = 0.05.sp
        )
    }
}
