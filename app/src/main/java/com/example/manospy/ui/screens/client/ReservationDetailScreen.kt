package com.example.manospy.ui.screens.client

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.util.NetworkResult
import com.example.manospy.ui.components.SimpleCardHeader
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreen(
    navController: NavController,
    reservationId: String,
    viewModel: ServiceViewModel
) {
    val reservationDetail by viewModel.reservationDetail.collectAsState()
    val reservationDetailLoading by viewModel.reservationDetailLoading.collectAsState()
    val reservationDetailError by viewModel.reservationDetailError.collectAsState()
    val pendingRequests by viewModel.pendingRequests.collectAsState()
    val lastCreatedRequest by viewModel.lastCreatedRequest.collectAsState()
    val step1Data by viewModel.reservationStep1Data.collectAsState()
    val context = LocalContext.current

    // Cargar detalles de la reservación cuando se monta la pantalla
    LaunchedEffect(reservationId) {
        if (reservationId.isNotEmpty() && reservationId != "null") {
            android.util.Log.d("ReservationDetailScreen", "Fetching details for reservationId: $reservationId")
            viewModel.fetchReservationDetail(reservationId)
        } else {
            android.util.Log.e("ReservationDetailScreen", "Invalid reservationId: $reservationId")
        }
    }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            // Obtener la lista actual de fotos
            val currentPhotos = (step1Data["photoUris"] as? List<Uri>)?.toMutableList() ?: mutableListOf()
            // Añadir la nueva foto
            currentPhotos.add(uri)
            // Actualizar en el ViewModel
            val category = step1Data["category"] as? String ?: ""
            val categoryLabel = step1Data["categoryLabel"] as? String ?: ""
            val description = step1Data["description"] as? String ?: ""
            viewModel.updateReservationStep1(category, categoryLabel, description, currentPhotos)
            
            android.util.Log.d("ReservationDetailScreen", "Photo added: $uri, total: ${currentPhotos.size}")
        }
    }

    // Colores del tema según diseño Stitch
    val primaryBlue = Color(0xFF2563EB)
    val bgLight = Color(0xFFF6F7F8)
    val textPrimary = Color(0xFF0D141B)
    val textSecondary = Color(0xFF64748B)
    val bgCard = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val pendingYellow = Color(0xFFFB923C)
    val acceptedGreen = Color(0xFF10B981)
    val completedGray = Color(0xFF64748B)
    val cancelledRed = Color(0xFFEF4444)

    // Los datos deberían estar disponibles desde la creación
    // No hacer llamada al API porque falla por permisos de usuario
    
    // Obtener el dato que vamos a mostrar
    // Primero intenta con reservationDetail; si es null y existe lastCreatedRequest construimos un Reservation temporal
    val categoryLabel = step1Data["categoryLabel"] as? String ?: ""
    val dataToShow: com.example.manospy.data.model.Reservation? = reservationDetail ?: lastCreatedRequest?.let { sr ->
        com.example.manospy.data.model.Reservation(
            id = sr.id,
            serviceRequestId = sr.id,
            clientId = sr.clientId,
            professionalId = "",
            status = com.example.manospy.data.model.ReservationStatus.PENDING,
            professionalName = null,
            clientName = sr.clientName,
            serviceName = categoryLabel.ifEmpty { sr.category },
            date = sr.preferredDate,
            timeStart = null,
            timeEnd = null,
            durationMinutes = null,
            type = "A Domicilio",
            location = sr.location,
            clientNotes = sr.description
        )
    }
    
    // Debug log
    LaunchedEffect(Unit) {
        android.util.Log.d("ReservationDetail", "reservationDetail: $reservationDetail, lastCreatedRequest: $lastCreatedRequest, reservationId: $reservationId")
    }

    AppScaffold(
        title = "Detalles de la Reserva",
        onBackClick = { navController.popBackStack() }
    ) { paddingValues ->
        if (dataToShow != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(bgLight)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Mostrar nota si estamos mostrando la última solicitud creada localmente
                    if (reservationDetail == null && lastCreatedRequest != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            color = Color(0xFFFFF3E0),
                            shape = RoundedCornerShape(12.dp),
                            shadowElevation = 2.dp,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD699))
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFFA500), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Tu solicitud ha sido registrada y está en búsqueda de profesionales. Pronto recibirás confirmación.", color = Color(0xFF8B5A00), fontSize = 12.sp)
                            }
                        }
                    }

                    ReservationDetailContent(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        reservation = dataToShow,
                        primaryBlue = primaryBlue,
                        bgLight = bgLight,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        bgCard = bgCard,
                        borderColor = borderColor,
                        pendingYellow = pendingYellow,
                        acceptedGreen = acceptedGreen,
                        completedGray = completedGray,
                        cancelledRed = cancelledRed,
                        step1Data = step1Data,
                        onAddPhoto = { photoPickerLauncher.launch("image/*") }
                    )

                    if (dataToShow != null) {
                        BottomBar(
                            onCloseClick = { navController.popBackStack() },
                            bgCard = bgCard,
                            primaryBlue = primaryBlue
                        )
                    }
                }
            }
        } else {
            // Mostrar estado de carga o error
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(bgLight),
                contentAlignment = Alignment.Center
            ) {
                when {
                    reservationDetailLoading -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            CircularProgressIndicator(color = primaryBlue)
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Cargando detalles...", color = textSecondary, fontSize = 14.sp)
                        }
                    }
                    !reservationDetailError.isNullOrEmpty() -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = reservationDetailError ?: "Error al cargar los detalles",
                                color = textSecondary,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.fetchReservationDetail(reservationId) }) {
                                Text("Reintentar")
                            }
                        }
                    }
                    else -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("No hay detalles disponibles.", color = textSecondary)
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(onClick = { viewModel.fetchReservationDetail(reservationId) }) {
                                Text("Reintentar")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservationDetailContent(
    modifier: Modifier = Modifier,
    reservation: com.example.manospy.data.model.Reservation,
    primaryBlue: Color,
    bgLight: Color,
    textPrimary: Color,
    textSecondary: Color,
    bgCard: Color,
    borderColor: Color,
    pendingYellow: Color,
    acceptedGreen: Color,
    completedGray: Color,
    cancelledRed: Color,
    step1Data: Map<String, Any>,
    onAddPhoto: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(bgLight)
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Estado Badge
        StatusBadge(
            status = reservation.status.name,
            pendingColor = pendingYellow,
            acceptedColor = acceptedGreen,
            completedColor = completedGray,
            cancelledColor = cancelledRed,
            textPrimary = textPrimary
        )

        // Tarjeta de Información
        ServiceInfoCard(
            reservation = reservation,
            primaryBlue = primaryBlue,
            bgCard = bgCard,
            borderColor = borderColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary
        )

        // Descripción
        if (!reservation.clientNotes.isNullOrEmpty()) {
            DescriptionSection(
                description = reservation.clientNotes!!,
                textPrimary = textPrimary,
                textSecondary = textSecondary,
                bgCard = bgCard,
                borderColor = borderColor
            )
        }

        // Fotos
        PhotosSection(
            photoUris = step1Data["photoUris"] as? List<android.net.Uri> ?: emptyList(),
            bgCard = bgCard,
            borderColor = borderColor,
            textPrimary = textPrimary,
            textSecondary = textSecondary,
            onAddPhoto = onAddPhoto
        )

        // Espacio para el botón inferior
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun StatusBadge(
    status: String,
    pendingColor: Color,
    acceptedColor: Color,
    completedColor: Color,
    cancelledColor: Color,
    textPrimary: Color
) {
    val (bgColor, textColor, label) = when (status.uppercase()) {
        "PENDING" -> Triple(
            pendingColor.copy(alpha = 0.1f),
            pendingColor,
            "Estado: Buscando Profesionales"
        )
        "ACTIVE" -> Triple(
            acceptedColor.copy(alpha = 0.1f),
            acceptedColor,
            "Estado: Aceptado"
        )
        "COMPLETED" -> Triple(
            completedColor.copy(alpha = 0.1f),
            completedColor,
            "Estado: Completado"
        )
        "CANCELLED" -> Triple(
            cancelledColor.copy(alpha = 0.1f),
            cancelledColor,
            "Estado: Cancelado"
        )
        else -> Triple(
            pendingColor.copy(alpha = 0.1f),
            pendingColor,
            "Estado: Buscando Profesionales"
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor, RoundedCornerShape(50.dp))
            .border(1.dp, textColor.copy(alpha = 0.3f), RoundedCornerShape(50.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Punto animado pulsante
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(textColor, RoundedCornerShape(50))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = textColor,
                fontSize = 14.sp
            )
        )
    }
}

@Composable
private fun ServiceInfoCard(
    reservation: com.example.manospy.data.model.Reservation,
    primaryBlue: Color,
    bgCard: Color,
    borderColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, borderColor, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = bgCard,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Servicio solicitado
            InfoItemRow(
                icon = Icons.Default.Plumbing,
                label = "Servicio solicitado",
                value = reservation.serviceName ?: "Servicio no especificado",
                primaryBlue = primaryBlue,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            Divider(color = borderColor.copy(alpha = 0.5f))

            // Fecha y Hora
            InfoItemRow(
                icon = Icons.Default.CalendarToday,
                label = "Fecha y Hora",
                value = buildString {
                    append(reservation.date ?: "No definida")
                    if (!reservation.timeStart.isNullOrEmpty()) {
                        append(" - ${reservation.timeStart}")
                    }
                },
                primaryBlue = primaryBlue,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )

            Divider(color = borderColor.copy(alpha = 0.5f))

            // Dirección
            InfoItemRow(
                icon = Icons.Default.LocationOn,
                label = "Dirección",
                value = reservation.location ?: "No especificada",
                primaryBlue = primaryBlue,
                textPrimary = textPrimary,
                textSecondary = textSecondary
            )
        }
    }
}

@Composable
private fun InfoItemRow(
    icon: ImageVector,
    label: String,
    value: String,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(12.dp),
            color = primaryBlue.copy(alpha = 0.1f)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = primaryBlue,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textSecondary,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
            )
        }
    }
}

