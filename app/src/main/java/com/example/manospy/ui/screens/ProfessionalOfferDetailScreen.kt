package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors
import androidx.compose.runtime.collectAsState
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.ui.viewmodel.ProfessionalOffer
import com.example.manospy.ui.navigation.Screen
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalOfferDetailScreen(
    navController: NavController? = null,
    viewModel: com.example.manospy.ui.viewmodel.ServiceViewModel? = null
) {
    val selectedOffer by (viewModel?.selectedOffer?.collectAsState() ?: mutableStateOf<ProfessionalOffer?>(null))
    val offer = selectedOffer
    if (offer == null) {
        AppScaffold(
            title = "Detalle de Oferta",
            onBackClick = { navController?.popBackStack() }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("Oferta no disponible")
            }
        }
        return
    }

    val primaryBlue = Color(0xFF137FEC)
    val bgLight = Color(0xFFF6F7F8)
    val textPrimary = Color(0xFF0D141B)
    val textSecondary = Color(0xFF64748B)
    val cardBg = Color.White
    val borderColor = Color(0xFFE2E8F0)
    val accentGreen = Color(0xFF10B981)

    AppScaffold(
        title = "Detalle de Oferta",
        headerBackgroundColor = Color(0xFF0056D2),
        headerTextColor = Color.White,
        onBackClick = { navController?.popBackStack() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(bgLight)
                .verticalScroll(rememberScrollState())
        ) {
            // Header Card with Avatar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
                    .shadow(4.dp),
                color = primaryBlue
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Avatar
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = CircleShape,
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(3.dp, Color.White)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    // Professional Name
                    Text(
                        text = offer.clientName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    // Service
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(20.dp)),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = offer.serviceName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }

                    // Rating
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(5) { index ->
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < offer.clientRating.toInt()) Color(0xFFFCD34D) else Color.White.copy(
                                    alpha = 0.3f
                                ),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "${offer.clientRating} (${offer.reviewCount})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            // Content Cards
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Budget Card
                OfferDetailCard(
                    title = "Presupuesto",
                    icon = Icons.Default.AttachMoney,
                    content = offer.budget,
                    contentColor = accentGreen,
                    cardBg = cardBg,
                    borderColor = borderColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                // Service Details
                OfferDetailCard(
                    title = "Servicio",
                    icon = Icons.Default.Build,
                    content = offer.serviceName,
                    cardBg = cardBg,
                    borderColor = borderColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                // Urgency
                OfferDetailCard(
                    title = "Urgencia",
                    icon = Icons.Default.Schedule,
                    content = when (offer.urgency.lowercase()) {
                        "high" -> "Alta"
                        "medium" -> "Media"
                        "low" -> "Baja"
                        else -> offer.urgency
                    },
                    contentColor = when (offer.urgency.lowercase()) {
                        "high" -> Color(0xFFEF4444)
                        "medium" -> Color(0xFFFB923C)
                        else -> textSecondary
                    },
                    cardBg = cardBg,
                    borderColor = borderColor,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )

                // About section
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
                    color = cardBg
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                color = primaryBlue.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.Info,
                                        contentDescription = null,
                                        tint = primaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Text(
                                "Información del Profesional",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        }
                        Text(
                            "Profesional certificado con experiencia en ${offer.serviceName.lowercase()}. Disponible para trabajos de urgencia.",
                            fontSize = 13.sp,
                            color = textSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }

                // Action Buttons
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val coroutineScope = rememberCoroutineScope()
                    Button(
                        onClick = {
                            // Crear chat en backend y navegar al chat con chatId
                            coroutineScope.launch {
                                val vm = viewModel
                                if (vm != null) {
                                    val result = vm.createChatSync(offer.id)
                                    if (result is com.example.manospy.util.NetworkResult.Success) {
                                        val chatId = (result.data as? com.example.manospy.data.model.CreateChatResponse)?.chatId ?: ""
                                        if (chatId.isNotEmpty()) {
                                            navController?.navigate(Screen.Chat.createRoute(chatId))
                                        } else {
                                            navController?.navigate(Screen.Chat.createRoute(offer.id))
                                        }
                                    } else {
                                        // Fallback: navegar con offer.id para compatibilidad
                                        navController?.navigate(Screen.Chat.createRoute(offer.id))
                                    }
                                } else {
                                    navController?.navigate(Screen.Chat.createRoute(offer.id))
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ChatBubble,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                "Escribir al Profesional",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    OutlinedButton(
                        onClick = { navController?.popBackStack() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(2.dp, primaryBlue)
                    ) {
                        Text(
                            "Volver",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryBlue
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OfferDetailCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: String,
    contentColor: Color = Color(0xFF137FEC),
    cardBg: Color,
    borderColor: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp)),
        color = cardBg
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    color = contentColor.copy(alpha = 0.1f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textSecondary,
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                content,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }
    }
}
