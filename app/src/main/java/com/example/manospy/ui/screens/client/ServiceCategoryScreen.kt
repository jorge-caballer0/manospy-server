package com.example.manospy.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Construction
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.HomeRepairService
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.manospy.ui.navigation.BottomNavScreen
import com.example.manospy.ui.navigation.Screen
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.util.NetworkResult
import com.example.manospy.data.model.ServiceRequestStatus
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceCategoryScreen(
    navController: NavController,
    viewModel: ServiceViewModel? = null,
    modifier: Modifier = Modifier
) {
    val bgLight = Color(0xFFF6F7F8)
    val cardBg = Color.White
    val textPrimary = Color(0xFF0F1724)
    val textSecondary = Color(0xFF6B7280)

    var query by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var selectedCategoryKey by remember { mutableStateOf<String?>(null) }
    var shouldNavigateToPending by remember { mutableStateOf(false) }
    var pendingId by remember { mutableStateOf("") }
    
    // Monitor for selected category and check for pending requests
    LaunchedEffect(selectedCategoryKey) {
        if (selectedCategoryKey != null) {
            if (viewModel?.hasPendingRequest() == true) {
                val pending = (viewModel.pendingRequests.value as? NetworkResult.Success)?.data?.firstOrNull { it.status == com.example.manospy.data.model.ServiceRequestStatus.PENDING }
                if (pending != null) {
                    shouldNavigateToPending = true
                    pendingId = pending.id
                } else {
                    navController.navigate(Screen.NewReservationStep1.route + "/${selectedCategoryKey}")
                }
            } else {
                navController.navigate(Screen.NewReservationStep1.route + "/${selectedCategoryKey}")
            }
            selectedCategoryKey = null
        }
    }
    
    // Navigate to pending reservation if needed
    LaunchedEffect(shouldNavigateToPending, pendingId) {
        if (shouldNavigateToPending && pendingId.isNotEmpty()) {
            navController.navigate(Screen.ReservationPending.createRoute(pendingId))
            shouldNavigateToPending = false
        }
    }

    data class Category(val key: String, val label: String, val icon: ImageVector, val bg: Color, val tint: Color)

    val categories = remember {
        listOf(
            Category("plumbing","Plomería", Icons.Default.Plumbing, Color(0xFFDBEAFE), Color(0xFF3B82F6)),
            Category("electricity","Electricidad", Icons.Default.Bolt, Color(0xFFFEF3C7), Color(0xFFF59E0B)),
            Category("cleaning","Limpieza", Icons.Default.CleaningServices, Color(0xFFF0FDF4), Color(0xFF10B981)),
            Category("climate","Clima", Icons.Default.AcUnit, Color(0xFFF5F3FF), Color(0xFFA78BFA)),
            Category("carpentry","Carpintería", Icons.Default.Construction, Color(0xFFFFF7ED), Color(0xFFF97316)),
            Category("painting","Pintura", Icons.Default.FormatPaint, Color(0xFFFDF2F8), Color(0xFFFB7185)),
            Category("gardening","Jardinería", Icons.Default.LocalFlorist, Color(0xFFF0FDF4), Color(0xFF34D399)),
            Category("locksmith","Cerrajería", Icons.Default.Key, Color(0xFFF0F9FF), Color(0xFF6366F1)),
            Category("moving","Mudanza", Icons.Default.LocalShipping, Color(0xFFE0FCFF), Color(0xFF06B6D4)),
            Category("masonry","Albañilería", Icons.Default.HomeRepairService, Color(0xFFF8FAF9), Color(0xFF8B8B8B))
        )
    }

    val filtered = remember(query) { categories.filter { it.label.contains(query, ignoreCase = true) } }

    AppScaffold(
        title = "Todas las Categorías",
        onBackClick = { navController.popBackStack() }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(bgLight)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(bgLight)
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text(text = "¿Qué servicio buscas?", color = textSecondary) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = cardBg,
                        unfocusedContainerColor = cardBg,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = textSecondary) }
                )
            }

            // Grid de categorías
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                content = {
                    items(filtered) { cat ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .height(110.dp)
                                .clickable {
                                    selectedCategoryKey = cat.key
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = cardBg),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(cat.bg, shape = RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(cat.icon, contentDescription = cat.label, tint = cat.tint)
                                }
                                Text(text = cat.label, fontWeight = FontWeight.Bold, color = textPrimary)
                            }
                        }
                    }
                }
            )
        }
    }
}
