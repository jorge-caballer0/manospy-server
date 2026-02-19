package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRatingScreen(
    professionalName: String,
    reservationId: String,
    onSubmit: (rating: Int, comment: String) -> Unit,
    onBack: () -> Unit
) {
    var selectedRating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    AppScaffold(
        title = "Calificar servicio",
        onBackClick = onBack,
        hasBottomNavigation = false
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header message
            Text(
                text = "¿Cómo fue tu experiencia con $professionalName?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Star rating
            Text(
                text = "Calificación",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                repeat(5) { index ->
                    IconButton(
                        onClick = { selectedRating = index + 1 },
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(
                            imageVector = if (index < selectedRating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Estrella ${index + 1}",
                            modifier = Modifier.size(40.dp),
                            tint = if (index < selectedRating) Color(0xFFFFAC4F) else Color(0xFFCBD5E1)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Comments section
            Text(
                text = "Comentarios (opcional)",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF334155)
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                placeholder = {
                    Text(
                        "Cuéntanos qué te pareció el servicio...",
                        color = Color(0xFF94A3B8)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                enabled = !isSubmitting,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF334155)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF0056D2),
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                )
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Submit button
            Button(
                onClick = {
                    isSubmitting = true
                    onSubmit(selectedRating, comment)
                },
                enabled = selectedRating > 0 && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0056D2),
                    disabledContainerColor = Color(0xFFCBD5E1)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Enviando...")
                } else {
                    Text(
                        text = "Enviar calificación",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Skip button
            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Saltar por ahora",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = Color(0xFF0056D2),
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}
