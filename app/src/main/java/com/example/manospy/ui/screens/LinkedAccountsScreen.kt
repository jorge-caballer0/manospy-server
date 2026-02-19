package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Payments
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
fun LinkedAccountsScreen(onBack: () -> Unit) {
    val brandBlue = Color(0xFF0EA5E9)
    val backgroundLight = Color(0xFFF8FAFC)
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)
    val labelColor = Color(0xFF94A3B8)

    AppScaffold(
        title = "Linked Accounts",
        onBackClick = onBack,
        hasBottomNavigation = false
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Section: SOCIAL CONNECTIONS
            item {
                LinkedSectionLabel("SOCIAL CONNECTIONS")
                AccountCardPremium(
                    title = "Google",
                    subtitle = "j.wilson@gmail.com",
                    icon = Icons.Default.AccountCircle, // Placeholder for Google
                    actionText = "Manage",
                    isLinked = true,
                    brandBlue = brandBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                AccountCardPremium(
                    title = "Apple ID",
                    subtitle = "Not connected",
                    icon = Icons.Default.AccountCircle, // Using generic person icon as fallback for Apple
                    actionText = "Connect",
                    isLinked = false,
                    brandBlue = brandBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Section: PAYOUT METHODS
            item {
                Spacer(modifier = Modifier.height(24.dp))
                LinkedSectionLabel("PAYOUT METHODS")
                AccountCardPremium(
                    title = "Bank Account",
                    subtitle = "Chase Bank • • • • 4291",
                    icon = Icons.Outlined.AccountBalance,
                    actionText = "Edit",
                    isLinked = true,
                    brandBlue = brandBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
                Spacer(modifier = Modifier.height(12.dp))
                AccountCardPremium(
                    title = "PayPal",
                    subtitle = "Not connected",
                    icon = Icons.Outlined.Payments,
                    actionText = "Connect",
                    isLinked = false,
                    brandBlue = brandBlue,
                    textPrimary = textPrimary,
                    textSecondary = textSecondary
                )
            }

            // Info Notice
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFEFF6FF)
                ) {
                    Row(modifier = Modifier.padding(16.dp)) {
                        Icon(
                            Icons.Outlined.Info,
                            null,
                            tint = brandBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Linked accounts are used for quick login and receiving payouts. Your bank details are encrypted and never stored directly on our servers.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = textSecondary,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }

            // Footer
            item {
                Spacer(modifier = Modifier.height(48.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "MANOSPY Premium v2.4.1",
                        style = MaterialTheme.typography.labelSmall.copy(color = labelColor)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Lock,
                            null,
                            tint = labelColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "End-to-end encrypted connection",
                            style = MaterialTheme.typography.labelSmall.copy(color = labelColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LinkedSectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 24.dp, bottom = 12.dp),
        style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            letterSpacing = 1.sp
        )
    )
}

@Composable
fun AccountCardPremium(
    title: String,
    subtitle: String,
    icon: ImageVector,
    actionText: String,
    isLinked: Boolean,
    brandBlue: Color,
    textPrimary: Color,
    textSecondary: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, modifier = Modifier.size(24.dp), tint = textPrimary)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(color = textSecondary)
                )
            }
            if (isLinked) {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = actionText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = textSecondary
                        )
                    )
                }
            } else {
                Button(
                    onClick = { },
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = brandBlue),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Text(
                        text = actionText,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }
            }
        }
    }
}
