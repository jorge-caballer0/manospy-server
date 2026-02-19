package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AccessTime
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
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors
import com.example.manospy.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalOffersScreen(
    navController: NavController,
    serviceViewModel: ServiceViewModel? = null
) {
    val primaryBlue = Color(0xFF0056D2)
    val bgLight = Color(0xFFF0F7FF)
    val cardBg = Color.White
    val textPrimary = Color(0xFF1F2937)
    val textSecondary = Color(0xFF6B7280)

    val offers = serviceViewModel?.getOffers() ?: emptyList()

    AppScaffold(
        title = "Mis Ofertas",
        headerBackgroundColor = Color(0xFF0056D2),
        headerTextColor = Color.White,
        onBackClick = { navController.popBackStack() },
        hasBottomNavigation = false,
        modifier = Modifier.background(bgLight)
    ) { innerPadding ->
        if (offers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        "Sin ofertas aún",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary
                    )
                    Text(
                        "Las ofertas que recibas aparecerán aquí",
                        fontSize = 14.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(offers, key = { it.id }) { offer ->
                    OfferCard(
                        clientName = offer.clientName,
                        service = offer.serviceName,
                        budget = offer.budget,
                        rating = offer.clientRating,
                        reviewCount = offer.reviewCount,
                        urgency = offer.urgency,
                        onClick = {
                            navController.let {
                                serviceViewModel?.setSelectedOffer(offer)
                                it.navigate(Screen.ProfessionalOfferDetail.route)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OfferCard(
    clientName: String,
    service: String,
    budget: String,
    rating: Double,
    reviewCount: Int,
    urgency: String,
    onClick: () -> Unit
) {
    val primaryBlue = Color(0xFF0056D2)
    val cardBg = Color.White
    val textPrimary = Color(0xFF1F2937)
    val textSecondary = Color(0xFF6B7280)
    val borderColor = Color(0xFFE5E7EB)
    val amberWarning = Color(0xFFF59E0B)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = cardBg,
        shadowElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Nombre del cliente + Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        clientName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        service,
                        fontSize = 13.sp,
                        color = textSecondary
                    )
                }
                // Rating
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = amberWarning,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        "$rating",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    )
                    Text(
                        "($reviewCount)",
                        fontSize = 12.sp,
                        color = textSecondary
                    )
                }
            }

            // Budget
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Presupuesto",
                    fontSize = 13.sp,
                    color = textSecondary
                )
                Text(
                    budget,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
            }

            // Urgency badge
            if (urgency.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .align(Alignment.Start)
                        .clip(RoundedCornerShape(8.dp)),
                    color = when (urgency) {
                        "urgent" -> Color(0xFFFEE2E2)
                        "normal" -> Color(0xFFE0E7FF)
                        else -> Color(0xFFF0FDF4)
                    }
                ) {
                    Text(
                        urgency.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (urgency) {
                            "urgent" -> Color(0xFFDC2626)
                            "normal" -> primaryBlue
                            else -> Color(0xFF059669)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}
