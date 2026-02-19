package com.example.manospy.ui.screens.client

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.manospy.ui.navigation.Screen
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.launch
import com.example.manospy.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationAcceptedScreen(
    navController: NavController,
    reservationId: String,
    viewModel: ServiceViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val reservationDetail by viewModel.reservationDetail.collectAsState()
    val reputation by viewModel.professionalReputation.collectAsState()

    // Colores del tema
    val primaryBlue = Color(0xFF0056D2)
    val primaryDark = Color(0xFF003A94)
    val bgMesh = Color(0xFFF8FAFC)
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val textGray = Color(0xFF94A3B8)
    val borderGray = Color(0xFFE2E8F0)
    val bgCard = Color.White
    val greenSuccess = Color(0xFF10B981)
    val amberStar = Color(0xFFFBBF24)

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

    LaunchedEffect(reservationDetail) {
        reservationDetail?.let { reservation ->
            viewModel.fetchProfessionalReputation(reservation.professionalId)
        }
    }

    AppScaffold(title = "Reserva Aceptada", onBackClick = { navController.popBackStack() }) { paddingValues ->
        val padding = paddingValues
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgMesh)
                .padding(padding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Main Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // Animated Success Icon
                    Box(
                        modifier = Modifier
                            .size(80.dp),
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
                                Icons.Default.VerifiedUser,
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
                        "¡Solicitud Aceptada!",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryDark,
                        letterSpacing = (-0.5).sp
                    )

                    // Subtitle
                    Text(
                        reservationDetail?.professionalName?.let {
                            "$it ha aceptado tu pedido de servicio"
                        } ?: "El profesional ha aceptado tu pedido de servicio",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = textSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Professional Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .align(Alignment.CenterHorizontally)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, borderGray.copy(alpha = 0.5f), RoundedCornerShape(24.dp)),
                    color = bgCard,
                    shadowElevation = 8.dp
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Confirmed Badge
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .clip(RoundedCornerShape(20.dp)),
                                color = greenSuccess.copy(alpha = 0.15f)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(greenSuccess)
                                    )
                                    Text(
                                        "CONFIRMADO",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = greenSuccess,
                                        letterSpacing = 0.5.sp
                                    )
                                }
                            }

                            // Professional Photo
                            Box(
                                modifier = Modifier
                                    .size(96.dp)
                                    .clip(CircleShape)
                                    .border(4.dp, borderGray.copy(alpha = 0.3f), CircleShape)
                            ) {
                                AsyncImage(
                                    model = "https://via.placeholder.com/96", // Placeholder
                                    contentDescription = "Avatar de ${reservationDetail?.professionalName}",
                                    modifier = Modifier.fillMaxSize()
                                )

                                // Star badge
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .offset(x = 8.dp, y = 8.dp)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(primaryBlue)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Grade,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            // Professional Name
                            Text(
                                reservationDetail?.professionalName ?: "Profesional",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )

                            // Specialty
                            Text(
                                reservationDetail?.serviceName?.replaceFirstChar { it.uppercase() }
                                    ?.replace("_", " ") ?: "Especialista",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryBlue
                            )

                            // Stats
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .drawBehind {
                                        val strokeWidth = 1.dp.toPx()
                                        drawLine(
                                            color = borderGray.copy(alpha = 0.5f),
                                            start = Offset(0f, 0f),
                                            end = Offset(size.width, 0f),
                                            strokeWidth = strokeWidth
                                        )
                                    }
                                    .padding(top = 12.dp),
                                horizontalArrangement = Arrangement.spacedBy(0.dp)
                            ) {
                                // Rating
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "CALIFICACIÓN",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textGray,
                                        letterSpacing = 0.5.sp
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            when (val rep = reputation) {
                                                is NetworkResult.Success -> "%.1f".format(rep.data.rating)
                                                else -> "4.9"
                                            },
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = textPrimary
                                        )
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = amberStar,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                // Divider
                                Box(
                                    modifier = Modifier
                                        .width(1.dp)
                                        .height(40.dp)
                                        .background(borderGray.copy(alpha = 0.5f))
                                )

                                // Jobs
                                Column(
                                    modifier = Modifier.weight(1f),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "TRABAJOS",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textGray,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        when (val rep = reputation) {
                                            is NetworkResult.Success -> "+${rep.data.totalReviews}"
                                            else -> "+150"
                                        },
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = textPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Accept and Chat Button
                    Button(
                        onClick = { navController.navigate(Screen.Chat.createRoute(reservationId)) },
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
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.ChatBubble,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Aceptar y Chatear",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                        }
                    }

                    // Continue Searching Button
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                viewModel.updateReservationStatus(reservationId, "CANCELLED")
                                navController.navigate(Screen.ReservationPending.createRoute(reservationDetail?.serviceRequestId ?: "")) {
                                    popUpTo(Screen.ClientMain.route) { inclusive = false }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(2.dp, primaryBlue.copy(alpha = 0.3f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = primaryBlue
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Seguir Buscando",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryBlue
                            )
                        }
                    }

                    // Back Button
                    TextButton(
                        onClick = { navController.navigate(Screen.ClientMain.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.textButtonColors(contentColor = textSecondary)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = textSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Atrás",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
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
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    borderGray,
                                    Color.Transparent
                                )
                            )
                        )
                )

                Text(
                    "Reserva #MP-${reservationId.takeLast(5).uppercase()}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = textGray,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
