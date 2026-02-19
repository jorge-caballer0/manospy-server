package com.example.manospy.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.manospy.data.model.Reservation
import com.example.manospy.data.model.ServiceRequest
import com.example.manospy.data.model.User
import com.example.manospy.data.model.ReservationStatus
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.ui.navigation.Screen
import com.example.manospy.util.NetworkResult
import com.example.manospy.ui.components.FilterCard
import com.example.manospy.ui.components.ServiceOfferCard
import com.example.manospy.ui.components.ServiceStatusCard
import com.example.manospy.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHistoryScreen(
    navController: NavController,
    viewModel: ServiceViewModel,
    currentUser: User?
) {
    // ============== COLORES ==============
    val primaryBlue = Color(0xFF2563EB)
    val bgLight = Color(0xFFF6F7F8)
    val textPrimary = Color(0xFF0D141B)
    val textSecondary = Color(0xFF4E7397)
    val textGray = Color(0xFF94A3B8)
    val borderGray = Color(0xFFE2E8F0)
    val bgCard = Color.White
    val successColor = Color(0xFF10B981)
    val warningColor = Color(0xFFF59E0B)
    val errorColor = Color(0xFFEF4444)

    // ============== ESTADO ==============
    val reservationsState by viewModel.reservations.collectAsState()
    val serviceRequestsState by viewModel.pendingRequests.collectAsState()

    val selectedTab = remember { mutableIntStateOf(0) }
    val tabs = listOf("Todas", "Completadas", "En proceso", "Pendientes")

    // ============== EFECTOS ==============
    LaunchedEffect(currentUser?.id) {
        val userId = currentUser?.id
        if (!userId.isNullOrEmpty()) {
            viewModel.fetchReservations(userId)
            viewModel.fetchServiceRequests()
        }
    }

    // ============== UI ==============
    AppScaffold(
        title = "Reservas",
        onBackClick = { navController.popBackStack() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(bgLight)
                .padding(paddingValues)
        ) {
            // ===== FILTROS SIMPLES (Chips/Botones) =====
            Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Triple(0, "Todas", Color(0xFF2563EB)),
                Triple(1, "Completadas", Color(0xFF10B981)),
                Triple(2, "En proceso", Color(0xFF0891B2)),
                Triple(3, "Pendientes", Color(0xFFF59E0B))
            ).forEach { (index, label, color) ->
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { selectedTab.intValue = index },
                    color = if (selectedTab.intValue == index) color else Color.White,
                    shape = RoundedCornerShape(20.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (selectedTab.intValue == index) color else Color(0xFFE5E7EB)
                    ),
                    shadowElevation = 1.dp
                ) {
                    Text(
                        label,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (selectedTab.intValue == index) Color.White else Color(0xFF1F2937),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // ===== CONTENIDO SEGÚN TAB =====
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (selectedTab.intValue) {
                0 -> TabContentAll(
                    viewModel = viewModel,
                    reservations = viewModel.getFilteredAllReservations(),
                    serviceRequests = viewModel.getFilteredPendingRequests(),
                    navController = navController,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    textGray = textGray,
                    bgCard = bgCard,
                    successColor = successColor,
                    warningColor = warningColor,
                    errorColor = errorColor,
                    borderGray = borderGray,
                    bgLight = bgLight
                )
                1 -> TabContent(
                    viewModel = viewModel,
                    items = viewModel.getFilteredCompletedReservations(),
                    navController = navController,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    textGray = textGray,
                    bgCard = bgCard,
                    successColor = successColor,
                    warningColor = warningColor,
                    errorColor = errorColor,
                    borderGray = borderGray,
                    bgLight = bgLight,
                    isReservations = true
                )
                2 -> TabContent(
                    viewModel = viewModel,
                    items = viewModel.getFilteredInProcessReservations(),
                    navController = navController,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    textGray = textGray,
                    bgCard = bgCard,
                    successColor = successColor,
                    warningColor = warningColor,
                    errorColor = errorColor,
                    borderGray = borderGray,
                    bgLight = bgLight,
                    isReservations = true
                )
                3 -> TabContentRequests(
                    viewModel = viewModel,
                    items = viewModel.getFilteredPendingRequests(),
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    textGray = textGray,
                    bgCard = bgCard,
                    errorColor = errorColor,
                    borderGray = borderGray,
                    bgLight = bgLight
                )
            }
        }
        }
    }
}

