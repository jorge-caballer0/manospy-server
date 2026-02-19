package com.example.manospy.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

import com.example.manospy.data.model.ReservationStatus
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.ui.components.AppScaffold

@Composable
fun WaitingProfessionalScreen(
    reservationId: String,
    viewModel: ServiceViewModel,
    onProfessionalFound: () -> Unit,
    onCancel: () -> Unit
) {
    val brandBlue = Color(0xFF0EA5E9)
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)

    // Pulse animation for the radar effect
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    // Polling: check reservation status periodically
    LaunchedEffect(reservationId) {
        while (true) {
            viewModel.fetchReservationDetail(reservationId)
            delay(2000)
            val detail = viewModel.reservationDetail.value
            if (detail != null) {
                if (detail.status == ReservationStatus.ACCEPTED || detail.status.name.equals("ACCEPTED", ignoreCase = true)) {
                    onProfessionalFound()
                    break
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Radar Animation
            Box(
                modifier = Modifier.size(200.dp),
                contentAlignment = Alignment.Center
            ) {
                // Animated pulse rings
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseScale)
                        .alpha(pulseAlpha)
                        .background(brandBlue.copy(alpha = 0.3f), CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .scale(pulseScale * 0.7f)
                        .alpha(pulseAlpha)
                        .background(brandBlue.copy(alpha = 0.2f), CircleShape)
                )
                
                // Central Icon
                Surface(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    color = Color(0xFFF0F9FF),
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = null,
                            tint = brandBlue,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = "Finding your professional",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "We are matching your request with the best top-tier professionals near you.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = textSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Nearby indicators (visual only)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(0.7f)
            ) {
                Icon(Icons.Default.PersonSearch, null, tint = brandBlue, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "3 professionals active nearby",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = brandBlue,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.height(80.dp))

            // Cancel Button
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = textSecondary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cancel Request", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
