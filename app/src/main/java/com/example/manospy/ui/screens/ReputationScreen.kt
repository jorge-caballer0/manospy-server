package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.animation.core.animateFloatAsState
import com.example.manospy.ui.components.AppScaffold

// Mock data for reviews
data class Review(
    val name: String,
    val date: String,
    val rating: Int,
    val comment: String,
    val likes: Int,
    val avatarUrl: String? = null // foto de perfil opcional
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReputationScreen() {
    val primaryCyan = Color(0xFF00E5D1)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val backgroundColor = Color(0xFFF9FAFB)

    val reviews = listOf(
        Review("Juan Pérez", "12 Oct 2023", 5,
            "Excelente trabajo, muy puntual y profesional. Altamente recomendado para cualquier reparación en casa. Dejó todo muy limpio.",
            12, avatarUrl = null),
        Review("María García", "05 Oct 2023", 4,
            "Buen servicio en general, llegó a tiempo y resolvió el problema eléctrico rápidamente. Tuve que explicar un poco de más al inicio.",
            8, avatarUrl = null),
        Review("Carlos Ruiz", "28 Sep 2023", 5,
            "Muy detallista y limpio en su trabajo. El acabado de la pintura fue perfecto. Volveré a contratarlo sin duda.",
            5, avatarUrl = null)
    )

    Scaffold(
        containerColor = backgroundColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Tu Reputación",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: back navigation */ }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                            contentDescription = "Volver",
                            modifier = Modifier.size(20.dp),
                            tint = textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Outlined.Home, null) },
                    label = { Text("INICIO", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Outlined.WorkOutline, null) },
                    label = { Text("TRABAJOS", fontSize = 10.sp) }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Star, null) },
                    label = { Text("REPUTACIÓN", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = primaryCyan,
                        selectedTextColor = primaryCyan,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Outlined.AccountCircle, null) },
                    label = { Text("PERFIL", fontSize = 10.sp) }
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Resumen reputación
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "4.9",
                                style = MaterialTheme.typography.displayMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = textPrimary
                                )
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                repeat(5) { index ->
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = if (index < 5) primaryCyan else primaryCyan.copy(alpha = 0.3f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "128 reseñas totales",
                                style = MaterialTheme.typography.bodySmall.copy(color = primaryCyan)
                            )
                        }

                        Spacer(modifier = Modifier.width(24.dp))

                        Column(modifier = Modifier.weight(1.5f)) {
                            RatingBarRow("5", 0.80f, "80%")
                            RatingBarRow("4", 0.15f, "15%")
                            RatingBarRow("3", 0.03f, "3%")
                            RatingBarRow("2", 0.01f, "1%")
                            RatingBarRow("1", 0.01f, "1%")
                        }
                    }
                }
            }

            // Encabezado reseñas
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Reseñas de clientes",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Filtrar",
                            style = MaterialTheme.typography.labelLarge.copy(color = primaryCyan)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.FilterList, null, tint = primaryCyan, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Lista de reseñas
            items(reviews) { review ->
                ReviewItem(review, primaryCyan, textPrimary, textSecondary)
            }
        }
    }
}
@Composable
fun RatingBarRow(label: String, progress: Float, percentage: String) {
    val animatedProgress by animateFloatAsState(targetValue = progress)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF111827)
            ),
            modifier = Modifier.width(12.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape),
            color = Color(0xFF00E5D1),
            trackColor = Color(0xFFF1F5F9),
            strokeCap = StrokeCap.Round
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = percentage,
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCBD5E1)),
            modifier = Modifier.width(30.dp)
        )
    }
}

@Composable
fun ReviewItem(
    review: Review,
    accentColor: Color,
    textPrimary: Color,
    textSecondary: Color,
    onReplyClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: avatar + name + date
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (review.avatarUrl != null) {
                    AsyncImage(
                        model = review.avatarUrl,
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E7EB))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE5E7EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = review.name,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                    )
                    Text(
                        text = review.date,
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFFCBD5E1))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rating stars
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (index < review.rating) accentColor else Color(0xFFE5E7EB),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comment
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = textSecondary,
                    lineHeight = 20.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Actions: likes + reply
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.ThumbUpOffAlt,
                    contentDescription = "Me gusta",
                    modifier = Modifier.size(18.dp),
                    tint = textSecondary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = review.likes.toString(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textSecondary
                    )
                )

                Spacer(modifier = Modifier.width(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(enabled = onReplyClick != null) { onReplyClick?.invoke() }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ChatBubbleOutline,
                        contentDescription = "Responder",
                        modifier = Modifier.size(18.dp),
                        tint = accentColor
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Responder",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    )
                }
            }
        }
    }
}