@Composable
fun TabContent(
    viewModel: ServiceViewModel,
    items: List<Reservation>,
    navController: NavController,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    textGray: Color,
    bgCard: Color,
    successColor: Color,
    warningColor: Color,
    errorColor: Color,
    borderGray: Color,
    bgLight: Color,
    isReservations: Boolean
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (items.isEmpty()) {
            item {
                EmptyState(
                    title = "No hay elementos",
                    description = if (isReservations) "Cuando realices una reserva, aparecerá aquí" else "No hay solicitudes pendientes",
                    textPrimary = textPrimary,
                    textGray = textGray
                )
            }
        } else {
            items(items) { reservation ->
                ServiceOfferCard(
                    title = reservation.serviceName ?: "Reserva",
                    subtitle = listOfNotNull(reservation.professionalName, reservation.date).joinToString(" · "),
                    status = reservation.status.name,
                    statusTag = when (reservation.status) {
                        ReservationStatus.COMPLETED -> "Completada"
                        ReservationStatus.ACCEPTED -> "En Proceso"
                        ReservationStatus.PENDING -> "Pendiente"
                        ReservationStatus.CANCELLED -> "Cancelada"
                    },
                    tagColor = when (reservation.status) {
                        ReservationStatus.COMPLETED -> successColor
                        ReservationStatus.ACCEPTED -> warningColor
                        ReservationStatus.PENDING -> warningColor
                        ReservationStatus.CANCELLED -> errorColor
                    },
                    onClick = { navController.navigate(Screen.ReservationAccepted.createRoute(reservation.id)) },
                    trailingContent = if (reservation.status == ReservationStatus.PENDING) {
                        {
                            OutlinedButton(onClick = {
                                viewModel.cancelReservation(reservation.id)
                                viewModel.fetchReservations()
                                viewModel.fetchServiceRequests()
                            }, colors = ButtonDefaults.outlinedButtonColors(contentColor = errorColor)) {
                                Text("Cancelar")
                            }
                        }
                    } else null
                )
            }
        }
    }
}

@Composable
fun EmptyState(title: String, description: String, textPrimary: Color, textGray: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textPrimary)
            Text(description, fontSize = 13.sp, color = textGray, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun LoadingState(color: Color) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = color, modifier = Modifier.size(40.dp))
    }
}

@Composable
fun ErrorState(errorColor: Color, textGray: Color) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Error al cargar datos", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = errorColor)
            Text("Intenta nuevamente", fontSize = 12.sp, color = textGray)
        }
    }
}

