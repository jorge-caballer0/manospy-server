package com.example.manospy.ui.screens.client

import android.location.Geocoder
import android.location.LocationManager
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import com.google.android.gms.maps.CameraUpdateFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.LocationServices
import android.content.pm.PackageManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.compose.material3.LocalTextStyle
import com.example.manospy.data.local.SessionManager
import com.example.manospy.ui.navigation.Screen
import com.example.manospy.ui.navigation.BottomNavScreen
import com.example.manospy.ui.screens.MyAddressesScreen
import com.example.manospy.ui.screens.AddAddressScreen
import com.example.manospy.ui.screens.Address
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.ui.components.FilterCard
import com.example.manospy.ui.components.ServiceOfferCard
import com.example.manospy.ui.components.ServiceStatusCard
import com.example.manospy.ui.components.UnifiedScreenHeader
import com.example.manospy.ui.components.SyncStatusBarWithHeader
import com.example.manospy.ui.components.AppScaffold
// UnifiedScreenHeader import removed; AppScaffold used
import com.example.manospy.data.model.ServiceRequest
import com.example.manospy.util.NetworkResult
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.mutableStateListOf
import java.util.*
import androidx.compose.material.icons.filled.MyLocation
import kotlin.math.abs
import com.example.manospy.ui.components.LocationSearchField
import com.example.manospy.ui.components.AppTopBar
import com.example.manospy.ui.theme.AppColors
import com.example.manospy.ui.theme.AppDimensions
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Helper function to convert coordinates to address using Geocoder
fun getAddressFromCoordinates(context: android.content.Context, latitude: Double, longitude: Double): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val streetAddress = if (!address.thoroughfare.isNullOrEmpty()) address.thoroughfare else ""
            val neighborhood = if (!address.subLocality.isNullOrEmpty()) address.subLocality else ""
            val city = if (!address.locality.isNullOrEmpty()) address.locality else ""
            
            listOfNotNull(streetAddress, neighborhood, city)
                .filter { it.isNotEmpty() }
                .joinToString(", ")
                .takeIf { it.isNotEmpty() } ?: "Ubicación seleccionada"
        } else {
            "Ubicación seleccionada"
        }
    } catch (e: Exception) {
        "Ubicación seleccionada"
    }
}

