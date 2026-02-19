package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.animateFloatAsState
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalMetricsScreen() {
    val primaryCyan = Color(0xFF00E5D1)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val backgroundColor = Color(0xFFF9FAFB)

    AppScaffold(
        title = "Métricas",
        onBackClick = { },
        hasBottomNavigation = false,
        modifier = Modifier.background(backgroundColor)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Métricas superiores
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricSmallCard(
                        modifier = Modifier.weight(1f),
                        label = "SERVICIOS",
                        value = "124",
                        trend = "+12%",
                        icon = Icons.Default.TrendingUp,
                        accentColor = primaryCyan
                    )
                    MetricSmallCard(
                        modifier = Modifier.weight(1f),
                        label = "CALIFICACIÓN",
                        value = "4.9",
                        trend = "+0.1",
                        icon = Icons.Default.Star,
                        accentColor = primaryCyan,
                        isRating = true
                    )
                }
            }
            // Actividad semanal
            item {
                WeeklyActivitySection(primaryCyan, textPrimary, textSecondary)
            }

            // Desempeño
            item {
                PerformanceSection(primaryCyan, textPrimary)
            }

            // Servicios más solicitados
            item {
                RequestedServicesSection(primaryCyan, textPrimary)
            }
        }
    }
}

@Composable
fun WeeklyActivitySection(primaryCyan: Color, textPrimary: Color, textSecondary: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Actividad semanal",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
                Text(
                    text = "Promedio 4.5 servicios/día",
                    style = MaterialTheme.typography.bodySmall.copy(color = primaryCyan)
                )
            }
            Text(
                text = "JUN 12 - 18",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .height(120.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                val activityData = listOf(0.5f, 0.3f, 0.7f, 0.9f, 0.6f, 0.5f, 0.2f)
                val days = listOf("L", "M", "M", "J", "V", "S", "D")

                activityData.forEachIndexed { index, weight ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .fillMaxHeight(weight)
                                .clip(RoundedCornerShape(4.dp))
                                .background(primaryCyan)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = days[index],
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = textSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PerformanceSection(primaryCyan: Color, textPrimary: Color) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Desempeño",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary
                )
            )
            Icon(
                imageVector = Icons.Default.AutoGraph,
                contentDescription = null,
                tint = primaryCyan,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PerformanceItem("Satisfacción cliente", 0.98f, Icons.Default.SentimentVerySatisfied, primaryCyan)
                PerformanceItem("Puntualidad", 0.95f, Icons.Default.Timer, primaryCyan)
                PerformanceItem("Finalización", 0.92f, Icons.Default.CheckCircle, primaryCyan)
            }
        }
    }
}
@Composable
fun MetricSmallCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    trend: String,
    icon: ImageVector,
    accentColor: Color,
    isRating: Boolean = false
) {
    Surface(
        modifier = modifier.height(120.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.5.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF111827)
                    )
                )
                if (isRating) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = trend,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = accentColor,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
fun PerformanceItem(label: String, progress: Float, icon: ImageVector, accentColor: Color) {
    val animatedProgress by animateFloatAsState(targetValue = progress)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )
        )
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .width(100.dp)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = accentColor,
            trackColor = Color(0xFFF1F5F9),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun RequestedServiceBar(label: String, progress: Float, percentageText: String) {
    val animatedProgress by animateFloatAsState(targetValue = progress)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            )
            Text(
                text = percentageText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF94A3B8),
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = Color(0xFF00E5D1),
            trackColor = Color(0xFFF1F5F9),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun RequestedServicesSection(primaryCyan: Color, textPrimary: Color) {
    Column {
        Text(
            text = "Servicios más solicitados",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                RequestedServiceBar("Plomería", 0.45f, "45%")
                RequestedServiceBar("Electricidad", 0.32f, "32%")
                RequestedServiceBar("Limpieza", 0.18f, "18%")
            }
        }
    }
}
