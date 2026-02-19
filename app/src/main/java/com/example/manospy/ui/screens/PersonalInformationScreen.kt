package com.example.manospy.ui.screens

import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.manospy.data.api.RetrofitClient
import com.example.manospy.data.local.SessionManager
import com.example.manospy.data.repository.AppRepository
import com.example.manospy.util.NetworkResult
import com.example.manospy.ui.components.AppScaffold
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInformationScreen(
    user: com.example.manospy.data.model.User? = null,
    onBack: () -> Unit,
    navController: androidx.navigation.NavController? = null,
    viewModel: com.example.manospy.ui.viewmodel.ServiceViewModel? = null
) {
    val primary = Color(0xFF137FEC)
    val primaryBlue = Color(0xFF2563EB)
    val bgLight = Color(0xFFF6F7F8)
    val textDark = Color(0xFF1F2937)
    val textGray = Color(0xFF6B7280)
    val white = Color.White
    val borderGray = Color(0xFFE5E7EB)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var navigateToAddresses by remember { mutableStateOf(false) }
    var navigateToNotifications by remember { mutableStateOf(false) }
    var navigateToHelp by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Launcher para cambiar foto
    val photoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            scope.launch {
                try {
                    val input = context.contentResolver.openInputStream(uri)
                    val bytes = input?.use { it.readBytes() }
                    if (bytes != null) {
                        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                        val fileName = "profile_${System.currentTimeMillis()}.jpg"
                        val repo = AppRepository(RetrofitClient.apiService)
                        when (val res = repo.uploadPhotoBase64(base64, fileName)) {
                            is NetworkResult.Success -> {
                                val photoRes = res.data
                                val photoUrl = photoRes.fullUrl.ifEmpty { photoRes.photoUrl }
                                val session = SessionManager(context)
                                val token = session.getToken() ?: ""
                                val current = session.getUser()
                                current?.let {
                                    val updated = it.copy(avatarUrl = photoUrl)
                                    session.saveUser(updated, token)
                                }
                            }
                            else -> {}
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    // Navegación
    if (navigateToNotifications) {
        NotificationSettingsScreen(onBack = { navigateToNotifications = false }, navController = navController)
        return
    }
    if (navigateToAddresses) {
        MyAddressesScreen(onBack = { navigateToAddresses = false }, navController = navController, showMyLocationButton = false, viewModel = viewModel)
        return
    }
    if (navigateToHelp) {
        HelpScreen(onBack = { navigateToHelp = false }, navController = navController)
        return
    }

    AppScaffold(
        title = "Mi Perfil",
        onBackClick = { onBack() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
        ) {
            // Avatar Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier
                        .size(112.dp)
                        .shadow(6.dp, CircleShape),
                    shape = CircleShape,
                    color = Color(0xFFF0F1F3),
                    border = androidx.compose.foundation.BorderStroke(4.dp, white)
                ) {
                    when {
                        imageUri != null -> {
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        !user?.avatarUrl.isNullOrEmpty() -> {
                            AsyncImage(
                                model = user?.avatarUrl,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        else -> {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = primaryBlue,
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = user?.name ?: "Juan Perez",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    ),
                    color = textDark,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = user?.email ?: "juan.perez@email.com",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = textGray,
                    textAlign = TextAlign.Center
                )
            }

            // Activity Section
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    "MI ACTIVIDAD",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ActivityCard(
                        title = "Servicios Solicitados",
                        value = user?.totalRequests?.toString() ?: "0",
                        modifier = Modifier.weight(1f)
                    )
                    ActivityCard(
                        title = "Calificación Cliente",
                        value = if ((user?.rating ?: 0.0) > 0.0) String.format("%.1f", user?.rating ?: 0.0) else "-",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Menu Section
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MenuCard(
                    icon = Icons.Default.Person,
                    title = "Editar Perfil",
                    onClick = { navController?.navigate("editProfile") }
                )
                MenuCard(
                    icon = Icons.Outlined.LocationOn,
                    title = "Mis Direcciones",
                    onClick = { navigateToAddresses = true }
                )
                MenuCard(
                    icon = Icons.Default.Notifications,
                    title = "Notificaciones",
                    onClick = { navigateToNotifications = true },
                    hasNotification = true
                )
                MenuCard(
                    icon = Icons.Outlined.Help,
                    title = "Ayuda",
                    onClick = { navigateToHelp = true }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .align(Alignment.CenterHorizontally)
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFFCA5A5))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Cerrar Sesión",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = Color(0xFFDC2626)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Text(
                text = "ManosPy v2.4.0 • Hecho con ❤️ en Paraguay",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
private fun ActivityCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(100.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color(0xFF137FEC)
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = Color(0xFF9CA3AF)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MenuCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit,
    hasNotification: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF3F4F6)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = Color(0xFF137FEC),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = Color(0xFF1F2937)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (hasNotification) {
                    Surface(
                        modifier = Modifier.size(8.dp),
                        shape = CircleShape,
                        color = Color(0xFFEF4444)
                    ) {}
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFFA3A3A3),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
