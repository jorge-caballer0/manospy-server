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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityPasswordScreen(onBack: () -> Unit) {
    val brandBlue = Color(0xFF0EA5E9)
    val backgroundLight = Color(0xFFF8FAFC)
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)
    val labelColor = Color(0xFF94A3B8)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Security & Password",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = backgroundLight,
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = Color.White,
                modifier = Modifier.navigationBarsPadding()
            ) {
                Box(modifier = Modifier.padding(24.dp)) {
                    Button(
                        onClick = { /* Update logic visual only */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                    ) {
                        Text(
                            "Update Security",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Section: CHANGE PASSWORD
            item {
                SecurityHeaderLabel("CHANGE PASSWORD")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        PasswordField(label = "CURRENT PASSWORD", placeholder = "••••••••")
                        Spacer(modifier = Modifier.height(20.dp))
                        PasswordField(label = "NEW PASSWORD", placeholder = "Min. 8 characters")
                        Spacer(modifier = Modifier.height(20.dp))
                        PasswordField(label = "CONFIRM NEW PASSWORD", placeholder = "Confirm password")
                    }
                }
            }

            // Section: ENHANCED SECURITY
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SecurityHeaderLabel("ENHANCED SECURITY")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(brandBlue.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Shield, null, modifier = Modifier.size(22.dp), tint = brandBlue)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Two-Factor Authentication",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            )
                            Text(
                                "Secure your account with a code",
                                style = MaterialTheme.typography.bodySmall.copy(color = textSecondary)
                            )
                        }
                        Switch(
                            checked = true,
                            onCheckedChange = { },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = brandBlue
                            )
                        )
                    }
                }
            }

            // Section: ACTIVE SESSIONS
            item {
                Spacer(modifier = Modifier.height(24.dp))
                SecurityHeaderLabel("ACTIVE SESSIONS")
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column {
                        SecuritySessionRow(
                            deviceName = "iPhone 15 Pro (Current)",
                            location = "San Francisco, USA • Active now",
                            icon = Icons.Outlined.Smartphone
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFF1F5F9)
                        )
                        SecuritySessionRow(
                            deviceName = "MacBook Pro 16\"",
                            location = "San Francisco, USA • 2 hours ago",
                            icon = Icons.Outlined.Laptop,
                            showLogout = true
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 0.5.dp,
                            color = Color(0xFFF1F5F9)
                        )
                        SecuritySessionRow(
                            deviceName = "Windows PC",
                            location = "London, UK • 3 days ago",
                            icon = Icons.Outlined.DesktopWindows,
                            showLogout = true
                        )
                    }
                }
            }

            // Footer
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Protecting your professional data is our priority. If you notice any suspicious activity, please contact our 24/7 security support immediately.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = labelColor,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    ),
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
        }
    }
}

@Composable
fun SecurityHeaderLabel(text: String) {
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
fun PasswordField(label: String, placeholder: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.5.sp
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFFF8FAFC)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = placeholder,
                    color = Color(0xFF94A3B8),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    Icons.Outlined.Visibility,
                    null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SecuritySessionRow(
    deviceName: String,
    location: String,
    icon: ImageVector,
    showLogout: Boolean = false
) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFFF8FAFC)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color(0xFF64748B))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = deviceName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            )
            Text(
                text = location,
                style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
            )
        }
        if (showLogout) {
            Text(
                "Log out",
                color = Color(0xFFEF4444),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.clickable { }
            )
        }
    }
}
