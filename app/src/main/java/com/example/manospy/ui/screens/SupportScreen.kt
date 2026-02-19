package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PersonAddAlt1
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupportScreen(navController: NavController? = null) {
    val primaryCyan = Color(0xFF00E5D1)
    val lightTealBg = Color(0xFFE6F9F8)
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val backgroundColor = Color(0xFFF9FAFB)

    AppScaffold(
        title = "Soporte",
        onBackClick = { /* navController?.popBackStack() si lo recibes */ },
        hasBottomNavigation = false
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(24.dp)
        ) {
            // Preguntas frecuentes
            item {
                Text(
                    text = "Preguntas frecuentes",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))

                FaqItem(
                    icon = Icons.Default.Lightbulb,
                    title = "¿Cómo funciona Manospy?",
                    primaryCyan = primaryCyan,
                    textPrimary = textPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                FaqItem(
                    icon = Icons.Default.PersonAddAlt1,
                    title = "¿Cómo contacto a un profesional?",
                    primaryCyan = primaryCyan,
                    textPrimary = textPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                FaqItem(
                    icon = Icons.Default.BusinessCenter,
                    title = "¿Cómo me registro como profesional?",
                    primaryCyan = primaryCyan,
                    textPrimary = textPrimary
                )
            }

            // Guía rápida
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "Guía rápida",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))

                Column(modifier = Modifier.padding(start = 8.dp)) {
                    GuideStep(
                        number = "1",
                        title = "Busca el servicio",
                        description = "Encuentra especialistas cerca de ti usando el mapa o categorías.",
                        primaryCyan = primaryCyan,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        isLast = false
                    )
                    GuideStep(
                        number = "2",
                        title = "Selecciona perfil",
                        description = "Revisa calificaciones y trabajos previos antes de decidir.",
                        primaryCyan = primaryCyan,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        isLast = false
                    )
                    GuideStep(
                        number = "3",
                        title = "Conecta y contrata",
                        description = "Chatea directamente y agenda tu cita de forma segura.",
                        primaryCyan = primaryCyan,
                        textPrimary = textPrimary,
                        textSecondary = textSecondary,
                        isLast = true
                    )
                }
            }
            // Contacto
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    color = lightTealBg.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¿Necesitas más ayuda?",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Nuestro equipo está disponible 24/7 para resolver tus dudas.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = textSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        // Email Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Email, null, tint = primaryCyan, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("soporte@manospy.com", fontWeight = FontWeight.Medium, color = textPrimary)
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        Button(
                            onClick = { navController?.navigate("chatSupport") },
                            modifier = Modifier.fillMaxWidth().height(60.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryCyan)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Contactar soporte", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = textPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.AutoMirrored.Filled.Send, null, tint = textPrimary, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun FaqItem(icon: ImageVector, title: String, primaryCyan: Color, textPrimary: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(72.dp),
        shape = RoundedCornerShape(32.dp),
        color = Color.White,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFF0FDFB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = primaryCyan, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                ),
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color(0xFFCBD5E1))
        }
    }
}

@Composable
fun GuideStep(
    number: String,
    title: String,
    description: String,
    primaryCyan: Color,
    textPrimary: Color,
    textSecondary: Color,
    isLast: Boolean
) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(32.dp)
        ) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(primaryCyan),
                contentAlignment = Alignment.Center
            ) {
                Text(number, color = textPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(primaryCyan.copy(alpha = 0.2f))
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 24.dp)) {
            Text(text = title, fontWeight = FontWeight.Bold, color = textPrimary, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = description, style = MaterialTheme.typography.bodyMedium, color = textSecondary)
        }
    }
}
