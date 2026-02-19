package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.manospy.data.model.Reservation
import com.example.manospy.ui.navigation.Screen
import com.example.manospy.ui.navigation.BottomNavScreen
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.util.NetworkResult
import com.example.manospy.ui.components.ServiceStatusCard
import com.example.manospy.ui.components.ServiceOfferCard
import com.example.manospy.ui.components.FilterCard
import com.example.manospy.ui.components.AppScaffold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientHomeScreen(
    navController: NavController? = null,
    currentUser: com.example.manospy.data.model.User? = null,
    serviceViewModel: ServiceViewModel? = null,
    onNavigateToCreateRequest: () -> Unit = {},
    onNavigateToService: (String) -> Unit = {}
) {
    val primaryBlue = Color(0xFF137FEC)
    val bgLight = Color(0xFFF6F7F8)
    val textPrimary = Color(0xFF0D141B)
    val textSecondary = Color(0xFF64748B)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)

    // Obtener datos del ViewModel
    val activeReservations = serviceViewModel?.getActiveReservations() ?: emptyList()
    val offers = serviceViewModel?.getOffers() ?: emptyList()

    val scope = rememberCoroutineScope()
    var navigateToProfile by remember { mutableStateOf(false) }
    var navigateToNotifications by remember { mutableStateOf(false) }

    // Navegar a Mi Perfil o Notificaciones
    if (navigateToProfile) {
        PersonalInformationScreen(
            user = currentUser,
            onBack = { navigateToProfile = false },
            navController = navController,
            viewModel = serviceViewModel
        )
        return
    }

    if (navigateToNotifications) {
        NotificationsReceivedScreen(
            onBack = { navigateToNotifications = false },
            navController = navController
        )
        return
    }

    // Categorías
    val categories = listOf(
        Triple("Plomería", Icons.Default.Build, Color(0xFF3B82F6)),
        Triple("Electricidad", Icons.Default.ElectricBolt, Color(0xFFFCD34D)),
        Triple("Limpieza", Icons.Default.Home, Color(0xFF10B981)),
        Triple("Clima", Icons.Default.AcUnit, Color(0xFFA855F7))
    )

    // Cargar datos al iniciar
    LaunchedEffect(currentUser?.id) {
        val userId = currentUser?.id
        if (!userId.isNullOrEmpty()) {
            try {
                serviceViewModel?.fetchReservations(userId)
                serviceViewModel?.fetchServiceRequests()
                serviceViewModel?.fetchReservationsByStatus("accepted")
            } catch (_: Exception) {}
        }
    }

    // Navegación automática
    LaunchedEffect(Unit) {
        val active = serviceViewModel?.getActiveReservationOrRequest()
        if (active != null && navController != null) {
            when (active) {
                is Reservation -> {
                    if (active.status.name.uppercase() == "ACCEPTED") {
                        navController.navigate(Screen.Chat.createRoute(active.id))
                    }
                }
            }
        }
    }

    val firstName = currentUser?.name?.split(" ")?.firstOrNull()

    AppScaffold(
        title = if (!firstName.isNullOrEmpty()) "Hola, $firstName" else "MANOSPY",
        subtitle = "¿En qué podemos ayudarte?",
        headerBackgroundColor = Color(0xFF0056D2),
        headerTextColor = Color.White,
        onBackClick = null,
        hasBottomNavigation = false,
        onRightIconClick = { navigateToNotifications = true },
        rightIcon = Icons.Default.Notifications,
        leftContent = {
            // Foto de perfil clickeable del usuario
            if (!currentUser?.avatarUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = currentUser?.avatarUrl,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { navigateToProfile = true },
                    contentScale = ContentScale.Crop
                )
            } else {
                // Avatar por defecto si no hay foto
                Surface(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { navigateToProfile = true },
                    color = Color(0xFFF0F4FF)
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Perfil",
                        modifier = Modifier
                            .size(48.dp)
                            .padding(8.dp),
                        tint = primaryBlue
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            // Se elimina la sección "Estado de Servicios" para mantener diseño: Categorías, Reservas Activas y Ofertas

            // CATEGORÍAS
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Categorías",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D141B)
                    )
                    Text(
                        text = "Ver todas",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2563EB),
                        modifier = Modifier.clickable {
                            navController?.navigate(Screen.ServiceCategories.route)
                        }
                    )
                }
            }

            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    color = Color.Transparent
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        repeat((categories.size + 1) / 2) { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                repeat(2) { col ->
                                    val index = row * 2 + col
                                    if (index < categories.size) {
                                        CategoryCard(
                                            name = categories[index].first,
                                            icon = categories[index].second,
                                            color = categories[index].third,
                                            modifier = Modifier.weight(1f),
                                            onClick = { onNavigateToService(categories[index].first) }
                                        )
                                    } else {
                                        Box(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // RESERVAS ACTIVAS
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Reservas Activas",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0D141B),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp)
                    )

                    if (activeReservations.isEmpty()) {
                        EmptyStateBox(
                            title = "Aún no tienes reservas",
                            subtitle = "Solicita un nuevo servicio para comenzar",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            activeReservations.forEach { r ->
                                ServiceOfferCard(
                                    title = r.serviceName ?: "Reserva",
                                    status = r.status?.name ?: "-",
                                    statusTag = when (r.status) {
                                        com.example.manospy.data.model.ReservationStatus.COMPLETED -> "Completada"
                                        com.example.manospy.data.model.ReservationStatus.ACCEPTED -> "En Proceso"
                                        com.example.manospy.data.model.ReservationStatus.PENDING -> "Pendiente"
                                        else -> r.status?.name ?: "-"
                                    },
                                    count = null,
                                    tagColor = when (r.status) {
                                        com.example.manospy.data.model.ReservationStatus.COMPLETED -> Color(0xFF10B981)
                                        com.example.manospy.data.model.ReservationStatus.ACCEPTED -> Color(0xFFF59E0B)
                                        com.example.manospy.data.model.ReservationStatus.PENDING -> Color(0xFFEF4444)
                                        else -> Color(0xFF64748B)
                                    },
                                    onClick = {
                                        navController?.navigate(com.example.manospy.ui.navigation.Screen.ReservationAccepted.createRoute(r.id))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // ACCIONES RÁPIDAS (botones compactos)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            scope.launch {
                                if (serviceViewModel?.hasPendingRequest() == true) {
                                    val pending = (serviceViewModel.pendingRequests.value as? NetworkResult.Success)?.data?.firstOrNull()
                                    if (pending != null) {
                                        navController?.navigate(Screen.ReservationPending.createRoute(pending.id))
                                    }
                                } else {
                                    navController?.navigate(BottomNavScreen.ClientSolicitar.route)
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                        contentPadding = PaddingValues(4.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxHeight()
                        ) {
                            Icon(
                                Icons.Default.AddCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "Nuevo",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { navController?.navigate(BottomNavScreen.ClientReservas.route) }
                            .shadow(1.dp, RoundedCornerShape(12.dp)),
                        color = cardBg
                    ) {
                        Column(
                            modifier = Modifier.padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "Historial",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        }
                    }
                }
            }

            // OFERTAS DE PROFESIONALES
            item {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ofertas de Profesionales",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = "Ver todo",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryBlue,
                            modifier = Modifier.clickable {
                                navController?.navigate(Screen.ProfessionalOffers.route)
                            }
                        )
                    }

                    if (offers.isEmpty()) {
                        EmptyStateBox(
                            title = "Sin ofertas disponibles",
                            subtitle = "No hay profesionales con ofertas en este momento",
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    } else {
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(end = 16.dp)
                        ) {
                            items(offers, key = { it.id }) { offer ->
                                OfferCard(
                                    offer = offer,
                                    primaryBlue = primaryBlue,
                                    cardBg = cardBg,
                                    borderColor = borderColor,
                                    onViewOfferClick = {
                                        serviceViewModel?.setSelectedOffer(offer)
                                        navController?.navigate(Screen.ProfessionalOfferDetail.route)
                                    }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun CategoryCard(
    name: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFECEFF3))
    ) {
        Column(
            modifier = Modifier
                .padding(14.dp)
                .width(IntrinsicSize.Min),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = name,
                    modifier = Modifier.size(26.dp),
                    tint = color
                )
            }

            Text(
                text = name,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0F172A)
            )
        }
    }
}

@Composable
private fun ActiveReservationItem(
    reservation: Reservation,
    primaryBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    cardBg: Color,
    borderColor: Color,
    navController: NavController?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = primaryBlue.copy(alpha = 0.1f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Construction,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = reservation.serviceName ?: "Servicio",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            text = reservation.date ?: reservation.timeStart ?: "Hoy, 14:30 PM",
                            fontSize = 12.sp,
                            color = textSecondary
                        )
                    }
                }

                Surface(
                    color = primaryBlue,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "En Camino",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            HorizontalDivider(color = Color(0xFFF3F4F6), thickness = 1.dp)

            // Professional info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier.size(32.dp),
                        shape = CircleShape,
                        color = Color.LightGray
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = reservation.professionalName ?: "Profesional",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = textPrimary
                        )
                        Text(
                            text = "Técnico Certificado",
                            fontSize = 11.sp,
                            color = textSecondary
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF3F4F6), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = null,
                            tint = primaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            navController?.navigate(Screen.Chat.createRoute(reservation.id))
                        },
                        modifier = Modifier
                            .size(36.dp)
                            .background(Color(0xFFF3F4F6), CircleShape)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            tint = primaryBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Progress bar
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { 0.7f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = primaryBlue,
                trackColor = Color(0xFFF3F4F6)
            )
        }
    }
}

@Composable
private fun OfferCard(
    offer: com.example.manospy.ui.viewmodel.ProfessionalOffer,
    primaryBlue: Color,
    cardBg: Color,
    borderColor: Color,
    onViewOfferClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .width(280.dp)
            .clip(RoundedCornerShape(20.dp))
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        color = cardBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = CircleShape,
                        color = Color.LightGray
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = offer.clientName,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = offer.serviceName,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B),
                            maxLines = 1
                        )
                    }
                }

                Surface(
                    color = Color(0xFFFFECEC),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "-30% OFF",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFFEF4444)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Gs. 250.000",
                        fontSize = 10.sp,
                        color = Color(0xFF9CA3AF),
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall.copy(
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                        )
                    )
                    Text(
                        text = offer.budget,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryBlue
                    )
                }

                Button(
                    onClick = onViewOfferClick,
                    modifier = Modifier
                        .height(32.dp)
                        .padding(0.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue.copy(alpha = 0.1f)),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Text(
                        "Ver Oferta",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryBlue
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateBox(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.EventBusy,
                contentDescription = null,
                tint = Color(0xFFE2E8F0),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
        }
    }
}
