package com.example.manospy.ui.screens.client

import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.manospy.data.model.Reservation
import com.example.manospy.data.model.ReservationStatus
import com.example.manospy.ui.navigation.BottomNavScreen
import com.example.manospy.ui.navigation.Screen
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors
import kotlinx.coroutines.delay

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewmodel.compose.viewModel

// Helper function to build date-time string
fun buildDateTimeString(reservation: Reservation?, step2Data: Map<String, Any>?): String {
    // Primero intentar obtener de step2Data
    if (step2Data != null && step2Data.isNotEmpty()) {
        val date = step2Data["date"] as? String ?: ""
        val time = step2Data["time"] as? String ?: ""
        if (date.isNotBlank()) {
            return date + (if (time.isNotBlank()) " - $time" else "")
        }
    }

    if (reservation == null) return "Por confirmar"

    val datePart = reservation.date ?: "Fecha no definida"
    val timePart = if (!reservation.timeStart.isNullOrEmpty()) {
        " a las ${reservation.timeStart}" +
        (if (!reservation.timeEnd.isNullOrEmpty()) " - ${reservation.timeEnd}" else "")
    } else {
        ""
    }

    return datePart + timePart
}

@Composable
fun ReservationPendingScreen(
    navController: NavController,
    reservationId: String,
    viewModel: ServiceViewModel,
    onBack: () -> Unit = { navController.popBackStack() }
) {
    // Colores del tema
    val primaryBlue = Color(0xFF2563EB)
    val bgLight = Color(0xFFF8FAFC)
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val yellowBg = Color(0xFFFEF3C7)
    val yellowBorder = Color(0xFFF59E0B)
    val yellowText = Color(0xFF92400E)
    val successGreen = Color(0xFF10B981)

    // Estados para diálogos
    var showCancelConfirmDialog by remember { mutableStateOf(false) }
    var showCancelSuccessDialog by remember { mutableStateOf(false) }

    // Estado para animación de progreso
    var currentStep by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        delay(2000) // Esperar 2 segundos antes de empezar
        currentStep = 1 // Publicando completado
        delay(4000) // Esperar 4 segundos más
        currentStep = 2 // Notificando completado
        // El último se queda en verde hasta aceptación
    }

    // Estado para dots animados del título
    var dots by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            delay(800) // Aumentado de 500 a 800ms
            dots = when (dots.length) {
                0 -> "."
                1 -> ".."
                2 -> "..."
                else -> ""
            }
        }
    }

    // Estado para animación de puntos por item
    val dotsStates = remember { mutableStateListOf("", "", "") }
    LaunchedEffect(Unit) {
        while (true) {
            delay(800) // Aumentado de 500 a 800ms
            for (i in 0..2) {
                // Solo animar el paso actual, no los completados
                if (i == currentStep) {
                    dotsStates[i] = when (dotsStates[i].length) {
                        0 -> "."
                        1 -> ".."
                        2 -> "..."
                        else -> ""
                    }
                } else {
                    dotsStates[i] = "" // Sin puntos en otros pasos
                }
            }
        }
    }

    // Función para determinar color del item
    fun getItemColor(index: Int): Color {
        return if (index < currentStep) Color(0xFF0EA5E9) else Color(0xFF10B981) // Azul completado, verde pendiente
    }

    fun getItemBgColor(index: Int): Color {
        return if (index < currentStep) Color(0xFFEBF4FF) else Color(0xFFF0FDF4) // Azul claro completado, verde claro pendiente
    }

    fun getItemBorderColor(index: Int): Color {
        return if (index < currentStep) Color(0xFFBFDBFE) else Color(0xFFBBF7D0) // Azul borde completado, verde borde pendiente
    }

    // Animación de rotación para el loader
    val infiniteTransition = rememberInfiniteTransition(label = "loader")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    // Observar cambios en el estado de reserva
    val reservationDetail by viewModel.reservationDetail.collectAsState()
    val step1Data by viewModel.reservationStep1Data.collectAsState()
    val step2Data by viewModel.reservationStep2Data.collectAsState()

    // No hacer polling a la API porque falla por permisos de usuario
    // La reservación se carga desde el ViewModel cuando se crea

    AppScaffold(title = "Solicitud Pendiente", onBackClick = onBack) { paddingValues ->
        val innerPadding = paddingValues
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Loader animado
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(primaryBlue, Color(0xFF60A5FA))
                        ),
                        shape = CircleShape
                    )
                    .rotate(rotation),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.HourglassBottom, // Hourglass para espera
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Título con dots
            Text(
                "Esperando a un profesional$dots",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Estamos notificando a los profesionales disponibles en tu área",
                fontSize = 16.sp,
                color = textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Cards animadas
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val items = listOf(
                    "Publicando solicitud" to 0,
                    "Notificando profesionales" to 1,
                    "Esperando respuestas" to 2
                )

                items.forEachIndexed { index, (text, delay) ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInHorizontally(initialOffsetX = { -20 })
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, getItemBorderColor(index), RoundedCornerShape(16.dp)),
                            color = getItemBgColor(index)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(getItemColor(index), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                // Mostrar puntos animados en todos los pasos que están activos o en progreso
                                val displayText = if (index <= currentStep) {
                                    "$text${dotsStates[index]}"
                                } else {
                                    text
                                }
                                Text(displayText, fontSize = 14.sp, color = if (index < currentStep) Color(0xFF1E40AF) else Color(0xFF065F46))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Card de advertencia
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, yellowBorder, RoundedCornerShape(16.dp)),
                color = yellowBg
            ) {
                Text(
                    "⏱️ Esto puede tomar entre 5-15 minutos. Te notificaremos cuando un profesional acepte tu solicitud.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    color = yellowText,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Sección de acciones integrada
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp)),
                color = Color(0xFFF8FAFC),
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Acciones disponibles",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = textPrimary
                    )

                    // Ver detalles de solicitud - Botón principal
                    Button(
                        onClick = {
                            navController.navigate(Screen.ReservationDetail.createRoute(reservationId, pending = true))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Ver detalles de solicitud",
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }

                    // Botones secundarios en fila
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Volver al inicio
                        OutlinedButton(
                            onClick = {
                                navController.navigate(BottomNavScreen.ClientHome.route) {
                                    popUpTo(Screen.ReservationPending.route) { inclusive = false }
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = textSecondary
                            ),
                            border = BorderStroke(1.5.dp, Color(0xFFE2E8F0)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Home,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Inicio",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
                                )
                            }
                        }

                        // Cancelar solicitud
                        OutlinedButton(
                            onClick = {
                                showCancelConfirmDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFDC2626)
                            ),
                            border = BorderStroke(1.5.dp, Color(0xFFFECACA)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Cancelar",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Diálogo de confirmación de cancelación
    if (showCancelConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showCancelConfirmDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Cancelar Solicitud",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas cancelar esta solicitud? Esta acción no se puede deshacer.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelConfirmDialog = false
                        viewModel.cancelServiceRequest(reservationId)
                        showCancelSuccessDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Sí, Cancelar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCancelConfirmDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("No, Mantener", fontWeight = FontWeight.Bold, color = Color(0xFF0EA5E9))
                }
            },
            containerColor = Color.White,
            titleContentColor = Color(0xFF1E293B),
            textContentColor = Color(0xFF64748B),
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Diálogo de éxito de cancelación
    if (showCancelSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showCancelSuccessDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Solicitud Cancelada",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            },
            text = {
                Text(
                    "Tu solicitud ha sido cancelada exitosamente.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCancelSuccessDialog = false
                        navController.navigate(BottomNavScreen.ClientHome.route) {
                            popUpTo(Screen.ReservationPending.route) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Volver al Inicio", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            titleContentColor = Color(0xFF1E293B),
            textContentColor = Color(0xFF64748B),
            shape = RoundedCornerShape(20.dp)
        )
    }
}


