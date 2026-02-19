package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import android.Manifest
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import android.location.Geocoder
import java.util.Locale
import com.example.manospy.ui.components.SyncStatusBarWithHeader
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors

fun getAddressFromCoordinate(context: Context, latitude: Double, longitude: Double): String {
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
            if (parts.isNotEmpty()) parts.joinToString(", ") else "Ubicación detectada"
        } else {
            "Ubicación detectada"
        }
    } catch (e: Exception) {
        "Ubicación detectada"
    }
}

data class Address(
    val id: String = "",
    val label: String = "",
    val fullAddress: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val icon: ImageVector = Icons.Default.Home
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyAddressesScreen(
    onBack: () -> Unit,
    navController: androidx.navigation.NavController? = null,
    onSelect: ((Address) -> Unit)? = null,
    showMyLocationButton: Boolean = true,
    requireConfirmOnSelect: Boolean = false,
    viewModel: com.example.manospy.ui.viewmodel.ServiceViewModel? = null
) {
    val primary = Color(0xFF137FEC)
    val bgLight = Color(0xFFF6F7F8)
    val white = Color.White
    val textGray = Color(0xFF64748B)
    val textDark = Color(0xFF1F2937)
    val borderGray = Color(0xFFE5E7EB)

    // Estado de direcciones
    var addresses by remember {
        mutableStateOf(
            listOf(
                Address(
                    id = "1",
                    label = "Casa",
                    fullAddress = "Avenida Mariscal López 1234, Edificio Plaza, Apto 402. Asunción, Paraguay.",
                    latitude = -25.2637,
                    longitude = -57.5759,
                    icon = Icons.Default.Home
                ),
                Address(
                    id = "2",
                    label = "Trabajo",
                    fullAddress = "Paseo La Galería, Torre 2, Piso 15. Santa Teresa. Asunción, Paraguay.",
                    latitude = -25.2890,
                    longitude = -57.5732,
                    icon = Icons.Default.Work
                ),
                Address(
                    id = "3",
                    label = "Casa de Mamá",
                    fullAddress = "Barrio Las Lomas, Calle Isaac Kostianovsky 455. Luque.",
                    latitude = -25.3064,
                    longitude = -57.5015,
                    icon = Icons.Default.Favorite
                )
            )
        )
    }

    // Si se proporciona un ViewModel, sincronizar con las direcciones del servidor
    if (viewModel != null) {
        val clientAddressesState by viewModel.clientAddresses.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.fetchAddresses()
        }

        LaunchedEffect(clientAddressesState) {
            when (clientAddressesState) {
                is com.example.manospy.util.NetworkResult.Success -> {
                    val list = (clientAddressesState as com.example.manospy.util.NetworkResult.Success).data
                    addresses = list.map { ca ->
                        Address(
                            id = ca.id ?: System.currentTimeMillis().toString(),
                            label = ca.label ?: "",
                            fullAddress = ca.fullAddress ?: "",
                            latitude = ca.latitude ?: 0.0,
                            longitude = ca.longitude ?: 0.0,
                            icon = Icons.Default.LocationOn
                        )
                    }
                }
                else -> {
                    // mantener lista local si está cargando o hay error
                }
            }
        }
    }

    // Estados de navegación y dirección   
    var editingAddress by remember { mutableStateOf<Address?>(null) }
    var navigateToAddAddress by remember { mutableStateOf(false) }

    // Ubicación
    val context = LocalContext.current
    var locationText by remember { mutableStateOf<String?>(null) }
    val fusedClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            try {
                fusedClient.lastLocation.addOnSuccessListener { loc: Location? ->
                    loc?.let {
                        val fullAddress = getAddressFromCoordinate(context, it.latitude, it.longitude)
                        locationText = fullAddress
                        addresses = addresses + Address(
                            id = System.currentTimeMillis().toString(),
                            label = "Mi ubicación",
                            fullAddress = fullAddress,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            icon = Icons.Default.LocationOn
                        )
                    }
                }
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    var selectedAddress by remember { mutableStateOf<Address?>(null) }

    // Navegación a AddAddressScreen
    if (navigateToAddAddress) {
        AddAddressScreen(
            editingAddress = editingAddress,
            onSave = { label, address, reference ->
                if (editingAddress != null) {
                    addresses = addresses.map { addr ->
                        if (addr.id == editingAddress!!.id) {
                            addr.copy(label = label, fullAddress = address)
                        } else {
                            addr
                        }
                    }
                } else {
                    addresses = addresses + Address(
                        id = System.currentTimeMillis().toString(),
                        label = label,
                        fullAddress = address,
                        icon = Icons.Default.LocationOn
                    )
                }
                navigateToAddAddress = false
                editingAddress = null
            },
            onBack = {
                navigateToAddAddress = false
                editingAddress = null
            },
            navController = navController,
            viewModel = viewModel
        )
        return
    }

    AppScaffold(title = "Mis Direcciones", onBackClick = onBack) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Lista de direcciones
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 100.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp)
                ) {
                    items(addresses.sortedBy { it.label == "Mi ubicación" }) { address ->
                        AddressCard(
                            address = address,
                            onEdit = {
                                editingAddress = address
                                navigateToAddAddress = true
                            },
                            onDelete = {
                                addresses = addresses.filter { it.id != address.id }
                            },
                            onSelect = {
                                if (requireConfirmOnSelect) {
                                    selectedAddress = address
                                } else {
                                    onSelect?.invoke(address)
                                    // close when selecting from parent
                                    onBack()
                                }
                            },
                            isSelected = selectedAddress?.id == address.id,
                            primary = primary,
                            borderGray = borderGray,
                            textGray = textGray,
                            white = white
                        )
                    }
            }
        }

        // Barra de confirmación cuando se requiere añadir la dirección seleccionada
        if (requireConfirmOnSelect && selectedAddress != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(1f)
                    .background(color = bgLight)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Dirección seleccionada: ${selectedAddress?.label}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textDark
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                selectedAddress = null
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                        ) {
                            Text("Cancelar")
                        }

                        Button(
                            onClick = {
                                selectedAddress?.let { addr ->
                                    onSelect?.invoke(addr)
                                    onBack()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primary)
                        ) {
                            Text("Aceptar", color = Color.White)
                        }
                    }

                }
            }
        }

        // Botón flotante - Añadir Dirección (posicionado al pie)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    color = bgLight
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        editingAddress = null
                        navigateToAddAddress = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(6.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            "Añadir Nueva Dirección",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = Color.White
                        )
                    }
                }

                Text(
                    "ManosPy v2.4.0 • Gestiona tus ubicaciones para servicios rápidos",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        }
    }
}

@Composable
fun AddressCard(
    address: Address,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSelect: (() -> Unit)? = null,
    isSelected: Boolean = false,
    primary: Color,
    borderGray: Color,
    textGray: Color,
    white: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(20.dp))
            .then(if (onSelect != null) Modifier.clickable { onSelect() } else Modifier),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) primary.copy(alpha = 0.06f) else white),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) primary else borderGray)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icono
            Surface(
                modifier = Modifier.size(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF0F4FF)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = address.icon,
                        contentDescription = null,
                        tint = primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Contenido
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = address.label,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        ),
                        color = Color(0xFF1F2937),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Seleccionado: mostrar check pequeño
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Seleccionado",
                            tint = primary,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 8.dp)
                        )
                    }

                    // Botones edit y delete
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(0.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onEdit,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = textGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Eliminar",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = address.fullAddress,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    color = textGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
