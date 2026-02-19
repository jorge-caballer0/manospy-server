package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.data.model.ServiceRequest
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalRequestsScreen(navController: androidx.navigation.NavController? = null) {
    val brandBlue = Color(0xFF0EA5E9)
    val accentCyan = Color(0xFF06B6D4)
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)
    val bgLight = Color(0xFFF8FAFC)
    val cardBg = Color.White
    val pendingColor = Color(0xFFFB923C)
    val successGreen = Color(0xFF10B981)

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Nuevas", "Respondidas", "Rechazadas")

    AppScaffold(
        title = "Nuevas Solicitudes",
        onBackClick = { },
        hasBottomNavigation = false,
        modifier = Modifier.background(bgLight)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                divider = {
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color(0xFFE2E8F0)
                    )
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                tab,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        selectedContentColor = brandBlue,
                        unselectedContentColor = textSecondary
                    )
                }
            }

            // Content
            when (selectedTab) {
                0 -> NewRequestsList(emptyList(), brandBlue, textPrimary, textSecondary, pendingColor)
                1 -> EmptyStateTab("No hay solicitudes respondidas", "Comienza a responder a nuevas solicitudes")
                2 -> EmptyStateTab("No hay solicitudes rechazadas", "Tu historial de rechazos aparecerá aquí")
            }
        }
    }
}

@Composable
private fun NewRequestsList(
    requests: List<ServiceRequest>,
    brandBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    pendingColor: Color
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(requests) { request ->
            RequestCard(request, brandBlue, textPrimary, textSecondary, pendingColor)
        }
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun RequestCard(
    request: ServiceRequest,
    brandBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    pendingColor: Color
) {
    val cardBg = Color.White

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        color = cardBg
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header con cliente y badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(brandBlue, Color(0xFF06B6D4))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            (request.clientName?.firstOrNull() ?: "?").toString(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            request.clientName ?: "Cliente",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = textPrimary
                            )
                        )
                        Text(
                            "Cliente nuevo",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = textSecondary,
                                fontSize = 10.sp
                            )
                        )
                    }
                }

                Badge(
                    containerColor = Color(0xFFFEF3C7),
                    contentColor = Color(0xFF92400E)
                ) {
                    Text(
                        "NUEVA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Divider(color = Color(0xFFE2E8F0))

            // Servicio y detalles
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ServiceDetailItem(Icons.Default.Handyman, "Servicio", request.category, textPrimary, textSecondary)
                ServiceDetailItem(Icons.Outlined.LocationOn, "Ubicación", request.location, textPrimary, textSecondary)
                ServiceDetailItem(Icons.Default.Description, "Descripción", request.description, textPrimary, textSecondary)
                ServiceDetailItem(Icons.Default.Schedule, "Fecha preferida", request.preferredDate, textPrimary, textSecondary)
            }

            Divider(color = Color(0xFFE2E8F0))

            // Botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {},
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = textSecondary)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Rechazar", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                }

                Button(
                    onClick = {},
                    modifier = Modifier
                        .weight(1.5f)
                        .height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Enviar Oferta", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ServiceDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String?,
    textPrimary: Color,
    textSecondary: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = Color(0xFF0EA5E9),
            modifier = Modifier.size(18.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = textSecondary,
                    fontSize = 10.sp
                )
            )
            Text(
                value ?: "No especificado",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
            )
        }
    }
}

@Composable
private fun EmptyStateTab(title: String, subtitle: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.Info,
            contentDescription = null,
            tint = Color(0xFFCBD5E1),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = Color(0xFF0F172A)
        )
        Text(
            subtitle,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFF64748B)
        )
    }
}
