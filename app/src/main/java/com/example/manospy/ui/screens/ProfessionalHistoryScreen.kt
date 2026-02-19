package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.manospy.data.model.Reservation
import com.example.manospy.data.model.ReservationStatus
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.util.NetworkResult
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalHistoryScreen(viewModel: ServiceViewModel = viewModel()) {
    val primaryCyan = Color(0xFF00E5D1)
    val backgroundColor = Color(0xFFF9FAFB)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)

    val reservationsState by viewModel.reservations.collectAsState()
    val selectedFilter = remember { mutableStateOf("Todos") }

    // Llamada inicial al backend
    LaunchedEffect(Unit) {
        viewModel.fetchReservations(userId = "prof123") // reemplazá con el ID real del profesional
    }

    // Filtrado dinámico según el chip seleccionado
    val filteredReservations = when (selectedFilter.value) {
        "Completados" -> (reservationsState as? NetworkResult.Success)?.data
            ?.filter { it.status == ReservationStatus.COMPLETED } ?: emptyList()
        "Cancelados" -> (reservationsState as? NetworkResult.Success)?.data
            ?.filter { it.status == ReservationStatus.CANCELLED } ?: emptyList()
        else -> (reservationsState as? NetworkResult.Success)?.data ?: emptyList()
    }

    AppScaffold(
        title = "Historial",
        onBackClick = { },
        hasBottomNavigation = true,
        modifier = Modifier.background(backgroundColor)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            // Earnings Stats Card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, primaryCyan.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "TOTAL GANADO ESTE MES",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = textSecondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₲",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = primaryCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "2.450.000",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    color = textPrimary,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }
                    }
                }
            }

            // Filter Chips dinámicos
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf("Todos", "Completados", "Cancelados").forEach { label ->
                        val selected = selectedFilter.value == label
                        FilterChipPremium(
                            label = label,
                            isSelected = selected,
                            color = primaryCyan,
                            onClick = { selectedFilter.value = label }
                        )
                    }
                }
            }

            // Section Title
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Servicios Recientes",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = textPrimary
                        )
                    )
                    Surface(
                        color = primaryCyan.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Octubre 2023",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = primaryCyan,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }

            // Lista de reservas filtradas con estados del ViewModel
            if (reservationsState is NetworkResult.Success) {
                items(filteredReservations) { reservation ->
                    ProServiceHistoryCard(reservation, primaryCyan, textPrimary, textSecondary)
                }
            } else if (reservationsState is NetworkResult.Loading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryCyan)
                    }
                }
            } else if (reservationsState is NetworkResult.Error) {
                item {
                    Text(
                        text = "Error al cargar reservas",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }


            // Footer action
            item {
                TextButton(
                    onClick = { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Ver meses anteriores",
                            color = primaryCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = primaryCyan
                        )
                    }
                }
            }

            }

            // Bottom navigation should be a sibling of the LazyColumn
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                        label = { Text("Inicio") }
                    )
                    NavigationBarItem(
                        selected = true,
                        onClick = { },
                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                        label = { Text("Historial") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = primaryCyan,
                            selectedTextColor = primaryCyan,
                            indicatorColor = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        label = { Text("Agenda") }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { },
                        icon = { Icon(Icons.Outlined.Person, contentDescription = null) },
                        label = { Text("Perfil") }
                    )
                }
            }
        }
    }
}
@Composable
fun FilterChipPremium(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) color else color.copy(alpha = 0.1f),
        modifier = Modifier
            .height(40.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                color = if (isSelected) Color.White else Color(0xFF475569),
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ProServiceHistoryCard(
    reservation: Reservation,
    primaryCyan: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    val statusColor = when (reservation.status) {
        ReservationStatus.COMPLETED -> Color(0xFFDCFCE7)
        ReservationStatus.CANCELLED -> Color(0xFFFEE2E2)
        ReservationStatus.ACCEPTED -> Color(0xFFE0F2FE)
        ReservationStatus.PENDING -> Color(0xFFFFF3CD)
    }

    val statusTextColor = when (reservation.status) {
        ReservationStatus.COMPLETED -> Color(0xFF166534)
        ReservationStatus.CANCELLED -> Color(0xFF991B1B)
        ReservationStatus.ACCEPTED -> Color(0xFF0369A1)
        ReservationStatus.PENDING -> Color(0xFF856404)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Top Row: Status and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = statusColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = reservation.status.name,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = statusTextColor,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Fecha pendiente",
                        style = MaterialTheme.typography.bodySmall.copy(color = textSecondary)
                    )
                }

                when (reservation.status) {
                    ReservationStatus.COMPLETED -> {
                        Text(
                            text = "₲ 150.000", // ejemplo, luego vendrá del backend
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = textPrimary
                            )
                        )
                    }
                    ReservationStatus.CANCELLED -> {
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₲ 0",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFCBD5E1)
                                )
                            )
                            Text(
                                text = "Sin cargo",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color(0xFFF87171),
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                    else -> {}
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Middle Row: Title and Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reservation.serviceRequestId,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (reservation.status == ReservationStatus.CANCELLED) Color(0xFF94A3B8) else textPrimary
                    )
                )

                if (reservation.status == ReservationStatus.COMPLETED) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "5.0", // ejemplo, luego vendrá del backend
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Row: Client Info
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cliente: ${reservation.clientName ?: ""}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = textSecondary)
                )
            }
        }
    }
}
