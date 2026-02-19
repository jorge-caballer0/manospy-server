package com.example.manospy.ui.screens.client

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.manospy.data.model.Reservation
import com.example.manospy.ui.navigation.Screen
import com.example.manospy.ui.viewmodel.ServiceViewModel
import kotlinx.coroutines.launch

@Composable
fun ServiceCompletedReviewScreen(
    navController: NavController,
    reservationId: String,
    viewModel: ServiceViewModel = viewModel(),
    onBack: () -> Unit = { navController.navigate(Screen.ClientMain.route) }
) {
    // Colores del tema
    val primaryBlue = Color(0xFF0056D2)
    val primaryDark = Color(0xFF003A94)
    val bgLight = Color(0xFFF8FAFC)
    val bgMesh = Color(0xFFF0F7FF)
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val textGray = Color(0xFF94A3B8)
    val borderGray = Color(0xFFE2E8F0)
    val bgCard = Color.White
    val greenSuccess = Color(0xFF10B981)
    val amberStar = Color(0xFFFBBF24)
    val grayLight = Color(0xFFE2E8F0)

    val scope = rememberCoroutineScope()

    // State variables
    var selectedRating by remember { mutableStateOf(4) }
    var reviewText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Animación de rotación del icono
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        rotation.animateTo(
            targetValue = 3f,
            animationSpec = spring(
                dampingRatio = 0.5f,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    // Cargar datos de la reservación
    LaunchedEffect(reservationId) {
        viewModel.fetchReservationDetail(reservationId)
    }

    val reservationDetail by viewModel.reservationDetail.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgMesh)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header with Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = textSecondary
                    )
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cerrar",
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(modifier = Modifier.weight(1f))
            }

            // Main Content
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Success Header
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Animated Success Icon
                        Box(
                            modifier = Modifier.size(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Large blue background with rotation
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(28.dp))
                                    .background(primaryBlue)
                                    .rotate(rotation.value),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Task,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(48.dp)
                                        .rotate(-rotation.value)
                                )
                            }

                            // Green check badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(greenSuccess)
                                    .border(4.dp, Color.White, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Title
                        Text(
                            "¡Servicio Finalizado!",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryDark,
                            textAlign = TextAlign.Center,
                            letterSpacing = (-0.5).sp
                        )

                        // Subtitle
                        Text(
                            reservationDetail?.professionalName?.let {
                                "Califica tu experiencia con $it"
                            } ?: "Califica tu experiencia con el profesional",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = textSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }

                // Professional Card with Review
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .border(1.dp, borderGray.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                        color = bgCard,
                        shadowElevation = 8.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Professional Photo
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, borderGray.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                // Mostrar icono por defecto (no hay URL de foto disponible)
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(borderGray),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Task,
                                        contentDescription = null,
                                        tint = textGray,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                            }

                            // Professional Name
                            Text(
                                reservationDetail?.professionalName ?: "Profesional",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )

                            // Star Rating
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                repeat(5) { index ->
                                    IconButton(
                                        onClick = { selectedRating = index + 1 },
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape),
                                        colors = IconButtonDefaults.iconButtonColors(
                                            contentColor = if (index < selectedRating) amberStar else grayLight
                                        )
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            modifier = Modifier.size(44.sp.value.dp)
                                        )
                                    }
                                }
                            }

                            // Review TextArea
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(1.dp, borderGray, RoundedCornerShape(16.dp)),
                                color = Color(0xFFF1F5F9)
                            ) {
                                TextField(
                                    value = reviewText,
                                    onValueChange = { reviewText = it },
                                    placeholder = {
                                        Text(
                                            "Escribe tu reseña (opcional)...",
                                            fontSize = 14.sp,
                                            color = textGray
                                        )
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 100.dp)
                                        .border(1.dp, Color.Transparent, RoundedCornerShape(16.dp)),
                                    colors = TextFieldDefaults.colors(
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        focusedTextColor = textPrimary,
                                        unfocusedTextColor = textPrimary
                                    ),
                                    textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                    maxLines = 4
                                )
                            }
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }

            // Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Submit Review Button
                Button(
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                viewModel.submitReview(
                                    reservationId = reservationId,
                                    rating = selectedRating,
                                    reviewText = reviewText
                                )
                                onBack()
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 4.dp
                    ),
                    enabled = !isLoading
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Enviar Reseña",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }

                // Skip Button
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = ButtonDefaults.buttonElevation(0.dp)
                ) {
                    Text(
                        "Omitir",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textGray
                    )
                }
            }
        }

        // Footer
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                borderGray,
                                Color.Transparent
                            )
                        )
                    )
            )

            Text(
                "Transacción #MP-${reservationId.takeLast(5).uppercase()}",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = textGray,
                letterSpacing = 0.5.sp,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}