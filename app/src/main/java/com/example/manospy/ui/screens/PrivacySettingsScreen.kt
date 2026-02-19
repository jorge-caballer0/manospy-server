package com.example.manospy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacySettingsScreen(
    onBack: () -> Unit,
    navController: androidx.navigation.NavController? = null
) {
    val primary = Color(0xFF137FEC)
    val white = Color.White
    val borderGray = Color(0xFFE5E7EB)
    val infoBgColor = Color(0xFFF0F4FF)
    val infoTextColor = Color(0xFF64748B)

    var publicProfileEnabled by remember { mutableStateOf(true) }
    var exactLocationEnabled by remember { mutableStateOf(false) }
    var servicesHistoryEnabled by remember { mutableStateOf(true) }

    AppScaffold(
        title = "Privacidad del Perfil",
        onBackClick = { onBack() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp)
        ) {
            // CONFIGURACIÓN DE VISIBILIDAD
            Text(
                "CONFIGURACIÓN DE VISIBILIDAD",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .shadow(2.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = white),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderGray)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    PrivacyToggleItem(
                        icon = Icons.Default.AccountCircle,
                        title = "Perfil Público",
                        description = "Permite que los profesionales vean tu perfil antes de confirmar la reserva.",
                        checked = publicProfileEnabled,
                        onCheckedChange = { publicProfileEnabled = it },
                        primary = primary
                    )

                    Divider(color = borderGray)

                    PrivacyToggleItem(
                        icon = Icons.Default.LocationOn,
                        title = "Mostrar Mi Ubicación Exacta",
                        description = "Solo muestra tu área general hasta que la reserva sea confirmada.",
                        checked = exactLocationEnabled,
                        onCheckedChange = { exactLocationEnabled = it },
                        primary = primary
                    )

                    Divider(color = borderGray)

                    PrivacyToggleItem(
                        icon = Icons.Default.History,
                        title = "Compartir Historial de Servicios",
                        description = "Permite a los profesionales ver tus calificaciones previas y experiencia en la plataforma.",
                        checked = servicesHistoryEnabled,
                        onCheckedChange = { servicesHistoryEnabled = it },
                        primary = primary,
                        isLast = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Info Box
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .align(Alignment.CenterHorizontally)
                    .shadow(2.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = infoBgColor),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0EAFF))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Surface(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(2.dp, RoundedCornerShape(12.dp)),
                        shape = RoundedCornerShape(12.dp),
                        color = primary
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = white,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Text(
                        text = "Tu privacidad es nuestra prioridad. Solo compartimos la información necesaria para garantizar un servicio de calidad y seguro para ambas partes.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        ),
                        color = infoTextColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Footer
            Text(
                "© 2024 ManosPy S.A.\nTodos los derechos reservados.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                color = Color(0xFF9CA3AF),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
            )
        }
    }
}

@Composable
private fun PrivacyToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    primary: Color,
    isLast: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFFF0F4FF)) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(imageVector = icon, contentDescription = null, tint = primary, modifier = Modifier.size(20.dp))
                    }
                }

                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, fontSize = 15.sp),
                    color = Color(0xFF1F2937)
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFCBD5E1)
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, lineHeight = 16.sp),
            color = Color(0xFF64748B),
            modifier = Modifier.paddingFromBaseline(top = 0.dp).padding(start = 52.dp)
        )
    }
}
