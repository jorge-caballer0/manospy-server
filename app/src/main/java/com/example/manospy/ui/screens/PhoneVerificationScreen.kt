package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.data.api.RetrofitClient
import com.example.manospy.data.local.SessionManager
import com.example.manospy.data.repository.AppRepository
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.launch
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneVerificationScreen(
    onBack: () -> Unit,
    onVerificationSuccess: (newPhoneNumber: String) -> Unit = {}
) {
    val primary = Color(0xFF137FEC)
    val bgLight = Color(0xFFF6F7F8)
    val white = Color.White
    val textGray = Color(0xFF64748B)
    val textDark = Color(0xFF1F2937)
    val borderGray = Color(0xFFE5E7EB)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var step by remember { mutableStateOf(1) } // 1: Número nuevo, 2: Código
    var newPhoneNumber by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var verificationId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgLight)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .background(white, RoundedCornerShape(12.dp))
                        .shadow(2.dp, RoundedCornerShape(12.dp), clip = true)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = textGray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    when (step) {
                        1 -> "Cambiar Teléfono"
                        2 -> "Método de Verificación"
                        else -> "Ingresar Código"
                    },
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = textDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Box(modifier = Modifier.size(44.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Progreso
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .background(
                                if (index < step) primary else Color(0xFFE5E7EB),
                                RoundedCornerShape(2.dp)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Contenido según paso
            when (step) {
                1 -> {
                    // Step 1: Ingrese el nuevo número
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Ingrese su nuevo número de teléfono",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = textDark
                        )

                        Text(
                            "Necesitamos verificar tu número para cambiar el actual",
                            style = MaterialTheme.typography.bodySmall,
                            color = textGray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = newPhoneNumber,
                            onValueChange = { newPhoneNumber = it },
                            placeholder = { Text("981 123456") },
                            leadingIcon = {
                                Text(
                                    "+595",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = textGray,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            },
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
                            textStyle = MaterialTheme.typography.bodyMedium.copy(color = textDark),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (newPhoneNumber.isEmpty()) {
                                    errorMessage = "Por favor ingrese un número válido"
                                } else {
                                    isLoading = true
                                    scope.launch {
                                        try {
                                            val repo = AppRepository(RetrofitClient.apiService)
                                            when (val res = repo.requestPhoneVerification("+595${newPhoneNumber}", "email")) {
                                                is NetworkResult.Success -> {
                                                    verificationId = res.data.verificationId ?: ""
                                                    step = 2
                                                    errorMessage = ""
                                                    isLoading = false
                                                }
                                                is NetworkResult.Error -> {
                                                    errorMessage = "Error: ${res.message}"
                                                    isLoading = false
                                                }
                                                else -> {
                                                    isLoading = false
                                                }
                                            }
                                        } catch (e: Exception) {
                                            errorMessage = "Error: ${e.message}"
                                            isLoading = false
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primary),
                            enabled = !isLoading && newPhoneNumber.isNotEmpty()
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = white, strokeWidth = 2.dp)
                            } else {
                                Text("Enviar Código", color = white, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                2 -> {
                    // Step 2: Ingresar código de verificación
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Ingrese el código de verificación",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = textDark
                        )

                        Text(
                            "Hemos enviado un código de 6 dígitos a tu correo electrónico",
                            style = MaterialTheme.typography.bodySmall,
                            color = textGray
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        TextField(
                            value = verificationCode,
                            onValueChange = { if (it.length <= 6) verificationCode = it },
                            placeholder = { Text("000000") },
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
                                color = textDark,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (isLoading) return@Button
                                isLoading = true
                                scope.launch {
                                    try {
                                        val repo = AppRepository(RetrofitClient.apiService)
                                        val session = SessionManager(context)
                                        val token = session.getToken() ?: ""
                                        when (val res = repo.verifyPhoneWithCode(verificationId, verificationCode)) {
                                            is NetworkResult.Success -> {
                                                session.saveUser(res.data, token)
                                                onVerificationSuccess("+595${newPhoneNumber}")
                                                isLoading = false
                                            }
                                            is NetworkResult.Error -> {
                                                errorMessage = "Código inválido: ${res.message}"
                                                isLoading = false
                                            }
                                            else -> {
                                                isLoading = false
                                            }
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Error: ${e.message}"
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primary),
                            enabled = !isLoading && verificationCode.length == 6
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = white, strokeWidth = 2.dp)
                            } else {
                                Text("Verificar Código", color = white, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { step = 1; errorMessage = "" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = androidx.compose.foundation.BorderStroke(2.dp, borderGray)
                        ) {
                            Text("Atrás", color = textDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                3 -> {
                    // Step 3: Ingresar códiho de verificación
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Ingrese el código de verificación",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = textDark
                        )

                        Text(
                            "Hemos enviado un código de 6 dígitos a tu correo electrónico",
                            style = MaterialTheme.typography.bodySmall,
                            color = textGray
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = verificationCode,
                            onValueChange = { if (it.length <= 6) verificationCode = it },
                            placeholder = { Text("000000") },
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
                                color = textDark,
                                textAlign = TextAlign.Center
                            ),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (verificationCode.length != 6) {
                                    errorMessage = "El código debe tener 6 dígitos"
                                    return@Button
                                }
                                if (isLoading) return@Button

                                isLoading = true
                                scope.launch {
                                    try {
                                        val repo = AppRepository(RetrofitClient.apiService)
                                        when (val res = repo.verifyPhoneWithCode(verificationId, verificationCode)) {
                                            is NetworkResult.Success -> {
                                                val session = SessionManager(context)
                                                val token = session.getToken() ?: ""
                                                session.saveUser(res.data, token)
                                                onVerificationSuccess("+595${newPhoneNumber}")
                                                isLoading = false
                                            }
                                            is NetworkResult.Error -> {
                                                errorMessage = "Código inválido: ${res.message}"
                                                isLoading = false
                                            }
                                            else -> {
                                                isLoading = false
                                            }
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Error: ${e.message}"
                                        isLoading = false
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primary),
                            enabled = !isLoading && verificationCode.length == 6
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = white, strokeWidth = 2.dp)
                            } else {
                                Text("Verificar Código", color = white, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = { step = 2; errorMessage = "" },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            border = androidx.compose.foundation.BorderStroke(2.dp, borderGray)
                        ) {
                            Text("Atrás", color = textDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