@Composable
fun TabContentAll(
    viewModel: ServiceViewModel,
    reservations: List<Reservation>,
    serviceRequests: List<ServiceRequest>,
    navController: NavController,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    textGray: Color,
    bgCard: Color,
    successColor: Color,
    warningColor: Color,
    errorColor: Color,
    borderGray: Color,
    bgLight: Color
) {
    val allItems = (reservations.map { it as Any } + serviceRequests.map { it as Any })

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (allItems.isEmpty()) {
            item {
                EmptyState(
                    title = "No tienes reservas ni solicitudes",
                    description = "Cuando publiques una solicitud o reserves un servicio, aparecerá aquí",
                    textPrimary = textPrimary,
                    textGray = textGray
                )
            }
        } else {
            items(allItems) { item ->
                when (item) {
                    is Reservation -> ServiceOfferCard(
                        title = item.serviceName ?: "Reserva",
                        subtitle = listOfNotNull(item.professionalName, item.date).joinToString(" · "),
                        status = item.status.name,
                        statusTag = when (item.status) {
                            ReservationStatus.COMPLETED -> "Completada"
                            ReservationStatus.ACCEPTED -> "En Proceso"
                            ReservationStatus.PENDING -> "Pendiente"
                            ReservationStatus.CANCELLED -> "Cancelada"
                        },
                        tagColor = when (item.status) {
                            ReservationStatus.COMPLETED -> successColor
                            ReservationStatus.ACCEPTED -> warningColor
                            ReservationStatus.PENDING -> warningColor
                            ReservationStatus.CANCELLED -> errorColor
                        },
                        onClick = { navController.navigate(Screen.ReservationAccepted.createRoute(item.id)) },
                        trailingContent = if (item.status == ReservationStatus.PENDING) {
                            {
                                OutlinedButton(onClick = {
                                    viewModel.cancelReservation(item.id)
                                    viewModel.fetchReservations()
                                    viewModel.fetchServiceRequests()
                                }, colors = ButtonDefaults.outlinedButtonColors(contentColor = errorColor)) {
                                    Text("Cancelar")
                                }
                            }
                        } else null
                    )
                    is ServiceRequest -> ServiceOfferCard(
                        title = item.category ?: "Solicitud",
                        subtitle = item.description.takeIf { it.length <= 80 } ?: item.description.take(80) + "...",
                        status = item.status.name,
                        statusTag = "Pendiente",
                        tagColor = warningColor,
                        onClick = { /* quizá navegar a detalle de solicitud */ },
                        trailingContent = {
                            OutlinedButton(onClick = {
                                viewModel.cancelServiceRequest(item.id)
                                viewModel.fetchServiceRequests()
                                viewModel.fetchReservations()
                            }, colors = ButtonDefaults.outlinedButtonColors(contentColor = errorColor)) {
                                Text("Cancelar")
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TabContentRequests(
    viewModel: ServiceViewModel,
    items: List<ServiceRequest>,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    textGray: Color,
    bgCard: Color,
    errorColor: Color,
    borderGray: Color,
    bgLight: Color
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(bgLight),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (items.isEmpty()) {
            item {
                EmptyState(
                    title = "No hay solicitudes pendientes",
                    description = "Cuando publiques una solicitud, aparecerá aquí",
                    textPrimary = textPrimary,
                    textGray = textGray
                )
            }
        } else {
            items(items) { request ->
                ServiceRequestCard(
                    request = request,
                    primaryBlue = primaryBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    textGray = textGray,
                    bgCard = bgCard,
                    borderGray = borderGray,
                    onCancel = {
                        viewModel.cancelServiceRequest(request.id)
                        viewModel.fetchServiceRequests()
                        viewModel.fetchReservations()
                    }
                )
            }
        }
    }
}

@Composable
fun ReservationHistoryCard(
    reservation: Reservation,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    textGray: Color,
    bgCard: Color,
    successColor: Color,
    warningColor: Color,
    errorColor: Color,
    borderGray: Color,
    onCardClick: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    // ===== COLORES SEGÚN ESTADO =====
    val (statusColor, statusTextColor, statusLabel) = when (reservation.status) {
        ReservationStatus.COMPLETED -> Triple(
            Color(0xFFDCFCE7),
            successColor,
            "Completado"
        )
        ReservationStatus.ACCEPTED -> Triple(
            Color(0xFFE0F2FE),
            Color(0xFF0369A1),
            "En proceso"
        )
        ReservationStatus.CANCELLED -> Triple(
            Color(0xFFFEE2E2),
            errorColor,
            "Cancelado"
        )
        ReservationStatus.PENDING -> Triple(
            Color(0xFFFEF3C7),
            warningColor,
            "Pendiente"
        )
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCardClick() }
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        color = bgCard,
        shape = RoundedCornerShape(16.dp)
    ) {
        val showCancelDialog = remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ===== ENCABEZADO: TIPO DE SERVICIO + ESTADO =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        reservation.serviceName ?: "Servicio",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textGray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        reservation.serviceRequestId,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = statusColor,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        statusLabel,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusTextColor
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = borderGray)

            // ===== PROFESIONAL =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = primaryBlue
                )
                Text(
                    reservation.professionalName ?: "Por asignar",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (reservation.professionalName != null) textPrimary else textGray
                )
            }

            // ===== FECHA Y HORA =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = primaryBlue
                )
                Column {
                    Text(
                        reservation.date ?: "Fecha por confirmar",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = textPrimary
                    )
                    if (!reservation.timeStart.isNullOrEmpty()) {
                        Text(
                            "${reservation.timeStart}${
                                if (!reservation.timeEnd.isNullOrEmpty()) 
                                    " - ${reservation.timeEnd}" 
                                else ""
                            }",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                }
            }
            // ===== ACCIONES (Cancelar si está pendiente) =====
            if (reservation.status == ReservationStatus.PENDING) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    OutlinedButton(
                        onClick = { showCancelDialog.value = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = errorColor)
                    ) {
                        Text("Cancelar")
                    }
                }

                if (showCancelDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showCancelDialog.value = false },
                        title = { Text("Confirmar cancelación") },
                        text = { Text("¿Desea cancelar esta reserva? Esta acción no se puede deshacer.") },
                        confirmButton = {
                            TextButton(onClick = {
                                showCancelDialog.value = false
                                onCancel()
                            }) { Text("Sí, cancelar") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showCancelDialog.value = false }) { Text("No") }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceRequestCard(
    request: ServiceRequest,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    textGray: Color,
    bgCard: Color,
    borderGray: Color,
    onCancel: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        color = bgCard,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ===== ENCABEZADO: TIPO DE SERVICIO + ESTADO =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Solicitud de servicio",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textGray,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        request.category,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "Pendiente",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF59E0B)
                    )
                }
            }

            HorizontalDivider(thickness = 0.5.dp, color = borderGray)

            // ===== DESCRIPCIÓN =====
            Text(
                request.description,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = textSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // ===== UBICACIÓN (SI APLICA) =====
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = primaryBlue
                )
                Text(
                    request.location ?: "Ubicación no especificada",
                    fontSize = 12.sp,
                    color = textGray
                )
            }

            // ===== FECHA PREFERIDA (SI APLICA) =====
            if (!request.preferredDate.isNullOrEmpty()) {
                Text(
                    "Fecha preferida: ${request.preferredDate}",
                    fontSize = 12.sp,
                    color = textSecondary
                )
            }
                    val showCancelDialogReq = remember { mutableStateOf(false) }
                    // ===== ACCIONES: Cancelar solicitud directa =====
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { showCancelDialogReq.value = true }, colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))) {
                            Text("Cancelar solicitud")
                        }
                    }
                    if (showCancelDialogReq.value) {
                        AlertDialog(
                            onDismissRequest = { showCancelDialogReq.value = false },
                            title = { Text("Confirmar cancelación") },
                            text = { Text("¿Desea cancelar esta solicitud? Esta acción eliminará la solicitud.") },
                            confirmButton = {
                                TextButton(onClick = {
                                    showCancelDialogReq.value = false
                                    onCancel()
                                }) { Text("Sí, cancelar") }
                            },
                            dismissButton = {
                                TextButton(onClick = { showCancelDialogReq.value = false }) { Text("No") }
                            }
                        )
                    }
        }
    }
}
