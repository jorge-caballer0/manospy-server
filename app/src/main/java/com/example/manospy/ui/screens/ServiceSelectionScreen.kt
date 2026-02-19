package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.manospy.R
import com.example.manospy.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)  

@Composable
fun ServiceSelectionScreen(navController: NavController) {
    val primaryCyan = Color(0xFF00E5D1)
    val backgroundColor = Color(0xFFF9FAFB)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)

    val searchQuery = remember { mutableStateOf("") }

    val services = listOf(
        ServiceItem("Masajes", "Relajación total en la comodidad de tu hogar", android.R.drawable.ic_menu_info_details
        ),
        ServiceItem("Plomería", "Reparación de fugas y cañerías", android.R.drawable.ic_menu_info_details
        ),
        ServiceItem("Electricidad", "Instalaciones y mantenimiento eléctrico", android.R.drawable.ic_menu_info_details
        ),
        ServiceItem("Limpieza", "Servicio profesional para tu hogar", android.R.drawable.ic_menu_info_details
        ),
        ServiceItem("Aire Acondicionado", "Carga de gas e instalación", android.R.drawable.ic_menu_info_details
        ),
        ServiceItem("Carpintería", "Reparación de muebles y estructuras", android.R.drawable.ic_menu_info_details
        )
    )

    AppScaffold(
        title = "¿Qué servicio necesitas?",
        onBackClick = null,
        hasBottomNavigation = false
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar servicios...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryCyan,
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                )
            )

            // Lista de servicios
            services.filter {
                it.title.contains(searchQuery.value, ignoreCase = true)
            }.forEach { service ->
                ServiceCard(service, primaryCyan, textPrimary, textSecondary) {
                    navController.navigate("createReservation/${service.title}")
                }
            }
        }
    }
}

@Composable
fun ServiceCard(
    service: ServiceItem,
    primaryCyan: Color,
    textPrimary: Color,
    textSecondary: Color,
    onReserveClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = service.iconRes),
                contentDescription = null,
                tint = primaryCyan,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(service.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = textPrimary)
                Text(service.description, fontSize = 14.sp, color = textSecondary)
            }

            Button(
                onClick = onReserveClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryCyan)
            ) {
                Text("Reservar", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

data class ServiceItem(
    val title: String,
    val description: String,
    val iconRes: Int
)