@Composable
private fun DescriptionSection(
    description: String,
    textPrimary: Color,
    textSecondary: Color,
    bgCard: Color,
    borderColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Descripción",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            color = bgCard.copy(alpha = 0.5f)
        ) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textSecondary,
                    lineHeight = 22.sp
                ),
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
private fun PhotosSection(
    photoUris: List<android.net.Uri>,
    bgCard: Color,
    borderColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    onAddPhoto: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Fotos adjuntas",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textPrimary,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp
            ),
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Mostrar fotos reales
            if (photoUris.isNotEmpty()) {
                items(photoUris) { photoUri ->
                    Surface(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                        color = Color(0xFFF1F5F9)
                    ) {
                        coil.compose.AsyncImage(
                            model = photoUri,
                            contentDescription = "Foto de solicitud",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else {
                // Si no hay fotos, mostrar placeholder
                items(1) {
                    Surface(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.ImageNotSupported,
                                contentDescription = "Sin fotos",
                                tint = textSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Botón agregar foto
            item {
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(2.dp, borderColor, RoundedCornerShape(16.dp)),
                    color = bgCard
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { onAddPhoto() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar foto",
                            tint = textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomBar(
    onCloseClick: () -> Unit,
    bgCard: Color,
    primaryBlue: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = bgCard.copy(alpha = 0.9f),
        shadowElevation = 8.dp,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onCloseClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.elevatedButtonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Text(
                    text = "Cerrar Detalles",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                )
            }
        }
    }
}
