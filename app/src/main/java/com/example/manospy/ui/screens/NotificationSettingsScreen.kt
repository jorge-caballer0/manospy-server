package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.manospy.ui.components.AppScaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    onBack: () -> Unit,
    navController: androidx.navigation.NavController? = null
) {
    val primary = Color(0xFF137FEC)
    val bgLight = Color(0xFFF6F7F8)
    val white = Color.White
    val textGray = Color(0xFF64748B)
    val textDark = Color(0xFF1F2937)
    val borderGray = Color(0xFFE5E7EB)

    var notificationsEnabled by remember { mutableStateOf(true) }
    var reservationAlerts by remember { mutableStateOf(true) }
    var messageAlerts by remember { mutableStateOf(true) }
    var promotionalAlerts by remember { mutableStateOf(false) }
    var soundEnabled by remember { mutableStateOf(true) }
    var vibrationEnabled by remember { mutableStateOf(true) }

    AppScaffold(
        title = "Notificaciones",
        onBackClick = { onBack() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Notificaciones Habilitadas
            Text(
                "GENERAL",
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
                SettingToggleItem(
                    icon = Icons.Default.Notifications,
                    title = "Habilitar Notificaciones",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it },
                    primary = primary,
                    isLast = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Tipos de Notificaciones
            Text(
                "TIPOS DE ALERTAS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (notificationsEnabled) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderGray)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SettingToggleItem(
                            icon = Icons.Default.EventNote,
                            title = "Alertas de Reservaciones",
                            checked = reservationAlerts,
                            onCheckedChange = { reservationAlerts = it },
                            primary = primary,
                            isLast = false
                        )

                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp),
                            color = borderGray
                        )

                        SettingToggleItem(
                            icon = Icons.Default.ChatBubble,
                            title = "Alertas de Mensajes",
                            checked = messageAlerts,
                            onCheckedChange = { messageAlerts = it },
                            primary = primary,
                            isLast = false
                        )

                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp),
                            color = borderGray
                        )

                        SettingToggleItem(
                            icon = Icons.Default.LocalOffer,
                            title = "Alertas Promocionales",
                            checked = promotionalAlerts,
                            onCheckedChange = { promotionalAlerts = it },
                            primary = primary,
                            isLast = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Sonido y Vibración
            Text(
                "PREFERENCIAS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                ),
                color = Color(0xFF9CA3AF),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (notificationsEnabled) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .shadow(2.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = white),
                    border = androidx.compose.foundation.BorderStroke(1.dp, borderGray)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SettingToggleItem(
                            icon = Icons.Default.VolumeUp,
                            title = "Sonido",
                            checked = soundEnabled,
                            onCheckedChange = { soundEnabled = it },
                            primary = primary,
                            isLast = false
                        )

                        Divider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp),
                            color = borderGray
                        )

                        SettingToggleItem(
                            icon = Icons.Default.Vibration,
                            title = "Vibración",
                            checked = vibrationEnabled,
                            onCheckedChange = { vibrationEnabled = it },
                            primary = primary,
                            isLast = true
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingToggleItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    primary: Color,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = primary,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = Color(0xFF1F2937)
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = primary,
                checkedTrackColor = primary.copy(alpha = 0.3f)
            )
        )
    }
}
