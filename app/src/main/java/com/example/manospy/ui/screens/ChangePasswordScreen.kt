package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.manospy.ui.components.AppScaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit,
    onPasswordChanged: suspend (currentPassword: String, newPassword: String) -> Boolean = { _, _ -> true },
    navController: androidx.navigation.NavController? = null
) {
    val primary = Color(0xFF137FEC)
    val bgLight = Color(0xFFF6F7F8)
    val white = Color.White
    val textGray = Color(0xFF64748B)
    val textDark = Color(0xFF1F2937)
    val borderGray = Color(0xFFE5E7EB)
    val successColor = Color(0xFF10B981)
    val errorColor = Color(0xFFEF4444)

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var showCurrentPassword by remember { mutableStateOf(false) }
    var showNewPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Validaciones
    val hasMinLength = newPassword.length >= 8
    val hasNumber = newPassword.any { it.isDigit() }
    val hasSymbol = newPassword.any { it in "!@#$%^&*()_+-=[]{}|;:',.<>?/\\`~" }
    val passwordsMatch = newPassword == confirmPassword && newPassword.isNotEmpty()
    val isFormValid = currentPassword.isNotEmpty() && hasMinLength && hasNumber && hasSymbol && passwordsMatch

    AppScaffold(
        title = "Cambiar Contraseña",
        onBackClick = { onBack() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Información
            Text(
                "Para proteger tu cuenta, asegúrate de que tu nueva contraseña tenga al menos 8 caracteres e incluya números y símbolos.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                ),
                color = textGray,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Contraseña Actual
            Text(
                "CONTRASEÑA ACTUAL",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            PasswordInputField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                placeholder = "••••••••",
                isVisible = showCurrentPassword,
                onVisibilityToggle = { showCurrentPassword = !showCurrentPassword },
                icon = Icons.Default.Lock
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Nueva Contraseña
            Text(
                "NUEVA CONTRASEÑA",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            PasswordInputField(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholder = "••••••••",
                isVisible = showNewPassword,
                onVisibilityToggle = { showNewPassword = !showNewPassword },
                icon = Icons.Default.VpnKey
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Confirmar Nueva Contraseña
            Text(
                "CONFIRMAR NUEVA CONTRASEÑA",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            PasswordInputField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = "••••••••",
                isVisible = showConfirmPassword,
                onVisibilityToggle = { showConfirmPassword = !showConfirmPassword },
                icon = Icons.Default.VpnKey
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Validaciones
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(2.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = white),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderGray)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Mínimo 8 caracteres
                    ValidationCheckItem(
                        icon = if (hasMinLength) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        text = "Mínimo 8 caracteres",
                        isChecked = hasMinLength,
                        successColor = successColor
                    )

                    // Al menos un número
                    ValidationCheckItem(
                        icon = if (hasNumber) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        text = "Incluir al menos un número",
                        isChecked = hasNumber,
                        successColor = successColor
                    )

                    // Al menos un símbolo
                    ValidationCheckItem(
                        icon = if (hasSymbol) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        text = "Incluir un símbolo (!@#\$%^&*)",
                        isChecked = hasSymbol,
                        successColor = successColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Mensajes de error
            if (errorMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .align(Alignment.CenterHorizontally)
                        .shadow(2.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFCDD2))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = errorColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            errorMessage,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                            color = errorColor
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Botón Actualizar
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = ""
                },
                enabled = isFormValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .align(Alignment.CenterHorizontally)
                    .height(56.dp)
                    .shadow(4.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary,
                    disabledContainerColor = Color(0xFFCBD5E1)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Actualizar Contraseña",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            ),
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Footer
            Text(
                "© 2024 ManosPy S.A.\nTodos los derechos reservados.",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.sp
                ),
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            )
        }

        // Success message
        if (showSuccessMessage) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = white)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = successColor,
                            modifier = Modifier.size(64.dp)
                        )

                        Text(
                            "¡Contraseña actualizada!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = textDark,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            "Tu contraseña ha sido actualizada correctamente.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            color = textGray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                showSuccessMessage = false
                                onBack()
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primary)
                        ) {
                            Text("Entendido", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Simular actualización de contraseña
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(2000) // Simular llamada a API
            val success = onPasswordChanged(currentPassword, newPassword)
            if (success) {
                showSuccessMessage = true
            } else {
                errorMessage = "No se pudo actualizar la contraseña. Verifica tu contraseña actual."
                isLoading = false
            }
        }
    }
}

@Composable
private fun PasswordInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isVisible: Boolean,
    onVisibilityToggle: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF9CA3AF),
                modifier = Modifier.size(20.dp)
            )

            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = Color(0xFFD1D5DB)) },
                modifier = Modifier
                    .weight(1f),
                visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                singleLine = true
            )

            IconButton(
                onClick = onVisibilityToggle,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                    contentDescription = if (isVisible) "Ocultar" else "Mostrar",
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


@Composable
private fun ValidationCheckItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    isChecked: Boolean,
    successColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isChecked) successColor else Color(0xFFCBD5E1),
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            ),
            color = if (isChecked) Color(0xFF1F2937) else Color(0xFF9CA3AF)
        )
    }
}