// Helper function to get current location using LocationManager
fun getCurrentLocation(context: android.content.Context): Pair<Double, Double>? {
    return try {
        val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
        val providers = locationManager.allProviders
        
        // Try GPS first, then NETWORK, then PASSIVE
        for (provider in listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER)) {
            if (provider in providers) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    val location = locationManager.getLastKnownLocation(provider)
                    if (location != null && (location.latitude != 0.0 || location.longitude != 0.0)) {
                        return Pair(location.latitude, location.longitude)
                    }
                }
            }
        }
        null
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewReservationStep2Screen(
    navController: NavController,
    viewModel: ServiceViewModel? = null,
    onBack: () -> Unit = { navController.popBackStack() }
) {
    // Colores del tema
    val primaryBlue = AppColors.PrimaryBlue
    val bgLight = AppColors.BgLight
    val textPrimary = AppColors.TextPrimary
    val textSecondary = AppColors.TextSecondary
    val textGray = AppColors.TextTertiary
    val borderGray = AppColors.BorderGray
    val bgInput = AppColors.BgWhite

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val scope = rememberCoroutineScope()

    // Observar cambios en el estado de creación de solicitud (si el viewModel existe)
    var createdRequestId by remember { mutableStateOf<String?>(null) }
    
    if (viewModel != null) {
        val createRequestStatus by viewModel.createRequestStatus.collectAsState()
        
        // Navegar cuando se cree exitosamente la solicitud
        LaunchedEffect(createRequestStatus) {
            if (createRequestStatus is NetworkResult.Success) {
                val request = (createRequestStatus as NetworkResult.Success<ServiceRequest>).data
                createdRequestId = request.id
            }
        }
    }
    
    // Navegar cuando se establece el ID
    LaunchedEffect(createdRequestId) {
        createdRequestId?.let { id ->
            navController.navigate(Screen.ReservationPending.createRoute(id))
        }
    }

    // State variables
    var location by remember { mutableStateOf("") }
    var selectedLocationDisplay by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var showLocationSuggestions by remember { mutableStateOf(false) }
    var selectedLatitude by remember { mutableDoubleStateOf(-25.2637) } // Asunción, Paraguay
    var selectedLongitude by remember { mutableDoubleStateOf(-57.5759) }
    var urgencyLevel by remember { mutableStateOf("normal") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    var additionalNotes by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showAddressesDialog by remember { mutableStateOf(false) }
    var showAddNewAddressDialog by remember { mutableStateOf(false) }
    
    // Addresses list - Same structure as MyAddressesScreen Address
    data class SavedAddress(
        val id: String = "",
        val label: String = "",
        val fullAddress: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )
    
    var savedAddresses by remember {
        mutableStateOf(
            listOf(
                SavedAddress(
                    id = "1",
                    label = "Casa",
                    fullAddress = "Avenida Mariscal López 1234, Edificio Plaza, Apto 402. Asunción, Paraguay.",
                    latitude = -25.2637,
                    longitude = -57.5759
                ),
                SavedAddress(
                    id = "2",
                    label = "Trabajo",
                    fullAddress = "Paseo La Galería, Torre 2, Piso 15. Santa Teresa. Asunción, Paraguay.",
                    latitude = -25.2890,
                    longitude = -57.5732
                ),
                SavedAddress(
                    id = "3",
                    label = "Casa de Mamá",
                    fullAddress = "Barrio Las Lomas, Calle Isaac Kostianovsky 455. Luque.",
                    latitude = -25.3064,
                    longitude = -57.5015
                )
            )
        )
    }
    
    // Fused location client and permission launcher
    val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            try {
                fusedClient.lastLocation.addOnSuccessListener { loc ->
                    loc?.let {
                        selectedLatitude = it.latitude
                        selectedLongitude = it.longitude
                        // Store current location as the selected display so it will be used for submission
                        selectedLocationDisplay = getAddressFromCoordinates(context, selectedLatitude, selectedLongitude)
                    }
                }
            } catch (e: Exception) {
                // fallback to previous method
                val currentLocation = getCurrentLocation(context)
                if (currentLocation != null) {
                    selectedLatitude = currentLocation.first
                    selectedLongitude = currentLocation.second
                    selectedLocationDisplay = getAddressFromCoordinates(context, selectedLatitude, selectedLongitude)
                }
            }
        }
    }

    // Cuando la pantalla abre, intentamos detectar la ubicación una vez para centrar el mapa
    // COMENTADO: El usuario prefiere que no se auto-cargue la ubicación actual
    /*
    LaunchedEffect(Unit) {
        try {
            val permission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            if (permission) {
                val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                fusedLocationClient.lastLocation.addOnSuccessListener { it ->
                    if (it != null) {
                        selectedLatitude = it.latitude
                        selectedLongitude = it.longitude
                        location = getAddressFromCoordinates(context, selectedLatitude, selectedLongitude)
                    }
                }
            }
        } catch (e: Exception) {
            val currentLocation = getCurrentLocation(context)
            if (currentLocation != null) {
                selectedLatitude = currentLocation.first
                selectedLongitude = currentLocation.second
                location = getAddressFromCoordinates(context, selectedLatitude, selectedLongitude)
            }
        }
    }
    */

    // Location suggestions (autocomplete via Geocoder)
    val geoSuggestions = remember { mutableStateListOf<Pair<String, LatLng>>() }
    // Camera state para el mapa (declarado temprano para uso en sugerencias)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(selectedLatitude, selectedLongitude), 14f)
    }

    // Get Step1 data from ViewModel (safe when viewModel is null)
    val step1Map = viewModel?.reservationStep1Data?.collectAsState(initial = emptyMap())?.value ?: emptyMap()
    val category = step1Map["category"] as? String ?: ""
    val categoryLabel = step1Map["categoryLabel"] as? String ?: ""
    val description = step1Map["description"] as? String ?: ""

    // Date and Time Picker callbacks removed - using Compose Material3 pickers instead

    AppScaffold(
        title = "Paso 2: Agregar Detalles",
        onBackClick = onBack
    ) { paddingValues ->
        // Sincronizar status bar con el color azul de la cabecera
        SyncStatusBarWithHeader(headerColor = Color(0xFF0D47A1))
        
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryBlue)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(bgLight)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Search Location Field
                    item {
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
                                    Text(
                                        "Buscar ubicaci\u00f3n",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        letterSpacing = 0.8.sp
                                    )

                                    LocationSearchField(
                                        value = searchQuery,
                                        onValueChange = { searchQuery = it },
                                        onSuggestionSelected = { suggestion ->
                                            // Cuando el usuario selecciona una sugerencia, actualizamos la ubicación seleccionada
                                            selectedLocationDisplay = getAddressFromCoordinates(context, suggestion.latitude, suggestion.longitude)
                                            selectedLatitude = suggestion.latitude
                                            selectedLongitude = suggestion.longitude
                                            searchQuery = "" // limpiar el buscador para nueva búsqueda
                                            showLocationSuggestions = false
                                            scope.launch {
                                                cameraPositionState.animate(
                                                    CameraUpdateFactory.newLatLngZoom(
                                                        LatLng(suggestion.latitude, suggestion.longitude),
                                                        16f
                                                    )
                                                )
                                            }
                                        },
                                        placeholder = "Busca tu ubicación...",
                                        showSuggestions = true
                                    )
                                }
                            }
                        }
                    }

                    // Dynamic Map
                    item {
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
                                    Text(
                                        "Mapa din\u00e1mico",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        letterSpacing = 0.8.sp
                                    )

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(250.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.dp, borderGray, RoundedCornerShape(12.dp))
                                    ) {
                                        GoogleMap(
                                            modifier = Modifier.fillMaxSize(),
                                            cameraPositionState = cameraPositionState,
                                            onMapClick = { latLng ->
                                                selectedLatitude = latLng.latitude
                                                selectedLongitude = latLng.longitude
                                                // Al marcar en el mapa actualizamos la ubicación seleccionada y limpiamos el buscador
                                                selectedLocationDisplay = getAddressFromCoordinates(context, selectedLatitude, selectedLongitude)
                                                searchQuery = ""
                                                scope.launch {
                                                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                                                }
                                            }
                                        ) {
                                            Marker(state = MarkerState(position = LatLng(selectedLatitude, selectedLongitude)))
                                        }

                                        IconButton(
                                            onClick = {
                                                if (ContextCompat.checkSelfPermission(
                                                        context,
                                                        Manifest.permission.ACCESS_FINE_LOCATION
                                                    ) == PackageManager.PERMISSION_GRANTED
                                                ) {
                                                    val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager
                                                    val providers = locationManager.allProviders
                                                    var currentLocation: Pair<Double, Double>? = null
                                                    for (provider in providers) {
                                                        try {
                                                            val location = locationManager.getLastKnownLocation(provider)
                                                            if (location != null) {
                                                                currentLocation = Pair(location.latitude, location.longitude)
                                                                break
                                                            }
                                                        } catch (e: Exception) {}
                                                    }
                                                    if (currentLocation != null) {
                                                        selectedLatitude = currentLocation.first
                                                        selectedLongitude = currentLocation.second
                                                        // Use reverse geocoded address as the selected display (used for submission)
                                                        selectedLocationDisplay = getAddressFromCoordinates(context, selectedLatitude, selectedLongitude)
                                                        scope.launch {
                                                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(selectedLatitude, selectedLongitude), 16f))
                                                        }
                                                    }
                                                } else {
                                                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                                }
                                            },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(12.dp)
                                                .size(42.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color.White)
                                                .border(1.5.dp, primaryBlue, RoundedCornerShape(10.dp))
                                        ) {
                                            Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicaci\u00f3n", tint = primaryBlue, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // My Addresses
                    item {
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
                                    Text(
                                        "Mis direcciones",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        letterSpacing = 0.8.sp
                                    )

                                    LazyRow(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(min = 50.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        contentPadding = PaddingValues(horizontal = 0.dp)
                                    ) {
                                        items(savedAddresses) { address ->
                                            Surface(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .clickable {
                                                        selectedLocationDisplay = address.fullAddress
                                                        selectedLatitude = address.latitude
                                                        selectedLongitude = address.longitude
                                                        scope.launch {
                                                            cameraPositionState.animate(
                                                                CameraUpdateFactory.newLatLngZoom(
                                                                    LatLng(address.latitude, address.longitude),
                                                                    16f
                                                                )
                                                            )
                                                        }
                                                    },
                                                color = if (selectedLocationDisplay == address.fullAddress) Color(0xFFDCE3FF) else Color(0xFFF5F7FA),
                                                border = BorderStroke(
                                                    1.dp,
                                                    Color(0xFFE2E8F0)
                                                ),
                                                shadowElevation = 0.dp
                                            ) {
                                                Text(
                                                    address.label,
                                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = if (selectedLocationDisplay == address.fullAddress) primaryBlue else textPrimary
                                                )
                                            }
                                        }

                                        item {
                                            Surface(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .clickable { showAddNewAddressDialog = true },
                                                color = Color(0xFFF5F7FA),
                                                border = BorderStroke(1.5.dp, Color(0xFFE2E8F0))
                                            ) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.Add, contentDescription = null, tint = primaryBlue, modifier = Modifier.size(16.dp))
                                                    Text(
                                                        "NUEVO",
                                                        fontSize = 12.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = primaryBlue
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Selected Location Display - Always visible
                    item {
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
                                    Text(
                                        "Ubicación del servicio",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary,
                                        letterSpacing = 0.8.sp
                                    )

                                    // Mostrar ubicación seleccionada (no editable)
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp)),
                                        color = Color(0xFFF5F7FA),
                                        border = BorderStroke(0.dp, Color.Transparent)
                                    ) {
                                        Text(
                                            selectedLocationDisplay.ifEmpty { "Selecciona una ubicación" },
                                            modifier = Modifier.padding(14.dp),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (selectedLocationDisplay.isNotEmpty()) Color(0xFF1F2937) else Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Urgency Level
                    item {
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
                                            color = Color(0xFFE5E7EB)
                                        ) {
                                            Icon(
                                                Icons.Default.Bolt,
                                                contentDescription = null,
                                                tint = Color(0xFF6B7280),
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(8.dp)
                                            )
                                        }
                                        Text(
                                            "Nivel de urgencia",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary,
                                            letterSpacing = 0.8.sp
                                        )
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        listOf("normal" to "Normal", "urgente" to "Urgente").forEach { (value, label) ->
                                            Surface(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .clickable { urgencyLevel = value },
                                                color = if (urgencyLevel == value) primaryBlue else Color(0xFFF5F7FA),
                                                border = BorderStroke(1.5.dp, if (urgencyLevel == value) primaryBlue else Color(0xFFE2E8F0)),
                                                shadowElevation = if (urgencyLevel == value) 3.dp else 0.dp
                                            ) {
                                                Text(
                                                    label,
                                                    modifier = Modifier.padding(12.dp),
                                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                                    color = if (urgencyLevel == value) Color.White else textPrimary,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Date and Time
                    item {
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
                                            color = Color(0xFFE5E7EB)
                                        ) {
                                            Icon(
                                                Icons.Default.Schedule,
                                                contentDescription = null,
                                                tint = Color(0xFF6B7280),
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(8.dp)
                                            )
                                        }
                                        Text(
                                            "Horario",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary,
                                            letterSpacing = 0.8.sp
                                        )
                                    }
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { showDatePicker = true },
                                            color = if (selectedDate.isNotEmpty()) Color(0xFFDCE3FF) else Color(0xFFF5F7FA),
                                            border = BorderStroke(1.dp, if (selectedDate.isNotEmpty()) primaryBlue else Color(0xFFE2E8F0)),
                                            shadowElevation = 1.dp
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text("Fecha", style = MaterialTheme.typography.labelSmall, color = if (selectedDate.isNotEmpty()) primaryBlue else Color(0xFF94A3B8), fontSize = 11.sp)
                                                Text(selectedDate.ifEmpty { "Seleccionar" }, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = if (selectedDate.isNotEmpty()) primaryBlue else textPrimary, fontSize = 13.sp)
                                            }
                                        }
                                        Surface(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(10.dp))
                                                .clickable { showTimePicker = true },
                                            color = if (selectedTime.isNotEmpty()) Color(0xFFDCE3FF) else Color(0xFFF5F7FA),
                                            border = BorderStroke(1.dp, if (selectedTime.isNotEmpty()) primaryBlue else Color(0xFFE2E8F0)),
                                            shadowElevation = 1.dp
                                        ) {
                                            Column(modifier = Modifier.padding(12.dp)) {
                                                Text("Hora", style = MaterialTheme.typography.labelSmall, color = if (selectedTime.isNotEmpty()) primaryBlue else Color(0xFF94A3B8), fontSize = 11.sp)
                                                Text(selectedTime.ifEmpty { "Seleccionar" }, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = if (selectedTime.isNotEmpty()) primaryBlue else textPrimary, fontSize = 13.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Additional Details
                    item {
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
                                            "Detalles adicionales",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary,
                                            letterSpacing = 0.8.sp
                                        )
                                    }
                                    TextField(
                                        value = additionalNotes,
                                        onValueChange = { additionalNotes = it },
                                        placeholder = { Text("Cuéntanos más sobre el servicio requerido...", color = textGray, fontSize = 14.sp) },
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
                                        textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                        maxLines = 4
                                    )
                                }
                            }
                        }
                    }

                    // Buttons Section - Inside LazyColumn
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 0.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Confirm Button
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp)),
                                color = primaryBlue,
                                shadowElevation = 4.dp
                            ) {
                                        Button(
                                    onClick = {
                                        val hasLocation = selectedLocationDisplay.isNotEmpty() || location.isNotEmpty()
                                        if (hasLocation && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()) {
                                            isLoading = true
                                            if (viewModel != null) {
                                                scope.launch {
                                                    val clientId = sessionManager.getUserId() ?: ""
                                                    val locationToSend = if (selectedLocationDisplay.isNotEmpty()) selectedLocationDisplay else location
                                                    viewModel.createRequest(
                                                        clientId = clientId,
                                                        category = category,
                                                        categoryLabel = categoryLabel,
                                                        description = description + if (additionalNotes.isNotEmpty()) "\n\nNotas: $additionalNotes" else "",
                                                        location = locationToSend,
                                                        latitude = selectedLatitude,
                                                        longitude = selectedLongitude,
                                                        preferredDate = selectedDate
                                                    )
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
                                    enabled = !isLoading && (selectedLocationDisplay.isNotEmpty() || location.isNotEmpty()) && selectedDate.isNotEmpty() && selectedTime.isNotEmpty()
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            "Confirmar Solicitud",
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

    // Add new address dialog
    if (showAddNewAddressDialog) {
        Dialog(
            onDismissRequest = { showAddNewAddressDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .heightIn(max = 600.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color.White
            ) {
                AddAddressScreen(
                    editingAddress = null,
                    onSave = { label, address, reference ->
                        savedAddresses = savedAddresses + SavedAddress(
                            id = System.currentTimeMillis().toString(),
                            label = label,
                            fullAddress = address,
                            latitude = selectedLatitude,
                            longitude = selectedLongitude
                        )
                        // Mark new address as the selected service location and recenter the map
                        selectedLocationDisplay = address
                        selectedLatitude = selectedLatitude
                        selectedLongitude = selectedLongitude
                        scope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(selectedLatitude, selectedLongitude), 16f))
                        }
                        showAddNewAddressDialog = false
                    },
                    onBack = { showAddNewAddressDialog = false },
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        androidx.compose.ui.window.Dialog(
            onDismissRequest = { showDatePicker = false },
            properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Seleccionar Fecha",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = Color(0xFF1F2937)
                        )
                        IconButton(onClick = { showDatePicker = false }, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = Color(0xFF64748B))
                        }
                    }

                    Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), color = Color(0xFFE5E7EB))

                    DatePicker(
                        state = datePickerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = DatePickerDefaults.colors(
                            containerColor = Color.White,
                            selectedDayContainerColor = primaryBlue,
                            selectedDayContentColor = Color.White,
                            selectedYearContainerColor = primaryBlue.copy(alpha = 0.2f),
                            selectedYearContentColor = primaryBlue,
                            todayContentColor = primaryBlue,
                            todayDateBorderColor = primaryBlue,
                            dayInSelectionRangeContainerColor = primaryBlue.copy(alpha = 0.15f),
                            dayInSelectionRangeContentColor = Color(0xFF0F1724),
                            dayContentColor = Color(0xFF0F1724),
                            disabledDayContentColor = Color(0xFFC4C7C5),
                            disabledSelectedDayContainerColor = primaryBlue.copy(alpha = 0.3f),
                            dividerColor = Color(0xFFE5E7EB),
                            yearContentColor = Color(0xFF0F1724),
                            disabledYearContentColor = Color(0xFFC4C7C5)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showDatePicker = false },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancelar", color = primaryBlue, fontWeight = FontWeight.SemiBold)
                        }
                        Button(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val cal = Calendar.getInstance().apply { timeInMillis = millis }
                                    selectedDate = "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH) + 1}/${cal.get(Calendar.YEAR)}"
                                }
                                showDatePicker = false
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Seleccionar Hora", color = Color(0xFF1F2937)) },
            text = {
                TimePicker(
                    state = timePickerState,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFF137FEC),
                        clockDialSelectedContentColor = Color.White,
                        selectorColor = Color(0xFF137FEC),
                        containerColor = Color.White,
                        periodSelectorBorderColor = Color(0xFFE5E7EB),
                        periodSelectorSelectedContainerColor = Color(0xFF137FEC)
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                        showTimePicker = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137FEC))
                ) {
                    Text("Aceptar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar", color = Color(0xFF137FEC))
                }
            },
            containerColor = Color.White
        )
    }
}
