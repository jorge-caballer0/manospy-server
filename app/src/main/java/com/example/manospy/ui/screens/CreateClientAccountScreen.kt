package com.example.manospy.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.viewmodel.AuthViewModel
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClientAccountScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: (com.example.manospy.data.model.User) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToPrivacy: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    // Color palette - Stitch design
    val primaryBlue = Color(0xFF0056D2)
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)
    val fieldBg = Color.White
    val fieldOutline = Color(0xFFE2E8F0)
    val fieldPlaceholder = Color(0xFF94A3B8)
    val amberBadge = Color(0xFFFCD34D)
    val lightBg = Color(0xFFF8FAFC)

    LaunchedEffect(authState) {
        try {
            val state = authState
            when (state) {
                is NetworkResult.Success -> {
                    val user = state.data?.user
                    if (user != null) {
                        errorMessage = "✓ Cuenta creada exitosamente"
                        showError = true
                        delay(1500)
                        onRegisterSuccess(user)
                    } else {
                        errorMessage = "Error: No se recibieron datos del usuario. Intenta de nuevo."
                        showError = true
                    }
                }
                is NetworkResult.Error -> {
                    errorMessage = state.message ?: "Error al registrarse. Intenta de nuevo."
                    showError = true
                }
                else -> {}
            }
        } catch (e: Exception) {
            errorMessage = "Error inesperado. Intenta de nuevo."
            showError = true
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
                .verticalScroll(rememberScrollState())
        ) {
            // Header - Back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onNavigateToLogin,
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
                    text = "Crear Cuenta",
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

            // Content area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Logo with badge
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = primaryBlue,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .scale(1.03f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "ManosPy",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )

                    // Amber verification badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .size(24.dp)
                            .background(
                                color = amberBadge,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clip(RoundedCornerShape(6.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = "Verified",
                            tint = primaryBlue,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title with gradient underline
                Text(
                    text = "Únete a ManosPy",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = primaryBlue
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Form fields
                StitchInputField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Nombre Completo",
                    placeholder = "Ej. Juan Pérez",
                    leadingIcon = Icons.Default.Person,
                    primaryBlue = primaryBlue,
                    textSecondary = textSecondary,
                    fieldBg = fieldBg,
                    fieldOutline = fieldOutline,
                    fieldPlaceholder = fieldPlaceholder
                )

                Spacer(modifier = Modifier.height(16.dp))

                StitchInputField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Correo electrónico",
                    placeholder = "tu@correo.com",
                    leadingIcon = Icons.Default.Email,
                    primaryBlue = primaryBlue,
                    textSecondary = textSecondary,
                    fieldBg = fieldBg,
                    fieldOutline = fieldOutline,
                    fieldPlaceholder = fieldPlaceholder
                )

                Spacer(modifier = Modifier.height(16.dp))

                StitchInputField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Teléfono",
                    placeholder = "+595 9xx xxx xxx",
                    leadingIcon = Icons.Default.Call,
                    primaryBlue = primaryBlue,
                    textSecondary = textSecondary,
                    fieldBg = fieldBg,
                    fieldOutline = fieldOutline,
                    fieldPlaceholder = fieldPlaceholder
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Password field with visibility toggle
                Text(
                    text = "Contraseña",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    ),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 8.dp, start = 4.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("••••••••", color = fieldPlaceholder) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = if (password.isNotEmpty()) primaryBlue else textSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (passwordVisible) "Hide" else "Show",
                                tint = textSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = fieldBg,
                        unfocusedContainerColor = fieldBg,
                        focusedBorderColor = primaryBlue,
                        unfocusedBorderColor = fieldOutline,
                        cursorColor = primaryBlue
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = textPrimary
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Error/Success message
                if (showError) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = if (errorMessage.startsWith("✓"))
                            Color(0xFFD1FAE5)
                        else
                            Color(0xFFFEE2E2)
                    ) {
                        Text(
                            text = errorMessage,
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (errorMessage.startsWith("✓"))
                                Color(0xFF065F46)
                            else
                                Color(0xFF7F1D1D),
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Register button
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val btnScale by animateFloatAsState(
                    if (isPressed && authState !is NetworkResult.Loading) 0.97f else 1f,
                    label = "scale"
                )
                val isLoading = authState is NetworkResult.Loading

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = primaryBlue,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    Button(
                        onClick = {
                            if (name.isNotBlank() && email.isNotBlank() && password.length >= 6 && phone.isNotBlank()) {
                                errorMessage = ""
                                showError = false
                                viewModel.registerClient(name, email, password, phone)
                            } else {
                                errorMessage = "Por favor completa todos los campos (contraseña mínimo 6 caracteres)"
                                showError = true
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
                                text = "Registrarse",
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
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Divider
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = fieldOutline,
                        thickness = 1.dp
                    )
                    Text(
                        text = "o",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondary
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = fieldOutline,
                        thickness = 1.dp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Login link
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Ya tienes una cuenta? ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textSecondary
                        )
                    )
                    Text(
                        text = "Inicia Sesión",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = primaryBlue,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Terms and Privacy
                Text(
                    text = buildAnnotatedString {
                        append("Al hacer clic en Registrarse, aceptas nuestros ")
                        withStyle(style = SpanStyle(color = textPrimary, fontWeight = FontWeight.Bold)) {
                            append("Términos de Servicio")
                        }
                        append(" y la ")
                        withStyle(style = SpanStyle(color = textPrimary, fontWeight = FontWeight.Bold)) {
                            append("Política de Privacidad")
                        }
                        append(".")
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(onClick = onNavigateToTerms) {
                        Text("Términos", style = MaterialTheme.typography.labelSmall.copy(color = primaryBlue))
                    }
                    Text("•", modifier = Modifier.align(Alignment.CenterVertically), color = textSecondary)
                    TextButton(onClick = onNavigateToPrivacy) {
                        Text("Privacidad", style = MaterialTheme.typography.labelSmall.copy(color = primaryBlue))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StitchInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
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
        modifier = Modifier
            .padding(bottom = 8.dp, start = 4.dp)
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
