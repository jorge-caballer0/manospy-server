package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.data.api.RetrofitClient
import com.example.manospy.data.local.SessionManager
import com.example.manospy.data.repository.AppRepository
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneVerifyScreen(onBack: () -> Unit, onVerificationSuccess: () -> Unit = {}) {
    val primary = Color(0xFF137FEC)
    val bgLight = Color(0xFFF6F7F8)
    val white = Color.White
    val textGray = Color(0xFF64748B)
    val textDark = Color(0xFF1F2937)
    
    var verificationCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) } // 1: request, 2: verify
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

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
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = textGray,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    "Verificar Teléfono",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    color = textDark,
                    modifier = Modifier.weight(1f)
                )

                Box(modifier = Modifier.size(44.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            when (step) {
                1 -> {
                    // Step 1: Request verification
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Verificar tu teléfono",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = textDark
                        )

                        Text(
                            "Te enviaremos un código de 6 dígitos a tu correo para verificar tu teléfono actual",
                            style = MaterialTheme.typography.bodySmall,
                            color = textGray
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                isLoading = true
                                scope.launch {
                                    try {
                                        val repo = AppRepository(RetrofitClient.apiService)
                                        when (val res = repo.requestPhoneVerification("+595", "email")) {
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
                                            else -> { isLoading = false }
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
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = white, strokeWidth = 2.dp)
                            } else {
                                Text("Enviar Código", color = white, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
                2 -> {
                    // Step 2: Verify code
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Ingresa el código",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = textDark
                        )

                        Text(
                            "Hemos enviado un código de 6 dígitos a tu correo",
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
                                cursorColor = primary
                            ),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = textDark
                            ),
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                isLoading = true
                                scope.launch {
                                    try {
                                        val repo = AppRepository(RetrofitClient.apiService)
                                        when (val res = repo.verifyPhoneWithCode(verificationId, verificationCode)) {
                                            is NetworkResult.Success -> {
                                                val session = SessionManager(context)
                                                val token = session.getToken() ?: ""
                                                session.saveUser(res.data, token)
                                                onVerificationSuccess()
                                                isLoading = false
                                            }
                                            is NetworkResult.Error -> {
                                                errorMessage = "Código inválido: ${res.message}"
                                                isLoading = false
                                            }
                                            else -> { isLoading = false }
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
                                Text("Verificar", color = white, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (errorMessage.isNotEmpty()) {
                            Text(
                                errorMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFDC2626),
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
