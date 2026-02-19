package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalBookingsScreen() {
    val brandBlue = Color(0xFF0EA5E9)
    val bgLight = Color(0xFFF8FAFC)
    val textDark = Color(0xFF0F172A)
    val textGray = Color(0xFF64748B)
    
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Upcoming", "Completed")

    AppScaffold(
        title = "Mis Reservas",
        onBackClick = { },
        hasBottomNavigation = true,
        modifier = Modifier.background(bgLight)
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
            // Segmented Control / Tabs
            Surface(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                color = Color(0xFFE2E8F0).copy(alpha = 0.5f)
            ) {
                Row(modifier = Modifier.padding(4.dp)) {
                    tabs.forEachIndexed { index, title ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(22.dp))
                                .background(if (selectedTab == index) Color.White else Color.Transparent)
                                .clickable { selectedTab = index },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == index) textDark else textGray
                                )
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val currentData = if (selectedTab == 0) upcomingMocks else completedMocks
                items(currentData) { booking ->
                    BookingCardProfessional(booking, brandBlue, textDark, textGray)
                }
            }
            }

            // Bottom Navigation placed over content (sibling of Column)
        
            Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 1.dp,
                    modifier = Modifier
                        .shadow(2.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                ) {
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { Icon(Icons.Outlined.Home, null, modifier = Modifier.size(24.dp)) },
                        label = { Text("HOME", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)) }
                    )
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(24.dp)) },
                        label = { Text("BOOKINGS", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = brandBlue,
                            selectedTextColor = brandBlue,
                            indicatorColor = Color(0xFF137FEC).copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { Icon(Icons.Outlined.ChatBubbleOutline, null, modifier = Modifier.size(24.dp)) },
                        label = { Text("MESSAGES", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { Icon(Icons.Outlined.Person, null, modifier = Modifier.size(24.dp)) },
                        label = { Text("PROFILE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp)) }
                    )
                }
            }
        }
    }
}

@Composable
fun BookingCardProfessional(
    booking: ProfBookingMock,
    brandBlue: Color,
    textDark: Color,
    textGray: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // Service Image
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F5F9))
                ) {
                    Icon(
                        Icons.Default.Image,
                        null,
                        modifier = Modifier.align(Alignment.Center),
                        tint = Color(0xFFCBD5E1)
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = textDark
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, null, tint = textGray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(booking.clientName, style = MaterialTheme.typography.bodySmall.copy(color = textGray))
                    }
                }
                
                StatusBadgeProf(booking.status)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Details Section
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF0F9FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.CalendarMonth, null, tint = brandBlue, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(booking.date, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = textDark))
                    Text(booking.time, style = MaterialTheme.typography.labelSmall.copy(color = textGray))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFF0F9FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.LocationOn, null, tint = brandBlue, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(booking.locationTitle, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, color = textDark))
                    Text(booking.locationSub, style = MaterialTheme.typography.labelSmall.copy(color = textGray))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            if (booking.status != "COMPLETED") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = {},
                        modifier = Modifier.weight(0.4f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Details", fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Button(
                        onClick = {},
                        modifier = Modifier.weight(0.6f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ChatBubble, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Contact", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9))
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Assessment, null, tint = textDark, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("View Summary", color = textDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadgeProf(status: String) {
    val bgColor = when(status) {
        "CONFIRMED" -> Color(0xFFE0F2FE)
        "PENDING" -> Color(0xFFFEF3C7)
        "COMPLETED" -> Color(0xFFDCFCE7)
        else -> Color(0xFFF1F5F9)
    }
    val txtColor = when(status) {
        "CONFIRMED" -> Color(0xFF0EA5E9)
        "PENDING" -> Color(0xFFD97706)
        "COMPLETED" -> Color(0xFF166534)
        else -> Color(0xFF64748B)
    }
    Surface(color = bgColor, shape = RoundedCornerShape(8.dp)) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black, 
                fontSize = 10.sp, 
                color = txtColor,
                letterSpacing = 0.5.sp
            )
        )
    }
}

data class ProfBookingMock(
    val title: String,
    val clientName: String,
    val status: String,
    val date: String,
    val time: String,
    val locationTitle: String,
    val locationSub: String
)

val upcomingMocks = listOf(
    ProfBookingMock("Deep Tissue Therapy", "Sarah Johnson", "CONFIRMED", "Oct 24, 2023", "10:30 AM - 12:00 PM", "Downtown Wellness Studio", "New York, NY"),
    ProfBookingMock("Executive Grooming", "Michael Chen", "PENDING", "Oct 26, 2023", "02:00 PM - 03:00 PM", "The Plaza Suites", "Manhattan, NY")
)

val completedMocks = listOf(
    ProfBookingMock("Private Yoga Session", "Amanda Lewis", "COMPLETED", "Oct 20, 2023", "09:00 AM - 10:30 AM", "Studio 54", "Brooklyn, NY")
)
