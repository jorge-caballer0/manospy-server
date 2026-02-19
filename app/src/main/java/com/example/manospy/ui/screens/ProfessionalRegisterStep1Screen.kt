@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.manospy.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import com.example.manospy.ui.viewmodel.ProfessionalRegisterViewModel
import java.io.ByteArrayOutputStream
import java.util.*

@Composable
fun ProfessionalRegisterStep1Screen(
    viewModel: ProfessionalRegisterViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit,
    onCancel: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var profilePhotoUri by remember { mutableStateOf<String?>(null) }
    var profilePhotoBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var errorMessage by remember { mutableStateOf("") }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Color palette - Stitch design
    val primaryBlue = Color(0xFF0056D2)
    val primaryDarkBlue = Color(0xFF003A94)
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)
    val fieldBg = Color.White
    val fieldOutline = Color(0xFFE2E8F0)
    val fieldPlaceholder = Color(0xFF94A3B8)
    val lightBg = Color(0xFFF8FAFC)

    // Photo picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            profilePhotoUri = uri.toString()
            // Load bitmap for display
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            profilePhotoBitmap = bitmap
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = lightBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "Atrás",
                        tint = textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = "Registro Profesional (1/3)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textPrimary
                    ),
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Box(modifier = Modifier.size(40.dp))
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Avatar with edit button
                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .clip(CircleShape)
                        .background(color = Color.White)
                        .border(4.dp, color = Color.White, shape = CircleShape)
                        .shadow(elevation = 8.dp, shape = CircleShape)
                ) {
                    if (profilePhotoBitmap != null) {
                        androidx.compose.foundation.Image(
                            bitmap = profilePhotoBitmap!!.asImageBitmap(),
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = Color(0xFFCBD5E1),
                                modifier = Modifier.size(56.dp)
                            )
                        }
                    }

                    // Edit button - bottom right
                    IconButton(
                        onClick = { photoPickerLauncher.launch("image/*") },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 4.dp, y = 4.dp)
                            .size(36.dp)
                            .background(
                                color = primaryBlue,
                                shape = CircleShape
                            )
                            .border(4.dp, color = lightBg, shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Photo",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Foto de Perfil",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = textSecondary,
                        fontWeight = FontWeight.Medium
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Form fields
                StitchProfInputField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = "Nombre Completo",
                    placeholder = "Nombre y apellido",
                    leadingIcon = Icons.Default.Badge,
                    primaryBlue = primaryBlue,
                    textSecondary = textSecondary,
                    fieldBg = fieldBg,
                    fieldOutline = fieldOutline,
                    fieldPlaceholder = fieldPlaceholder
                )

                Spacer(modifier = Modifier.height(16.dp))

                StitchProfInputField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    placeholder = "profesional@ejemplo.com",
                    leadingIcon = Icons.Default.Email,
                    primaryBlue = primaryBlue,
                    textSecondary = textSecondary,
                    fieldBg = fieldBg,
                    fieldOutline = fieldOutline,
                    fieldPlaceholder = fieldPlaceholder
                )

                Spacer(modifier = Modifier.height(16.dp))

                StitchProfInputField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    label = "Teléfono",
                    placeholder = "+595 9xx xxx xxx",
                    leadingIcon = Icons.Default.Call,
                    primaryBlue = primaryBlue,
                    textSecondary = textSecondary,
                    fieldBg = fieldBg,
                    fieldOutline = fieldOutline,
                    fieldPlaceholder = fieldPlaceholder
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Error message
                if (errorMessage.isNotEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = errorMessage,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF7F1D1D),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Next button
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val btnScale by animateFloatAsState(
                    if (isPressed) 0.97f else 1f,
                    label = "scale"
                )

                Button(
                    onClick = {
                        if (fullName.isNotBlank() && email.isNotBlank() && phoneNumber.isNotBlank()) {
                            errorMessage = ""
                            
                            // Si hay foto, subirla primero
                            if (profilePhotoBitmap != null) {
                                viewModel.uploadProfilePhoto(profilePhotoBitmap)
                                // Esperar a que se complete el upload
                                // En un caso real, deberías observar el photoUploadState
                            }
                            
                            // Guardar datos Step 1
                            viewModel.updateStep1Data(
                                name = fullName,
                                email = email,
                                phone = phoneNumber,
                                photoUri = profilePhotoUri
                            )
                            onNext()
                        } else {
                            errorMessage = "Por favor completa todos los campos"
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .scale(btnScale)
                        .shadow(
                            elevation = 12.dp,
                            shape = RoundedCornerShape(18.dp),
                            spotColor = primaryBlue
                        ),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                    interactionSource = interactionSource
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Siguiente",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }

        // Footer - Cancel button
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            TextButton(
                onClick = onCancel
            ) {
                Text(
                    text = "Cancelar",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = textSecondary
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StitchProfInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    primaryBlue: Color,
    textSecondary: Color,
    fieldBg: Color,
    fieldOutline: Color,
    fieldPlaceholder: Color,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A)
        ),
        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
    )

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = fieldPlaceholder) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = label,
                tint = if (value.isNotEmpty()) primaryBlue else textSecondary,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(18.dp),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = fieldBg,
            unfocusedContainerColor = fieldBg,
            focusedBorderColor = primaryBlue,
            unfocusedBorderColor = fieldOutline,
            cursorColor = primaryBlue
        ),
        textStyle = MaterialTheme.typography.bodyMedium.copy(
            color = Color(0xFF0F172A)
        )
    )
}
