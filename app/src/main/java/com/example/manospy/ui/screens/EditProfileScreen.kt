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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.manospy.data.repository.AppRepository
import com.example.manospy.data.api.RetrofitClient
import com.example.manospy.data.local.SessionManager
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors
import com.example.manospy.ui.theme.AppDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    user: com.example.manospy.data.model.User? = null,
    repository: AppRepository? = null,
    onBack: () -> Unit,
    onSave: (name: String, email: String, phoneNumber: String) -> Unit = { _, _, _ -> }
) {
    val primary = AppColors.PrimaryBlue
    val bgLight = AppColors.BgLight
    val white = AppColors.BgWhite
    val textGray = AppColors.TextSecondary
    val textDark = AppColors.TextPrimary

    var name by remember { mutableStateOf(user?.name ?: "Juan Perez") }
    var email by remember { mutableStateOf(user?.email ?: "juan.perez@email.com") }
    var phoneNumber by remember { mutableStateOf(user?.phoneNumber ?: "981 123456") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = rememberCoroutineScope()
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var navigateToPhoneVerification by remember { mutableStateOf(false) }
    var navigateToEmailVerification by remember { mutableStateOf(false) }
    var navigateToEmailChange by remember { mutableStateOf(false) }
    var navigateToPhoneVerify by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }

    // Launcher para seleccionar imagen y subirla
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            // Leer bytes y convertir a Base64 y subir
            scope.launch {
                try {
                    val input = context.contentResolver.openInputStream(uri)
                    val bytes = input?.use { it.readBytes() }
                    if (bytes != null) {
                        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                        val fileName = "profile_${System.currentTimeMillis()}.jpg"
                        // Llamar al repositorio para subir
                        val repo = repository ?: AppRepository(RetrofitClient.apiService)
                        when (val res = repo.uploadPhotoBase64(base64, fileName)) {
                            is NetworkResult.Success -> {
                                val photoRes = res.data
                                val photoUrl = photoRes.fullUrl.ifEmpty { photoRes.photoUrl }
                                // Guardar en SessionManager localmente
                                try {
                                    val session = SessionManager(context)
                                    val token = session.getToken() ?: ""
                                    val current = session.getUser()
                                    current?.let {
                                        val updated = it.copy(avatarUrl = photoUrl)
                                        session.saveUser(updated, token)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            is NetworkResult.Error -> {
                                // manejar error (opcional mostrar toast)
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

    // Navegación a PhoneVerificationScreen
    if (navigateToPhoneVerification) {
        PhoneVerificationScreen(
            onBack = { navigateToPhoneVerification = false },
            onVerificationSuccess = { newPhone ->
                phoneNumber = newPhone.removePrefix("+595")
                navigateToPhoneVerification = false
            }
        )
        return
    }

    // Navegación a EmailVerificationScreen
    if (navigateToEmailVerification) {
        EmailVerificationScreen(
            onBack = { navigateToEmailVerification = false },
            onVerificationSuccess = { navigateToEmailVerification = false }
        )
        return
    }

    // Navegación a EmailChangeScreen
    if (navigateToEmailChange) {
        EmailChangeScreen(
            onBack = { navigateToEmailChange = false },
            onChangeSuccess = { 
                email = "" // Se actualizará cuando refresh el usuario
                navigateToEmailChange = false 
            }
        )
        return
    }

    // Navegación a PhoneVerifyScreen
    if (navigateToPhoneVerify) {
        PhoneVerifyScreen(
            onBack = { navigateToPhoneVerify = false },
            onVerificationSuccess = { navigateToPhoneVerify = false }
        )
        return
    }

    AppScaffold(
        title = "Editar Perfil",
        headerBackgroundColor = Color.White,
        headerTextColor = Color.Black,
        onBackClick = { onBack() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Avatar Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(132.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Avatar circular
                    Surface(
                        modifier = Modifier
                            .size(132.dp)
                            .shadow(6.dp, CircleShape),
                        shape = CircleShape,
                        color = Color(0xFFFACE8D),
                        border = androidx.compose.foundation.BorderStroke(4.dp, white)
                    ) {
                        if (imageUri != null) {
                            // Mostrar foto seleccionada
                            AsyncImage(
                                model = imageUri,
                                contentDescription = "Foto de perfil seleccionada",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else if (!user?.avatarUrl.isNullOrEmpty()) {
                            // Mostrar foto del servidor
                            AsyncImage(
                                model = user?.avatarUrl,
                                contentDescription = "Foto de perfil del usuario",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Mostrar placeholder
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color(0xFFFACE8D))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = Color(0xFF92400E).copy(alpha = 0.3f)
                                )
                            }
                        }
                    }

                    // Botón cámara
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .align(Alignment.BottomEnd)
                            .offset(x = 2.dp, y = 2.dp)
                            .shadow(4.dp, CircleShape),
                        shape = CircleShape,
                        color = primary
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { launcher.launch("image/*") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoCamera,
                                contentDescription = "Cambiar foto",
                                tint = white,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Cambiar foto de perfil",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Formulario
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Nombre Completo
                Column {
                    Text(
                        "Nombre Completo",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        color = textGray,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = { Text("Tu nombre") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(2.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = white,
                            unfocusedContainerColor = white,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = primary,
                            focusedTextColor = textDark,
                            unfocusedTextColor = textDark
                        ),
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = textDark
                        ),
                        singleLine = true
                    )
                }

                // Email - Con botones Verificar y Cambiar
                Column {
                    Text(
                        "Email",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        color = textGray,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                email,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF9CA3AF),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { navigateToEmailVerification = true },
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Verificar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = white)
                            }
                            Button(
                                onClick = { navigateToEmailChange = true },
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Cambiar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = white)
                            }
                        }
                    }
                }

                // Teléfono - Con botones Verificar y Cambiar
                Column {
                    Text(
                        "Teléfono",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        ),
                        color = textGray,
                        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .background(Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
                            .border(
                                width = 1.dp,
                                color = Color(0xFFE5E7EB),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "+595 ${phoneNumber}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF9CA3AF),
                                modifier = Modifier.weight(1f)
                            )
                            Button(
                                onClick = { navigateToPhoneVerify = true },
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF22C55E)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Verificar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = white)
                            }
                            Button(
                                onClick = { navigateToPhoneVerification = true },
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Cambiar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = white)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Para cambiar tu teléfono, deberás verificar el nuevo número con un código",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp
                        ),
                        color = Color(0xFF9CA3AF),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón Guardar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 24.dp)
            ) {
                Button(
                    onClick = {
                        if (isSaving) return@Button
                        isSaving = true
                        scope.launch {
                            try {
                                // Guardar en BD mediante API
                                val repo = repository ?: AppRepository(RetrofitClient.apiService)
                                when (val res = repo.updateProfile(name = name)) {
                                    is NetworkResult.Success -> {
                                        // Actualizar sesión localmente
                                        val session = SessionManager(context)
                                        val token = session.getToken() ?: ""
                                        val current = session.getUser()
                                        if (current != null) {
                                            val updated = current.copy(name = name, email = email)
                                            session.saveUser(updated, token)
                                        }
                                        onSave(name, email, phoneNumber)
                                        isSaving = false
                                        onBack()
                                    }
                                    is NetworkResult.Error -> {
                                        // Si falla la API, guardar localmente al menos
                                        try {
                                            val session = SessionManager(context)
                                            val token = session.getToken() ?: ""
                                            val current = session.getUser()
                                            if (current != null) {
                                                val updated = current.copy(name = name, email = email)
                                                session.saveUser(updated, token)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        isSaving = false
                                    }
                                    else -> {
                                        isSaving = false
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = white, strokeWidth = 2.dp)
                    } else {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = white,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Guardar Cambios",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = white
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
