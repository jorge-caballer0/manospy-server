package com.example.manospy.ui.screens.client

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.manospy.ui.navigation.BottomNavScreen
import com.example.manospy.ui.navigation.Screen
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.ui.components.FilterCard
import com.example.manospy.ui.components.ServiceOfferCard
import com.example.manospy.ui.components.ServiceStatusCard
import com.example.manospy.ui.components.AppScaffold
import kotlinx.coroutines.launch

@Composable
fun NewReservationStep1Screen(
    navController: NavController,
    viewModel: ServiceViewModel? = null,
    initialService: String? = null,
    onBack: () -> Unit = { navController.popBackStack() },
    onNext: () -> Unit = { navController.navigate(Screen.NewReservationStep2.route) }
) {
    // Colores del tema
    val primaryBlue = Color(0xFF2563EB)
    val primaryDark = Color(0xFF003A94)
    val bgLight = Color(0xFFF8FAFC)
    val bgMesh = Color(0xFFF0F7FF)
    val textPrimary = Color(0xFF000000)
    val textSecondary = Color(0xFF64748B)
    val textGray = Color(0xFF94A3B8)
    val borderGray = Color(0xFFE2E8F0)
    val bgInput = Color(0xFFF1F5F9)
    val redError = Color(0xFFF87171)

    var selectedCategory by remember { mutableStateOf("") }
    var selectedCategoryLabel by remember { mutableStateOf("Selecciona un servicio") }
    var description by remember { mutableStateOf("") }
    var selectedPhotos by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var searchCategoryText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Opciones de categorías
    val categories = listOf(
        Pair("plumbing", "Plomería"),
        Pair("electricity", "Electricidad"),
        Pair("cleaning", "Limpieza"),
        Pair("carpentry", "Carpintería"),
        Pair("painting", "Pintura")
    )

    // Si viene un servicio inicial desde la navegación, pre-seleccionarlo
    LaunchedEffect(initialService) {
        initialService?.let { svc ->
            val found = categories.find { it.first == svc }
            if (found != null) {
                selectedCategory = found.first
                selectedCategoryLabel = found.second
            }
        }
    }

    // Image picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedPhotos = selectedPhotos + it
        }
    }

    AppScaffold(
        title = "Paso 1: Nuevo Servicio",
        onBackClick = onBack
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Main Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 16.dp)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                // Category Dropdown Card
                run {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInHorizontally(initialOffsetX = { -300 })
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, borderGray, RoundedCornerShape(20.dp)),
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(10.dp)),
                                        color = Color(0xFFDCE3FF)
                                    ) {
                                        Icon(
                                            Icons.Default.Handyman,
                                            contentDescription = null,
                                            tint = primaryBlue,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(8.dp)
                                        )
                                    }
                                    Text(
                                        "Categoría del servicio",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(bgInput, shape = RoundedCornerShape(16.dp))
                                        .border(1.dp, borderGray, RoundedCornerShape(16.dp))
                                        .clickable { showCategoryDropdown = !showCategoryDropdown }
                                        .padding(12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            selectedCategoryLabel,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = textPrimary
                                        )

                                        Icon(
                                            Icons.Default.ExpandMore,
                                            contentDescription = null,
                                            tint = textGray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }

                                // Dropdown Menu
                                if (showCategoryDropdown) {
                                    val filteredCategories = categories.filter {
                                        it.second.contains(searchCategoryText, ignoreCase = true)
                                    }
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.dp, borderGray, RoundedCornerShape(12.dp)),
                                        color = Color.White,
                                        shadowElevation = 4.dp
                                    ) {
                                        Column(
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            // Campo de búsqueda
                                            TextField(
                                                value = searchCategoryText,
                                                onValueChange = { searchCategoryText = it },
                                                placeholder = { Text("Buscar servicio...", fontSize = 13.sp) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(48.dp)
                                                    .padding(8.dp),
                                                singleLine = true,
                                                colors = TextFieldDefaults.colors(
                                                    focusedContainerColor = Color(0xFFF5F7FA),
                                                    unfocusedContainerColor = Color(0xFFF5F7FA),
                                                    focusedIndicatorColor = Color.Transparent,
                                                    unfocusedIndicatorColor = Color.Transparent
                                                )
                                            )
                                            HorizontalDivider(
                                                color = borderGray,
                                                thickness = 1.dp,
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                            // Listado filtrado
                                            filteredCategories.forEachIndexed { index, (id, label) ->
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            selectedCategory = id
                                                            selectedCategoryLabel = label
                                                            showCategoryDropdown = false
                                                            searchCategoryText = ""
                                                        }
                                                        .background(
                                                            if (selectedCategory == id) bgMesh else Color.White
                                                        )
                                                        .padding(16.dp),
                                                    contentAlignment = Alignment.CenterStart
                                                ) {
                                                    Text(
                                                        label,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = textPrimary
                                                    )
                                                }
                                                if (index < filteredCategories.size - 1) {
                                                    HorizontalDivider(
                                                        color = borderGray,
                                                        thickness = 1.dp,
                                                        modifier = Modifier.fillMaxWidth()
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Description Card
                run {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInHorizontally(initialOffsetX = { -300 })
                    ) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(1.dp, borderGray, RoundedCornerShape(16.dp)),
                                    color = Color.White,
                                    shadowElevation = 2.dp
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(0.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(10.dp)),
                                            color = Color(0xFFE5E7EB)
                                        ) {
                                            Icon(
                                                Icons.Default.Description,
                                                contentDescription = null,
                                                tint = Color(0xFF6B7280),
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(8.dp)
                                            )
                                        }
                                        Text(
                                            "Descripción del problema",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary,
                                            letterSpacing = 0.8.sp
                                        )
                                    }

                                            TextField(
                                                value = description,
                                                onValueChange = { description = it },
                                                placeholder = { Text("Describe detalladamente...", fontSize = 13.sp, color = textGray) },
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(140.dp)
                                                    .clip(RoundedCornerShape(12.dp)),
                                                colors = TextFieldDefaults.colors(
                                                    unfocusedContainerColor = bgInput,
                                                    focusedContainerColor = bgInput,
                                                    unfocusedIndicatorColor = Color.Transparent,
                                                    focusedIndicatorColor = Color.Transparent,
                                                    focusedTextColor = textPrimary,
                                                    unfocusedTextColor = textPrimary
                                                ),
                                                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                                                maxLines = 4
                                            )
                                }
                            }
                        }
                    }
                }

                // Photos Card
                run {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInHorizontally(initialOffsetX = { -300 })
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, borderGray, RoundedCornerShape(20.dp)),
                            color = Color.White,
                            shadowElevation = 2.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .size(40.dp)
                                                .clip(RoundedCornerShape(10.dp)),
                                            color = Color(0xFFE5E7EB)
                                        ) {
                                            Icon(
                                                Icons.Default.Handyman,
                                                contentDescription = null,
                                                tint = Color(0xFF6B7280),
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(8.dp)
                                            )
                                        }
                                        Text(
                                            "Fotos de referencia",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary,
                                            letterSpacing = 0.8.sp
                                        )
                                    }
                                    Text(
                                        "OPCIONAL",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryBlue,
                                        letterSpacing = 0.8.sp
                                    )
                                }

                                // Photos Gallery
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Add Photo Button
                                    Button(
                                        onClick = { photoPickerLauncher.launch("image/*") },
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .border(
                                                2.dp,
                                                borderGray,
                                                RoundedCornerShape(16.dp)
                                            ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = bgInput
                                        ),
                                        elevation = ButtonDefaults.buttonElevation(0.dp),
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Default.AddAPhoto,
                                                contentDescription = null,
                                                tint = textGray,
                                                modifier = Modifier.size(24.sp.value.dp)
                                            )
                                            Text(
                                                "SUBIR",
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = textGray,
                                                letterSpacing = 0.5.sp
                                            )
                                        }
                                    }

                                    // Photo Previews
                                    selectedPhotos.forEach { uri ->
                                        Box(
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .border(
                                                    2.dp,
                                                    Color.Transparent,
                                                    RoundedCornerShape(16.dp)
                                                )
                                                .background(Color(0xFFE5E7EB))
                                        ) {
                                            AsyncImage(
                                                model = uri,
                                                contentDescription = null,
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )

                                            IconButton(
                                                onClick = {
                                                    selectedPhotos = selectedPhotos.filter { it != uri }
                                                },
                                                modifier = Modifier
                                                    .align(Alignment.TopEnd)
                                                    .offset(x = 6.dp, y = (-6).dp)
                                                    .size(20.dp)
                                                    .background(redError, shape = CircleShape),
                                                colors = IconButtonDefaults.iconButtonColors(
                                                    contentColor = Color.White
                                                )
                                            ) {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Info Text
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = textGray,
                                        modifier = Modifier
                                            .size(14.sp.value.dp)
                                            .padding(top = 1.dp)
                                    )
                                    Text(
                                        "Las fotos ayudan al profesional a darte un presupuesto exacto.",
                                        fontSize = 9.sp,
                                        color = textGray,
                                        lineHeight = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Buttons Section - Integrated at the bottom of content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Next Button
                    Button(
                        onClick = {
                            if (selectedCategory.isNotBlank() && description.isNotBlank()) {
                                scope.launch {
                                    isLoading = true
                                    try {
                                        viewModel?.updateReservationStep1(
                                            category = selectedCategory,
                                            categoryLabel = selectedCategoryLabel,
                                            description = description,
                                            photoUris = selectedPhotos
                                        )
                                        onNext()
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryBlue,
                            disabledContainerColor = primaryBlue.copy(alpha = 0.45f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
                        enabled = !isLoading && selectedCategory.isNotBlank() && description.isNotBlank()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Siguiente",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Cancel Button (compact)
                    TextButton(
                        onClick = {
                            scope.launch {
                                navController.navigate(BottomNavScreen.ClientHome.route) {
                                    popUpTo(BottomNavScreen.ClientHome.route) { inclusive = true }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Cancelar reservación",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = textGray
                        )
                    }
                }
                }
            }
        }
    }
}
