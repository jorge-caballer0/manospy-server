package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalRequestDetailScreen(
    navController: NavController? = null,
    requestId: String? = null
) {
    val textPrimary = Color(0xFF1F2937)
    val textSecondary = Color(0xFF6B7280)
    val bgLight = Color(0xFFF0F7FF)
    val cardBg = Color.White
    val accentGreen = Color(0xFF10B981)
    val pendingColor = Color(0xFFFB923C)

    // Placeholder data - replace with actual data from ViewModel
    val requestData = mapOf(
        "clientName" to "Juan Pérez",
        "service" to "Reparación de Electricidad",
        "budget" to "$500 - $800",
        "location" to "Calle Principal 123, Apto 4B",
        "date" to LocalDateTime.now(),
        "description" to "Necesito reparar los enchufes de la sala. Algunos no funcionan correctamente.",
        "status" to "pending",
        "clientRating" to 4.8,
        "reviews" to 47
    )

    AppScaffold(
        title = "Detalles de Solicitud",
        onBackClick = { navController?.popBackStack() },
        hasBottomNavigation = false,
        modifier = Modifier.background(bgLight)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Client Info Card
            item {
                ClientInfoCard(
                    clientName = requestData["clientName"] as String,
                    rating = (requestData["clientRating"] as Double),
                    reviews = (requestData["reviews"] as Int),
                    cardBg = cardBg,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Service Details Card
            item {
                ServiceDetailsCard(
                    service = requestData["service"] as String,
                    budget = requestData["budget"] as String,
                    cardBg = cardBg,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Location Card
            item {
                LocationCard(
                    location = requestData["location"] as String,
                    cardBg = cardBg,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Date Time Card
            item {
                DateTimeCard(
                    dateTime = requestData["date"] as LocalDateTime,
                    cardBg = cardBg,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Description Card
            item {
                DescriptionCard(
                    description = requestData["description"] as String,
                    cardBg = cardBg,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Action Buttons
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController?.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            Color(0xFFE5E7EB)
                        )
                    ) {
                        Text(
                            "Rechazar",
                            color = textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = { navController?.popBackStack() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = accentGreen
                        )
                    ) {
                        Text(
                            "Aceptar",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun ClientInfoCard(
    clientName: String,
    rating: Double,
    reviews: Int,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp)),
                color = Color(0xFFE0E7FF)
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    tint = AppColors.PrimaryBlue
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    clientName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (index < rating.toInt()) Color(0xFFFBC02D) else Color(0xFFE5E7EB)
                        )
                    }
                    Text(
                        "$rating ($reviews)",
                        fontSize = 13.sp,
                        color = textSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun ServiceDetailsCard(
    service: String,
    budget: String,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color(0xFFE0E7FF)
                ) {
                    Icon(
                        Icons.Default.Build,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        tint = AppColors.PrimaryBlue
                    )
                }
                Column {
                    Text(
                        "Servicio",
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                    Text(
                        service,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    color = Color(0xFFFFECCC)
                ) {
                    Icon(
                        Icons.Default.AttachMoney,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(6.dp),
                        tint = Color(0xFFFB923C)
                    )
                }
                Column {
                    Text(
                        "Presupuesto",
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                    Text(
                        budget,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationCard(
    location: String,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = AppColors.PrimaryBlue
                )
                Text(
                    "Ubicación",
                    fontSize = 13.sp,
                    color = textSecondary
                )
            }
            Text(
                location,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )
        }
    }
}

@Composable
private fun DateTimeCard(
    dateTime: LocalDateTime,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm")
    val formattedDate = dateTime.format(formatter)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = AppColors.PrimaryBlue
                )
                Text(
                    "Fecha y Hora",
                    fontSize = 13.sp,
                    color = textSecondary
                )
            }
            Text(
                formattedDate,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = textPrimary
            )
        }
    }
}

@Composable
private fun DescriptionCard(
    description: String,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Descripción",
                fontSize = 13.sp,
                color = textSecondary
            )
            Text(
                description,
                fontSize = 14.sp,
                color = textPrimary,
                lineHeight = 20.sp
            )
        }
    }
}
