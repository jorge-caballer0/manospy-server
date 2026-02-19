package com.example.manospy.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpScreen(
    onBack: () -> Unit,
    navController: androidx.navigation.NavController? = null
) {
    val primary = Color(0xFF137FEC)
    val bgLight = Color(0xFFF6F7F8)
    val white = Color.White
    val textGray = Color(0xFF64748B)
    val textDark = Color(0xFF1F2937)
    val borderGray = Color(0xFFE5E7EB)
    val context = LocalContext.current

    var faq1Expanded by remember { mutableStateOf(false) }
    var faq2Expanded by remember { mutableStateOf(false) }
    var faq3Expanded by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    val helpTopics = listOf(
        "Cómo solicito un servicio" to "Para solicitar un servicio, selecciona la categoría desde el inicio, elige profesional y pulsa 'Reservar'.",
        "¿Cómo pago?" to "El pago se realiza presencialmente con el profesional — la app no procesa el pago en línea.",
        "Cómo cancelo una reserva" to "Puedes cancelar desde 'Mis Reservas' hasta 24 horas antes sin penalización.",
        "Verificación de cuenta" to "Para cambiar email o teléfono requerimos verificación con código.",
        "Cambiar foto de perfil" to "Desde tu perfil puedes cambiar la foto tocando el icono de cámara.",
        "Direcciones" to "Agrega y edita tus direcciones en 'Mis Direcciones'."
    )

    AppScaffold(
        title = "Ayuda",
        onBackClick = { onBack() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(top = 12.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
                // Buscador
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(white, RoundedCornerShape(20.dp))
                        .shadow(1.dp, RoundedCornerShape(20.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("¿Cómo podemos ayudarte?") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = primary
                        ),
                        singleLine = true
                    )
                }

                // Contactar Soporte
                Text(
                    "CONTACTAR SOPORTE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color(0xFFA0AEC0),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Chat en vivo
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { navController?.navigate("chatSupport") },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = white),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderGray)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = Color(0xFFF0F4FF)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.ChatBubble,
                                        contentDescription = null,
                                        tint = primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Chat en vivo",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = textDark
                            )
                            Text(
                                "Respuesta inmediata",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    // Email
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO)
                                intent.data = Uri.parse("mailto:digitalfoxjym@gmail.com")
                                context.startActivity(intent)
                            },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = white),
                        border = androidx.compose.foundation.BorderStroke(1.dp, borderGray)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Surface(
                                modifier = Modifier.size(48.dp),
                                shape = CircleShape,
                                color = Color(0xFFF0F4FF)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        Icons.Default.MailOutline,
                                        contentDescription = null,
                                        tint = primary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                "Email",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                ),
                                color = textDark
                            )
                            Text(
                                "Soporte 24/7",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }

                // Preguntas Frecuentes (mostrar solo cuando hay búsqueda o mostrar top items)
                if (query.isNotBlank()) {
                    val results = helpTopics.filter { (q, a) -> q.contains(query, ignoreCase = true) || a.contains(query, ignoreCase = true) }
                    if (results.isEmpty()) {
                        Text("No se encontraron resultados para '$query'", color = Color(0xFF9CA3AF))
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            results.forEach { (q, a) ->
                                FAQItem(
                                    question = q,
                                    expanded = false,
                                    onToggle = {},
                                    answer = a
                                )
                            }
                        }
                    }
                } else {
                    // Mostrar solo 3 ejemplos por defecto
                    Text(
                        "PREGUNTAS FRECUENTES",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color(0xFFA0AEC0),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FAQItem(
                            question = "¿Cómo solicito un servicio?",
                            expanded = faq1Expanded,
                            onToggle = { faq1Expanded = !faq1Expanded },
                            answer = "Para solicitar un servicio, selecciona la categoría deseada desde el inicio, elige al profesional que mejor se adapte a tus necesidades y pulsa en 'Reservar'."
                        )
                        FAQItem(
                            question = "¿Cómo pago?",
                            expanded = faq2Expanded,
                            onToggle = { faq2Expanded = !faq2Expanded },
                            answer = "El pago se realiza presencialmente con el profesional — la app no procesa el pago en línea."
                        )
                        FAQItem(
                            question = "¿Cómo cancelo una reserva?",
                            expanded = faq3Expanded,
                            onToggle = { faq3Expanded = !faq3Expanded },
                            answer = "Puedes cancelar desde la sección 'Mis Reservas' hasta 24 horas antes del servicio sin penalización alguna."
                        )
                    }
                }

                // Información Legal
                Text(
                    "INFORMACIÓN LEGAL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color(0xFFA0AEC0),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LegalItem(
                        icon = Icons.Default.Description,
                        title = "Términos y Condiciones",
                        onClick = { navController?.navigate("terms_and_conditions") }
                    )
                    LegalItem(
                        icon = Icons.Default.Shield,
                        title = "Política de Privacidad",
                        onClick = { navController?.navigate("privacy_policy") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Soporte WhatsApp
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(primary, RoundedCornerShape(28.dp))
                        .shadow(6.dp, RoundedCornerShape(28.dp))
                        .padding(0.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = primary),
                    border = null
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "¿Aún necesitas ayuda?",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = white
                        )
                        Text(
                            "Nuestro equipo está disponible de lunes a viernes de 08:00 a 18:00 hs.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp
                            ),
                            color = Color(0xFFDBEAFE),
                            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                        )
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.data = Uri.parse("https://wa.me/595991836168")
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = white),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                "Enviar WhatsApp Soporte",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Footer
                Text(
                    "Centro de Ayuda ManosPy • Versión 2.4.0",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp
                    ),
                    color = Color(0xFF9CA3AF),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
            }
        }
    }

@Composable
private fun FAQItem(
    question: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    answer: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(20.dp))
            .clickable { onToggle() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    question,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = Color(0xFF1F2937)
                )
                Icon(
                    if (expanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(22.dp)
                )
            }
            if (expanded) {
                Divider(color = Color(0xFFF3F4F6), thickness = 1.dp)
                Text(
                    answer,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 13.sp
                    ),
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun LegalItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF0F4FF)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = Color(0xFF137FEC),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    ),
                    color = Color(0xFF1F2937)
                )
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color(0xFFA3A3A3),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
