package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import android.location.Geocoder
import java.util.Locale
import com.google.android.gms.maps.CameraUpdateFactory
import kotlinx.coroutines.launch
import com.example.manospy.ui.components.LocationSearchField
import com.example.manospy.ui.components.SyncStatusBarWithHeader
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors
import com.example.manospy.ui.theme.AppDimensions
import com.example.manospy.util.PlacesHelper
import androidx.compose.runtime.collectAsState

fun getAddressFromCoord(context: android.content.Context, latitude: Double, longitude: Double): String {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latitude, longitude, 1)
        if (!addresses.isNullOrEmpty()) {
            val address = addresses[0]
            val parts = mutableListOf<String>()
            address.thoroughfare?.let { parts.add(it) }
            address.subLocality?.let { parts.add(it) }
            address.locality?.let { parts.add(it) }
            address.countryName?.let { parts.add(it) }
            if (parts.isNotEmpty()) parts.joinToString(", ") else "Ubicación seleccionada"
        } else {
            "Ubicación seleccionada"
        }
    } catch (e: Exception) {
        "Ubicación seleccionada"
    }
}


data class MapLocation(
    val name: String,
    val address: String,
    val x: Float,
    val y: Float
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAddressScreen(
    editingAddress: Address? = null,
    onSave: (label: String, address: String, reference: String) -> Unit,
    onBack: () -> Unit,
    navController: androidx.navigation.NavController? = null,
    viewModel: com.example.manospy.ui.viewmodel.ServiceViewModel? = null
) {
    val primary = AppColors.PrimaryBlue
    val bgLight = AppColors.BgLight
    val white = AppColors.BgWhite
    val textGray = AppColors.TextSecondary
    val textDark = AppColors.TextPrimary
    val inputBgColor = Color(0xFFF3F4F6)

    var nameInput by remember { mutableStateOf(editingAddress?.label ?: "") }
    var addressInput by remember { mutableStateOf(editingAddress?.fullAddress ?: "") }
    var referenceInput by remember { mutableStateOf("") }
    var isLocationSelected by remember { mutableStateOf(editingAddress != null) }
    var isSaving by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Observar cambios en saveAddressStatus
    if (viewModel != null) {
        val saveAddressStatus by viewModel.saveAddressStatus.collectAsState()
        
        LaunchedEffect(saveAddressStatus) {
            when (saveAddressStatus) {
                is com.example.manospy.util.NetworkResult.Loading -> {
                    isSaving = true
                }
                is com.example.manospy.util.NetworkResult.Success -> {
                    isSaving = false
                    showSuccessDialog = true
                }
                is com.example.manospy.util.NetworkResult.Error -> {
                    isSaving = false
                    errorMessage = (saveAddressStatus as com.example.manospy.util.NetworkResult.Error).message ?: "Error al guardar"
                    showErrorDialog = true
                }
                else -> {}
            }
        }
    }

    val localContext = LocalContext.current
    val fusedClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(localContext)
    var markerPosition by remember { mutableStateOf(LatLng(-25.296, -57.635)) }
    val scope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    val mapPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            try {
                fusedClient.lastLocation.addOnSuccessListener { loc: Location? ->
                    loc?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        markerPosition = latLng
                        addressInput = getAddressFromCoord(localContext, it.latitude, it.longitude)
                        isLocationSelected = true
                    }
                }
            } catch (e: Exception) {}
        }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 14f)
    }

    AppScaffold(title = "Añadir Dirección", onBackClick = onBack) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Search field
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppDimensions.SpaceLarge)
                ) {
                    LocationSearchField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        onSuggestionSelected = { suggestion ->
                            markerPosition = LatLng(suggestion.latitude, suggestion.longitude)
                            addressInput = suggestion.displayName
                            isLocationSelected = true
                            scope.launch {
                                cameraPositionState.animate(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(suggestion.latitude, suggestion.longitude),
                                        16f
                                    )
                                )
                            }
                        },
                        placeholder = "Buscar calle, barrio, ciudad...",
                        showSuggestions = true
                    )
            }

            // Mapa (cuadrado)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    onMapClick = { latLng ->
                        markerPosition = latLng
                        addressInput = getAddressFromCoord(localContext, latLng.latitude, latLng.longitude)
                        isLocationSelected = true
                        scope.launch {
                            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                        }
                    }
                ) {
                    Marker(state = MarkerState(position = markerPosition))
                }

                // MyLocation button
                IconButton(
                    onClick = {
                        val granted = androidx.core.content.PermissionChecker.checkSelfPermission(localContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == androidx.core.content.PermissionChecker.PERMISSION_GRANTED
                        if (!granted) {
                            mapPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        } else {
                            fusedClient.lastLocation.addOnSuccessListener { loc: Location? ->
                                loc?.let {
                                    val latLng = LatLng(it.latitude, it.longitude)
                                    markerPosition = latLng
                                    addressInput = getAddressFromCoord(localContext, it.latitude, it.longitude)
                                    isLocationSelected = true
                                    scope.launch {
                                        cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                                    }
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, Color(0xFFE6EEF9), CircleShape)
                ) {
                    Icon(
                        Icons.Default.MyLocation,
                        contentDescription = "Mi ubicación",
                        tint = primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Formulario
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = white,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .imePadding()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Nombre
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "NOMBRE DE LA UBICACIÓN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = textGray
                        )

                        TextField(
                            value = nameInput,
                            onValueChange = { nameInput = it },
                            placeholder = { Text("Ej: Casa, Oficina...", color = Color(0xFF9CA3AF)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .background(inputBgColor, RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(20.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = inputBgColor,
                                unfocusedContainerColor = inputBgColor,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = primary,
                                focusedTextColor = Color(0xFF1F2937),
                                unfocusedTextColor = Color(0xFF1F2937)
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1F2937),
                                fontSize = 15.sp
                            ),
                            leadingIcon = {
                                Icon(
                                    Icons.AutoMirrored.Filled.Label,
                                    contentDescription = null,
                                    tint = textGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )
                    }

                    // Dirección
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            "DIRECCIÓN",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = textGray
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 56.dp)
                                .background(inputBgColor, RoundedCornerShape(20.dp))
                                .clip(RoundedCornerShape(20.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Map,
                                contentDescription = null,
                                tint = textGray,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(top = 4.dp)
                            )

                            TextField(
                                value = addressInput,
                                onValueChange = { addressInput = it },
                                placeholder = { Text("Escribe la dirección completa") },
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    cursorColor = Color.Black,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    fontSize = 14.sp,
                                    color = Color.Black
                                ),
                                singleLine = false
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            // Guardar en la base de datos usando el ViewModel si está disponible
                            if (viewModel != null) {
                                viewModel.saveAddress(
                                    label = nameInput,
                                    fullAddress = addressInput,
                                    latitude = markerPosition.latitude,
                                    longitude = markerPosition.longitude,
                                    reference = referenceInput.ifEmpty { null }
                                )
                            } else {
                                // Si no hay ViewModel, solo llamar al callback
                                onSave(nameInput, addressInput, referenceInput)
                                onBack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (nameInput.isNotBlank() && addressInput.isNotBlank()) primary else Color(0xFFD1D5DB),
                            disabledContainerColor = Color(0xFFD1D5DB)
                        ),
                        enabled = nameInput.isNotBlank() && addressInput.isNotBlank() && !isSaving
                    ) {
                        if (isSaving) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Text(
                                    "Guardando...",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = Color.White
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Guardar Dirección",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "MANOSPY • ASUNCIÓN",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color(0xFF9CA3AF),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                }
            }
        }
    }

    // Diálogo de éxito
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(28.dp))
                    Text("¡Dirección Guardada!", fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
                }
            },
            text = { Text("Tu nueva dirección ha sido guardada exitosamente.", color = AppColors.TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onSave(nameInput, addressInput, referenceInput)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Aceptar", color = Color.White)
                }
            },
            containerColor = Color.White
        )
    }

    // Diálogo de error
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ErrorOutline, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(28.dp))
                    Text("Error al Guardar", fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                }
            },
            text = { Text(errorMessage, color = AppColors.TextSecondary) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Aceptar", color = Color.White)
                }
            },
            containerColor = Color.White
        )
    }
}
