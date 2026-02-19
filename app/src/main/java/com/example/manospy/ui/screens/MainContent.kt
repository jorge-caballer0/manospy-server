package com.example.manospy.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.Edit
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.delay
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.asPaddingValues
import com.example.manospy.data.repository.AppRepository
import com.example.manospy.data.model.ReservationStatus
import com.example.manospy.data.model.ServiceRequestStatus
import com.example.manospy.util.NetworkResult
import com.example.manospy.ui.navigation.BottomNavScreen
import com.example.manospy.ui.navigation.Screen
import com.example.manospy.ui.screens.client.NewReservationStep1Screen
import com.example.manospy.ui.screens.client.NewReservationStep2Screen
import com.example.manospy.ui.screens.client.ServiceCategoryScreen
import com.example.manospy.ui.screens.ProfessionalOfferDetailScreen
import com.example.manospy.ui.screens.client.ReservationDetailScreenNew
import com.example.manospy.ui.screens.client.ReservationAcceptedScreen
import com.example.manospy.ui.screens.client.ReservationPendingScreen
import com.example.manospy.ui.screens.WaitingProfessionalScreen
import com.example.manospy.ui.screens.client.ServiceCompletedReviewScreen
import com.example.manospy.ui.viewmodel.MainViewModel
import com.example.manospy.ui.viewmodel.ServiceViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    mainViewModel: MainViewModel,
    repository: AppRepository,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val isClient = mainViewModel.isClient
    val currentUser by mainViewModel.currentUser.collectAsState()
    val serviceViewModel = viewModel { ServiceViewModel() }

    // Tabs dinámicos según rol
    val screens: List<BottomNavScreen> = if (isClient) {
        listOf(
            BottomNavScreen.ClientHome,
            BottomNavScreen.ClientSolicitar,
            BottomNavScreen.ClientReservas,
            BottomNavScreen.ClientChat,
            BottomNavScreen.ClientPerfil
        )
    } else {
        listOf(
            BottomNavScreen.ProfHome,
            BottomNavScreen.ProfSolicitudes,
            BottomNavScreen.ProfReservas,
            BottomNavScreen.ProfChat,
            BottomNavScreen.ProfPerfil
        )
    }

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        bottomBar = {
            val navBarInsets = WindowInsets.navigationBars.asPaddingValues()
            
            // Barra inferior personalizada
            if (isClient) {
                // Estado dinámico: si hay una reserva/solicitud activa el centro muestra Chat, si no muestra Solicitar
                val active = serviceViewModel.getActiveReservationOrRequest() != null
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Surface(
                    tonalElevation = 6.dp,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                        .padding(bottom = navBarInsets.calculateBottomPadding())
                ) {
                    // Sombra interna superior sutil (degradado) para separar contenido blanco
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.06f), Color.Transparent)
                                )
                            )
                    )
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Inicio - Mejorado visualmente
                        Column(
                            modifier = Modifier
                                .width(68.dp)
                                .clickable {
                                    navController.navigate(BottomNavScreen.ClientHome.route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val selectedHome = currentRoute == BottomNavScreen.ClientHome.route
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedHome) Color(0xFF2563EB).copy(alpha = 0.12f) else Color.Transparent,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.Explore,
                                        contentDescription = null,
                                        tint = if (selectedHome) Color(0xFF2563EB) else Color(0xFF64748B),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                BottomNavScreen.ClientHome.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Medium),
                                color = if (selectedHome) Color(0xFF0D47A1) else Color(0xFF64748B)
                            )
                        }

                        val hasHistoryBadge = serviceViewModel.hasPendingRequest()
                        Box(contentAlignment = Alignment.TopEnd) {
                            Column(
                                modifier = Modifier
                                    .width(68.dp)
                                    .clickable {
                                        navController.navigate(BottomNavScreen.ClientReservas.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val selectedHist = currentRoute == BottomNavScreen.ClientReservas.route
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedHist) Color(0xFF2563EB).copy(alpha = 0.12f) else Color.Transparent,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Outlined.CalendarToday,
                                            contentDescription = null,
                                            tint = if (selectedHist) Color(0xFF2563EB) else Color(0xFF64748B),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    BottomNavScreen.ClientReservas.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Medium),
                                    color = if (selectedHist) Color(0xFF0D47A1) else Color(0xFF64748B)
                                )
                            }
                            if (hasHistoryBadge) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFFDC2626),
                                    modifier = Modifier.offset(x = 8.dp, y = (-4).dp)
                                ) {
                                    Text(
                                        "!",
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp)
                                    )
                                }
                            }
                        }

                        // Botón central dinámico - Minimizado para mejor responsividad
                        val primary = Color(0xFF2563EB)
                        val pulse = rememberInfiniteTransition()
                        val scale by pulse.animateFloat(
                            initialValue = 1f,
                            targetValue = 1.05f,
                            animationSpec = infiniteRepeatable(animation = tween(800), repeatMode = RepeatMode.Reverse)
                        )

                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(64.dp)) {
                            if (!active) {
                                // Centro: (+) para Solicitar
                                FloatingActionButton(
                                    onClick = { navController.navigate(BottomNavScreen.ClientSolicitar.route) },
                                    containerColor = primary,
                                    modifier = Modifier.scale(scale),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        Icons.Filled.Add,
                                        contentDescription = "Solicitar",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            } else {
                                // Centro: Chat destacado
                                FloatingActionButton(
                                    onClick = { navController.navigate(BottomNavScreen.ClientChat.route) },
                                    containerColor = primary,
                                    modifier = Modifier.scale(scale),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Icon(
                                        Icons.Outlined.Chat,
                                        contentDescription = "Chat",
                                        tint = Color.White,
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }

                        // Posición derecha dinámica según estado
                        if (!active) {
                            // Sin solicitud activa: Chat en posición normal (derecha)
                            Column(
                                modifier = Modifier
                                    .width(68.dp)
                                    .clickable {
                                        navController.navigate(BottomNavScreen.ClientChat.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val selectedChat = currentRoute == BottomNavScreen.ClientChat.route
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (selectedChat) Color(0xFF137FEC).copy(alpha = 0.1f) else Color.Transparent,
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Outlined.Chat,
                                            contentDescription = null,
                                            tint = if (selectedChat) Color(0xFF0D47A1) else Color(0xFF64748B),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    BottomNavScreen.ClientChat.label,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Medium),
                                    color = if (selectedChat) Color(0xFF0D47A1) else Color(0xFF64748B)
                                )
                            }
                        } else {
                            // Con solicitud activa: (+) en posición normal (derecha)
                            Column(
                                modifier = Modifier
                                    .width(68.dp)
                                    .clickable { 
                                        navController.navigate(BottomNavScreen.ClientSolicitar.route) { 
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true 
                                        } 
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = if (currentRoute == BottomNavScreen.ClientSolicitar.route) Color(0xFF0D47A1) else Color(0xFF64748B),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Solicitar",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Medium),
                                    color = if (currentRoute == BottomNavScreen.ClientSolicitar.route) Color(0xFF0D47A1) else Color(0xFF64748B)
                                )
                            }
                        }

                        // Perfil / Ajustes (siempre al final) - Mejorado visualmente
                        Column(
                            modifier = Modifier
                                .width(68.dp)
                                .clickable {
                                    navController.navigate(BottomNavScreen.ClientPerfil.route) {
                                        popUpTo(navController.graph.startDestinationId)
                                        launchSingleTop = true
                                    }
                                },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val selectedProfile = currentRoute == BottomNavScreen.ClientPerfil.route
                                Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (selectedProfile) Color(0xFF2563EB).copy(alpha = 0.12f) else Color.Transparent,
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Outlined.Person,
                                        contentDescription = null,
                                        tint = if (selectedProfile) Color(0xFF2563EB) else Color(0xFF64748B),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                BottomNavScreen.ClientPerfil.label,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Medium),
                                color = if (selectedProfile) Color(0xFF0D47A1) else Color(0xFF64748B)
                            )
                        }
                    }
                }
            } else {
                // Profesional: usar barra estándar mejorada
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .padding(bottom = navBarInsets.calculateBottomPadding())
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route
                    screens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(getIconForScreen(screen), contentDescription = null, modifier = Modifier.size(24.dp)) },
                            label = { Text(screen.label, style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)) },
                            selected = currentRoute == screen.route,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFF0D47A1),
                                selectedTextColor = Color(0xFF0D47A1),
                                unselectedIconColor = Color(0xFF64748B),
                                unselectedTextColor = Color(0xFF64748B),
                                indicatorColor = Color(0xFF137FEC).copy(alpha = 0.1f)
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        val enterAnimation = fadeIn(
            animationSpec = tween(300, delayMillis = 0)
        ) + slideInHorizontally(
            animationSpec = tween(300),
            initialOffsetX = { 500 }
        )
        
        val exitAnimation = fadeOut(
            animationSpec = tween(300)
        ) + slideOutHorizontally(
            animationSpec = tween(300),
            targetOffsetX = { -500 }
        )
        
        NavHost(
            navController = navController,
            startDestination = screens.first().route,
            modifier = Modifier.padding(innerPadding),
            enterTransition = { enterAnimation },
            exitTransition = { exitAnimation },
            popEnterTransition = { enterAnimation },
            popExitTransition = { exitAnimation }
        ) {
            // Cliente
            composable(
                BottomNavScreen.ClientHome.route,
                enterTransition = { enterAnimation },
                exitTransition = { exitAnimation }
            ) { 
                ClientHomeScreen(
                    navController = navController,
                    currentUser = currentUser,
                    serviceViewModel = serviceViewModel,
                    onNavigateToCreateRequest = {},
                    onNavigateToService = { category ->
                        // Verificar si hay solicitudes pendientes antes de navegar a nueva reserva
                        if (serviceViewModel.pendingRequests.value !is NetworkResult.Success) {
                            serviceViewModel.fetchServiceRequests()
                        }
                        if (serviceViewModel.hasPendingRequest()) {
                            val pending = (serviceViewModel.pendingRequests.value as? NetworkResult.Success)?.data?.firstOrNull { it.status == ServiceRequestStatus.PENDING }
                            if (pending != null) {
                                navController.navigate(Screen.ReservationPending.createRoute(pending.id))
                            } else {
                                navController.navigate(BottomNavScreen.ClientSolicitar.route)
                            }
                        } else {
                            navController.navigate(BottomNavScreen.ClientSolicitar.route)
                        }
                    }
                )
            }
            composable(BottomNavScreen.ClientSolicitar.route) {
                // Verificar si hay solicitudes pendientes antes de crear nueva
                val shouldNavigateToPending = remember { mutableStateOf(false) }
                val pendingId = remember { mutableStateOf("") }
                
                LaunchedEffect(Unit) {
                    // Siempre refrescar las solicitudes para asegurar que tenemos datos actuales
                    serviceViewModel.fetchServiceRequests()
                }
                
                val pendingRequests by serviceViewModel.pendingRequests.collectAsState()
                
                LaunchedEffect(pendingRequests) {
                    if (pendingRequests is NetworkResult.Success) {
                        val pending = (pendingRequests as? NetworkResult.Success)?.data?.firstOrNull { it.status == ServiceRequestStatus.PENDING }
                        if (pending != null) {
                            shouldNavigateToPending.value = true
                            pendingId.value = pending.id
                        }
                    }
                }
                
                if (shouldNavigateToPending.value && pendingId.value.isNotEmpty()) {
                    LaunchedEffect(pendingId.value) {
                        navController.navigate(Screen.ReservationPending.createRoute(pendingId.value)) {
                            popUpTo(BottomNavScreen.ClientSolicitar.route) { inclusive = true }
                        }
                    }
                }
                
                NewReservationStep1Screen(navController = navController, viewModel = serviceViewModel)
            }
            composable(Screen.NewReservationStep2.route) { NewReservationStep2Screen(navController = navController, viewModel = serviceViewModel) }
            composable(Screen.ServiceCategories.route) { ServiceCategoryScreen(navController = navController) }
            composable(
                Screen.ReservationAccepted.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                ReservationAcceptedScreen(navController = navController, reservationId = id, viewModel = serviceViewModel)
            }
            composable(
                Screen.ReservationDetail.route,
                arguments = listOf(
                    navArgument("id") { type = NavType.StringType },
                    navArgument("pending") { type = NavType.BoolType; defaultValue = false }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                val isPending = backStackEntry.arguments?.getBoolean("pending") ?: false
                ReservationDetailScreenNew(navController = navController, reservationId = id, svcViewModel = serviceViewModel, isPending = isPending)
            }
            composable(
                Screen.ReservationPending.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                ReservationPendingScreen(
                    navController = navController,
                    reservationId = id,
                    viewModel = serviceViewModel
                )
            }
            composable(
                Screen.WaitingProfessional.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                WaitingProfessionalScreen(
                    reservationId = id,
                    viewModel = serviceViewModel,
                    onProfessionalFound = {
                        navController.navigate(Screen.ReservationAccepted.createRoute(id)) {
                            popUpTo(Screen.WaitingProfessional.route) { inclusive = true }
                        }
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(
                Screen.ServiceCompletedReview.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                ServiceCompletedReviewScreen(navController = navController, reservationId = id)
            }
            composable(Screen.ProfessionalOffers.route) { ProfessionalOffersScreen(navController = navController, serviceViewModel = serviceViewModel) }
            composable(Screen.ProfessionalOfferDetail.route) {
                ProfessionalOfferDetailScreen(navController = navController, viewModel = serviceViewModel)
            }
            composable(BottomNavScreen.ClientReservas.route) { com.example.manospy.ui.screens.client.ClientHistoryScreen(navController, viewModel = serviceViewModel, currentUser = currentUser) }
            composable(BottomNavScreen.ClientChat.route) { ChatListScreen(isClient = true, navController = navController, serviceViewModel = serviceViewModel) }
            composable(BottomNavScreen.ClientPerfil.route) { 
                PersonalInformationScreen(
                    user = currentUser, 
                    onBack = {
                        serviceViewModel.clearAllData()
                        onLogout()
                    },
                    navController = navController,
                    viewModel = serviceViewModel
                ) 
            }

            // Profesional
            composable(BottomNavScreen.ProfHome.route) { 
                ProfessionalHomeScreen(
                    navController = navController,
                    userName = currentUser?.name ?: "Profesional",
                    userRating = currentUser?.rating ?: 0.0,
                    totalIncome = "Gs. ${currentUser?.totalRequests?.times(50000) ?: 0}",
                    bookings = currentUser?.completedServices ?: 0
                ) 
            }
            composable(BottomNavScreen.ProfSolicitudes.route) { ProfessionalRequestsScreen(navController = navController) }
            composable(BottomNavScreen.ProfReservas.route) { ProfessionalBookingsScreen() }
            composable(BottomNavScreen.ProfChat.route) { ChatListScreen(isClient = false, navController = navController, serviceViewModel = serviceViewModel) }
            composable(BottomNavScreen.ProfPerfil.route) { ProfessionalProfileScreen() }

            // Extras
            composable("chat/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id") ?: ""
                ChatScreen(navController = navController, reservationIdOrChatId = id)
            }
            composable("editProfile") {
                EditProfileScreen(
                    user = currentUser,
                    repository = repository,
                    onBack = { navController.popBackStack() },
                    onSave = { name, email, phoneNumber ->
                        // Aquí se guardarían los cambios al backend
                        // mainViewModel.updateProfile(name, email, phoneNumber)
                    }
                )
            }
            composable("accountSettings") {
                AccountSettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
            composable("support") { SupportScreen(navController = navController) }
            
            // Chat de soporte
            composable("chatSupport") {
                ChatSupportScreen(
                    onBack = { navController.popBackStack() },
                    navController = navController
                )
            }
            
            // Calificación de soporte
            composable("supportRating") {
                SupportRatingScreen(
                    agentName = "Agente de Soporte",
                    onSend = { rating, tags, comment ->
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun getIconForScreen(screen: BottomNavScreen) = when (screen) {
    // Cliente
    BottomNavScreen.ClientHome -> Icons.Outlined.Explore
    BottomNavScreen.ClientSolicitar -> Icons.Outlined.Explore
    BottomNavScreen.ClientReservas -> Icons.Outlined.CalendarToday
    BottomNavScreen.ClientChat -> Icons.Outlined.Chat
    BottomNavScreen.ClientPerfil -> Icons.Outlined.Person

    // Profesional
    BottomNavScreen.ProfHome -> Icons.Outlined.Explore
    BottomNavScreen.ProfSolicitudes -> Icons.Outlined.Explore
    BottomNavScreen.ProfReservas -> Icons.Outlined.CalendarToday
    BottomNavScreen.ProfChat -> Icons.Outlined.Chat
    BottomNavScreen.ProfPerfil -> Icons.Outlined.Person
}
