package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.manospy.data.model.Reservation
import com.example.manospy.data.model.ReservationStatus
import com.example.manospy.ui.viewmodel.ServiceViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manospy.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(
    reservationId: String,
    navController: NavController,
    viewModel: ServiceViewModel = viewModel()
) {
    val primaryCyan = Color(0xFF00E5D1)
    val backgroundColor = Color(0xFFF9FAFB)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)

    val reservationState by viewModel.reservationDetail.collectAsState()

    LaunchedEffect(reservationId) {
        viewModel.fetchReservationDetail(reservationId)
    }

    AppScaffold(
        title = "Detalle de Reserva",
        headerBackgroundColor = Color(0xFF10B981),
        headerTextColor = Color.White,
        onBackClick = { navController.popBackStack() },
        hasBottomNavigation = false
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            when (val reservation = reservationState) {
                is Reservation -> {
                    // Estado
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Confirmada",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            color = Color(0xFF166534),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    reservation.serviceName?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        )
                    }


                    Text("#${reservation.serviceRequestId}", color = textSecondary, fontSize = 14.sp)

                    // Fecha y hora
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        Column {
                            Text("Fecha", fontWeight = FontWeight.Bold, color = textPrimary)
                            Text(reservation.date ?: "-", color = textSecondary)
                        }
                        Column {
                            Text("Hora", fontWeight = FontWeight.Bold, color = textPrimary)
                            Text(
                                if (!reservation.timeStart.isNullOrEmpty() && !reservation.timeEnd.isNullOrEmpty()) "${reservation.timeStart} - ${reservation.timeEnd}" else (reservation.timeStart ?: "-"),
                                color = textSecondary
                            )
                        }
                        Column {
                            Text("Duración", fontWeight = FontWeight.Bold, color = textPrimary)
                            Text(reservation.durationMinutes?.let { "$it min" } ?: "-", color = textSecondary)
                        }
                    }

                    // Tipo y ubicación
                    Column {
                        Text("Tipo", fontWeight = FontWeight.Bold, color = textPrimary)
                        Text(reservation.type ?: "-", color = textSecondary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Ubicación", fontWeight = FontWeight.Bold, color = textPrimary)
                        Text(reservation.location ?: "-", color = textSecondary)
                    }

                    // Profesional
                    Column {
                        Text("Profesional", fontWeight = FontWeight.Bold, color = textPrimary)
                        Text("Dra. Sofía Martínez", color = primaryCyan, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("4.9", fontWeight = FontWeight.Bold, color = textPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Fisioterapeuta", color = textSecondary)
                        }
                    }

                    // Notas del cliente
                    Column {
                        Text("Notas del Cliente", fontWeight = FontWeight.Bold, color = textPrimary)
                        Text(
                            "\"Por favor, llegar 10 minutos antes. Contamos con estacionamiento frente al edificio.\"",
                            color = textSecondary
                        )
                    }

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = { navController.navigate("reschedule/${reservation.id}") },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Modificar Reserva", fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = { viewModel.cancelReservation(reservation.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cancelar Cita", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                else -> {
                    CircularProgressIndicator(color = primaryCyan)
                }
            }
        }
    }
}
