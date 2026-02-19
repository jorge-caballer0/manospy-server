package com.example.manospy.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.R
import com.example.manospy.ui.viewmodel.AuthViewModel
import com.example.manospy.util.NetworkResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (com.example.manospy.data.model.User) -> Unit,
    onNavigateToRegisterClient: () -> Unit,
    onNavigateToRegisterProfessional: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf("client") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val authState by viewModel.authState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Color palette - Stitch design
    val primaryBlue = Color(0xFF0056D2)
    val primaryDarkBlue = Color(0xFF003A94)
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)
    val fieldBg = Color.White
    val fieldOutline = Color(0xFFE2E8F0)
    val fieldPlaceholder = Color(0xFF94A3B8)
    val amberBadge = Color(0xFFFCD34D)
    val lightBg = Color(0xFFF8FAFC)
    val darkBg = Color(0xFF0F172A)

    LaunchedEffect(authState) {
        try {
            val state = authState
            when (state) {
                is NetworkResult.Success -> {
                    val user = state.data?.user
                    if (user != null) {
                        onLoginSuccess(user)
                    } else {
                        errorMessage = "Error: No se recibieron datos del usuario"
                        snackbarHostState.showSnackbar("Error: Usuario inválido")
                    }
                }
                is NetworkResult.Error -> {
                    errorMessage = state.message ?: "Error desconocido"
                    snackbarHostState.showSnackbar(state.message ?: "Ocurrió un error inesperado")
                }
                is NetworkResult.Loading -> {
                    // En carga, no hacer nada
                }
                is NetworkResult.Idle -> {
                    // Estado inicial, no hacer nada
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
            snackbarHostState.showSnackbar("Error: ${e.localizedMessage}")
        }
    }

    fun validateAndLogin() {
        errorMessage = null
        if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "Ingresa un correo electrónico válido"
            return
        }
        if (password.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
            return
        }
        // ✅ ahora usamos el rol seleccionado
        viewModel.login(email, password, selectedRole)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(lightBg, lightBg)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .statusBarsPadding()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                // Back button header
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = 16.dp, top = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "Atrás",
                        tint = textPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Centered logo with badge
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(80.dp)
                        .background(
                            color = primaryBlue,
                            shape = RoundedCornerShape(24.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "ManosPy",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )

                    // Amber verification badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(24.dp)
                            .background(
                                color = amberBadge,
                                shape = CircleShape
                            )
                            .clip(CircleShape),
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

                Spacer(modifier = Modifier.height(24.dp))

                // Title with gradient text effect
                Text(
                    text = "Iniciar Sesión",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = primaryBlue,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = "Soluciones expertas a tu alcance",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Role toggle segmented control
                Row(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(
                            color = Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    RoleToggleButton(
                        text = "Cliente",
                        isSelected = selectedRole == "client",
                        onClick = { selectedRole = "client" },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    )
                    RoleToggleButton(
                        text = "Profesional",
                        isSelected = selectedRole == "professional",
                        onClick = { selectedRole = "professional" },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Content padding
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Column {
                        // Email field
                        Text(
                            text = "Correo electrónico",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; errorMessage = null },
                            placeholder = { Text("tu@correo.com", color = fieldPlaceholder) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Email",
                                    tint = if (email.isNotEmpty()) primaryBlue else textSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            isError = errorMessage != null && email.isBlank(),
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

                        Spacer(modifier = Modifier.height(20.dp))

                        // Password field
                        Text(
                            text = "Contraseña",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary
                            ),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = null },
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
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                        tint = textSecondary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            isError = errorMessage != null && password.length < 6,
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

                        // Forgot password link - right aligned
                        Text(
                            text = "¿Olvidaste tu contraseña?",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = primaryBlue,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier
                                .align(Alignment.End)
                                .padding(top = 12.dp)
                                .clickable { onNavigateToForgotPassword() }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Error message
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage!!,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp)
                            )
                        }

                        // Login button
                        val interactionSource = remember { MutableInteractionSource() }
                        val isPressed by interactionSource.collectIsPressedAsState()
                        val btnScale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "scale")

                        if (authState is NetworkResult.Loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = primaryBlue,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        } else {
                            Button(
                                onClick = { validateAndLogin() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .scale(btnScale)
                                    .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = primaryBlue),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = primaryBlue
                                ),
                                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                                interactionSource = interactionSource
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Iniciar Sesión",
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 15.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .scale(-1f)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Divider
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Divider(modifier = Modifier.weight(1f), color = fieldOutline)
                    Text(
                        text = "o",
                        style = MaterialTheme.typography.bodySmall,
                        color = textSecondary
                    )
                    Divider(modifier = Modifier.weight(1f), color = fieldOutline)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Register prompt
                Text(
                    text = "¿No tienes cuenta?",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = textSecondary,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Register buttons
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onNavigateToRegisterClient() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Cliente",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            )
                        }

                        Button(
                            onClick = { onNavigateToRegisterProfessional() },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .shadow(4.dp, RoundedCornerShape(12.dp)),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryDarkBlue),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                        ) {
                            Text(
                                text = "Profesional",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}


@Composable
fun RoleToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(10.dp),
        color = if (isSelected) Color.White else Color.Transparent,
        shadowElevation = if (isSelected) 2.dp else 0.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color(0xFF0056D2) else Color(0xFF64748B),
                    fontSize = 14.sp
                )
            )
        }
    }
}
