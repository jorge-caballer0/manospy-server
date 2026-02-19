package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationPreferencesScreen(onBack: () -> Unit) {
    val brandBlue = Color(0xFF0EA5E9)
    val backgroundLight = Color(0xFFF8FAFC)
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)
    val labelColor = Color(0xFF94A3B8)

    AppScaffold(
        title = "Notification Preferences",
        onBackClick = onBack,
        hasBottomNavigation = false
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                Text(
                    text = "MANAGE HOW YOU STAY INFORMED",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, top = 24.dp, bottom = 12.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = textSecondary,
                        letterSpacing = 1.sp
                    )
                )
            }

            // Section: New Service Requests
            item {
                NotificationCategoryCard(
                    title = "New Service Requests",
                    icon = Icons.Outlined.NotificationsActive,
                    brandBlue = brandBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    emailEnabled = true,
                    pushEnabled = true
                )
            }

            // Section: Client Messages
            item {
                NotificationCategoryCard(
                    title = "Client Messages",
                    icon = Icons.Outlined.ChatBubble,
                    brandBlue = brandBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    emailEnabled = false,
                    pushEnabled = true
                )
            }

            // Section: Booking Updates
            item {
                NotificationCategoryCard(
                    title = "Booking Updates",
                    icon = Icons.Outlined.EventAvailable,
                    brandBlue = brandBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    emailEnabled = true,
                    pushEnabled = true
                )
            }

            // Section: Marketing Alerts
            item {
                NotificationCategoryCard(
                    title = "Marketing Alerts",
                    icon = Icons.Outlined.Campaign,
                    brandBlue = brandBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    emailEnabled = false,
                    pushEnabled = false
                )
            }

            // Footer
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Column(
                    modifier = Modifier.padding(horizontal = 40.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Some essential account notifications cannot be disabled to ensure you receive critical updates regarding your account security and service performance.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = labelColor,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "MANOSPY Professional v2.4.1",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCBD5E1))
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCategoryCard(
    title: String,
    icon: ImageVector,
    brandBlue: Color,
    textPrimary: Color,
    textSecondary: Color,
    emailEnabled: Boolean,
    pushEnabled: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(brandBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, modifier = Modifier.size(22.dp), tint = brandBlue)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            NotificationToggleRow(
                label = "Email Notifications",
                isEnabled = emailEnabled,
                brandBlue = brandBlue,
                textSecondary = textSecondary
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            NotificationToggleRow(
                label = "Push Notifications",
                isEnabled = pushEnabled,
                brandBlue = brandBlue,
                textSecondary = textSecondary
            )
        }
    }
}

@Composable
fun NotificationToggleRow(
    label: String,
    isEnabled: Boolean,
    brandBlue: Color,
    textSecondary: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(color = textSecondary)
        )
        Switch(
            checked = isEnabled,
            onCheckedChange = { },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = brandBlue,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE2E8F0),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
