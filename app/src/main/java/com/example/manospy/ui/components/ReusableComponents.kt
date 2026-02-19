package com.example.manospy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.manospy.ui.theme.AppColors
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// ============================================
// TARJETAS MEJORADAS
// ============================================

/**
 * Tarjeta base con sombra mejorada y bordes redondeados optimizados
 */
@Composable
fun ElevatedCard(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    shadowElevation: Float = 2f,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .shadow(shadowElevation.dp, RoundedCornerShape(12.dp)),
        color = color,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(content = content)
    }
}

/**
 * Tarjeta interactiva con ripple effect mejorado
 */
@Composable
fun InteractiveCard(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    onClick: () -> Unit = {},
    shadowElevation: Float = 2f,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .shadow(shadowElevation.dp, RoundedCornerShape(12.dp))
            .clickable(
                interactionSource = MutableInteractionSource(),
                indication = null,
                onClick = onClick
            ),
        color = color,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(content = content)
    }
}

/**
 * Tarjeta con gradiente de fondo optimizada
 */
@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    gradientColors: List<Color>,
    shadowElevation: Float = 2f,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .shadow(shadowElevation.dp, RoundedCornerShape(12.dp))
            .background(
                brush = Brush.linearGradient(colors = gradientColors)
            ),
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(content = content)
    }
}

// ============================================
// BOTONES MEJORADOS
// ============================================

/**
 * Botón primario con sombra y estilo moderno
 */
@Composable
fun ModernButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    backgroundColor: Color = Color(0xFF0EA5E9)
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(6.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            disabledContainerColor = backgroundColor.copy(alpha = 0.5f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

/**
 * Botón secundario (outline) mejorado
 */
@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
    borderColor: Color = Color(0xFF0EA5E9)
) {
    OutlinedButton(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(2.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        border = androidx.compose.foundation.BorderStroke(2.dp, borderColor),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = borderColor,
            disabledContentColor = borderColor.copy(alpha = 0.5f)
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// ============================================
// CAMPOS DE TEXTO MEJORADOS
// ============================================

/**
 * Campo de texto mejorado con sombra
 */
@Composable
fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    label: String = "",
    isError: Boolean = false,
    helperText: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        if (label.isNotEmpty()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            )
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier
                .fillMaxWidth()
                .shadow(2.dp, RoundedCornerShape(16.dp)),
            placeholder = { Text(placeholder, color = Color(0xFF94A3B8)) },
            shape = RoundedCornerShape(16.dp),
            isError = isError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF0EA5E9),
                unfocusedBorderColor = Color(0xFFE2E8F0),
                cursorColor = Color(0xFF0EA5E9)
            )
        )

        if (isError && helperText != null) {
            Text(
                text = helperText,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

// ============================================
// AVATARES Y ICONOS
// ============================================

/**
 * Avatar circular con gradiente opcional
 */
@Composable
fun AvatarCircle(
    size: Float = 56f,
    backgroundColor: Color = Color(0xFF0EA5E9),
    useGradient: Boolean = false,
    gradientEnd: Color? = null,
    content: @Composable () -> Unit
) {
    val modifier = if (useGradient && gradientEnd != null) {
        Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(backgroundColor, gradientEnd)
                )
            )
    } else {
        Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(backgroundColor)
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Ícono en caja redondeada
 */
@Composable
fun IconBox(
    icon: ImageVector,
    backgroundColor: Color = Color(0xFFF0F9FF),
    iconColor: Color = Color(0xFF0EA5E9),
    size: Float = 40f
) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size((size / 2).dp)
        )
    }
}

// ============================================
// ESPACIADORES Y DIVISORES
// ============================================

/**
 * Divisor mejorado
 */
@Composable
fun ElevatedDivider(
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFE2E8F0),
    thickness: Float = 1f
) {
    Divider(
        modifier = modifier,
        color = color,
        thickness = thickness.dp
    )
}

/**
 * Espaciador vertical mejorado
 */
@Composable
fun VerticalSpacer(dp: Float) {
    Spacer(modifier = Modifier.height(dp.dp))
}

/**
 * Espaciador horizontal mejorado
 */
@Composable
fun HorizontalSpacer(dp: Float) {
    Spacer(modifier = Modifier.width(dp.dp))
}

// ============================================
// INDICADORES
// ============================================

/**
 * Indicador de estado con badge
 */
@Composable
fun StatusBadge(
    label: String,
    backgroundColor: Color = Color(0xFF10B981),
    textColor: Color = Color.White
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp)),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
        }
    }
}

/**
 * Indicador de progreso mejorado
 */
@Composable
fun ProgressIndicatorCard(
    title: String,
    value: Float,
    maxValue: Float = 100f,
    progressColor: Color = Color(0xFF0EA5E9)
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "${(value / maxValue * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF64748B)
                )
            )
        }
        LinearProgressIndicator(
            progress = value / maxValue,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = progressColor,
            trackColor = Color(0xFFE2E8F0)
        )
    }
}

// ============================================
// ITEMS DE LISTA
// ============================================

/**
 * Item de lista mejorado
 */
@Composable
fun ListItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF0EA5E9),
                    modifier = Modifier.size(20.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF0F172A)
                    )
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color(0xFF64748B)
                        )
                    )
                }
            }

            if (trailingContent != null) {
                trailingContent()
            }
        }
    }
}

// ============================================
// TARJETAS INFORMATIVAS
// ============================================

/**
 * Tarjeta informativa con ícono
 */
@Composable
fun InfoCard(
    title: String,
    description: String,
    backgroundColor: Color = Color(0xFFF0F9FF),
    borderColor: Color = Color(0xFF0EA5E9),
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            ),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFF64748B)
                )
            )
        }
    }
}
// ============================================
// DISEÑO MODERNO - HEADER Y TARJETAS
// ============================================

/**
 * Header azul con saludo, foto de perfil e icono de notificaciones
 */
@Composable
fun ModernBlueHeader(
    userName: String,
    userPhotoUrl: String? = null,
    onProfileClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp),
        color = Color(0xFF2563EB),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onProfileClick() }
                ) {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                        color = Color.White.copy(alpha = 0.9f),
                        shape = CircleShape
                    ) {
                        if (!userPhotoUrl.isNullOrEmpty()) {
                            AsyncImage(
                                model = userPhotoUrl,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF2563EB),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }

                    Column {
                        Text(
                            text = "Hola, $userName",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "¿En qué podemos ayudarte?",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .clickable { onNotificationClick() },
                    color = Color.White.copy(alpha = 0.2f),
                    shape = CircleShape
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = "Notificaciones",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Header de pasos con tarjeta superpuesta (ej: Paso 1 / Paso 2)
 */
@Composable
fun StepHeader(
    step: Int,
    totalSteps: Int = 2,
    title: String,
    userName: String? = null,
    onBack: () -> Unit = {},
    onNotificationClick: () -> Unit = {}
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .shadow(4.dp),
            color = Color(0xFF2563EB),
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
        ) {
            // espacio para el header azul
            Box(modifier = Modifier.fillMaxSize().padding(16.dp))
        }

        // tarjeta blanca superpuesta
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = 72.dp)
                .clip(RoundedCornerShape(16.dp))
                .shadow(6.dp, RoundedCornerShape(16.dp)),
            color = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF0F172A)
                            )
                        )
                        if (!userName.isNullOrEmpty()) {
                            Text(text = "Hola, $userName", style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B)))
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        // icono de notificaciones pequeño
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .clickable { onNotificationClick() },
                            color = Color(0xFF2563EB).copy(alpha = 0.08f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF2563EB))
                            }
                        }
                    }
                }

                // Indicador de pasos tipo pills
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..totalSteps) {
                        val selected = i == step
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            color = if (selected) Color(0xFF2563EB) else Color(0xFFF3F4F6)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "Paso $i",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (selected) Color.White else Color(0xFF475569)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tarjeta colorida para servicios activos (4 servicios como en referencia)
 */
@Composable
fun ServiceStatusCard(
    title: String,
    count: Int,
    backgroundColor: Color,
    icon: ImageVector? = null,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        color = backgroundColor,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                if (icon != null) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = count.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        }
    }
}

/**
 * Tarjeta de filtro para historial de reservas (Todas, Completadas, etc.)
 */
@Composable
fun FilterCard(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .shadow(if (isSelected) 2.dp else 0.dp, RoundedCornerShape(12.dp)),
        color = if (isSelected) Color(0xFF2563EB) else Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(12.dp, 8.dp)
                .wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) Color.White else Color(0xFF64748B)
            )
        }
    }
}

/**
 * Tarjeta de servicio/oferta en lista (similar a la referencia)
 */
@Composable
fun ServiceOfferCard(
    title: String,
    status: String,
    statusTag: String? = null,
    subtitle: String? = null,
    count: Int? = null,
    tagColor: Color = Color(0xFF10B981),
    onClick: () -> Unit = {},
    trailingContent: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = Color.White,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = statusTag ?: status,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = tagColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(tagColor.copy(alpha = 0.15f))
                        .padding(6.dp, 3.dp)
                )
            }

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0D141B),
                maxLines = 2
            )

            if (!subtitle.isNullOrEmpty()) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    maxLines = 2
                )
            }

            if (count != null) {
                Text(
                    text = "$count Rooms",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF64748B)
                )
            }

            if (trailingContent != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                    trailingContent()
                }
            }
        }
    }
}

/**
 * Cabecera con estilo: top azul y tarjeta blanca superpuesta con título y acciones.
 */
@Composable
fun SimpleCardHeader(
    title: String,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    showBack: Boolean = true,
    onBack: () -> Unit = {},
    trailing: (@Composable () -> Unit)? = null,
    userName: String? = null
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .shadow(4.dp),
            color = AppColors.PrimaryBlue,
            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
        ) {
            // espacio para el header azul
            Box(modifier = Modifier.fillMaxSize().padding(16.dp))
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = 72.dp)
                .clip(RoundedCornerShape(16.dp))
                .shadow(6.dp, RoundedCornerShape(16.dp)),
            color = Color.White
        ) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                if (showBack) {
                    IconButton(onClick = onBack, modifier = Modifier.size(44.dp)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                }

                if (leading != null) {
                    Box(modifier = Modifier.padding(end = 8.dp)) { leading() }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = AppColors.TextPrimary
                    )
                    if (!subtitle.isNullOrEmpty()) {
                        Text(text = subtitle, style = MaterialTheme.typography.bodySmall.copy(color = AppColors.TextSecondary))
                    }
                }

                if (trailing != null) {
                    Box(contentAlignment = Alignment.CenterEnd) { trailing() }
                }
            }
        }
    }
}

/**
 * Cabecera minimalista sobre fondo azul: flecha pequeña y título en blanco sin tarjeta.
 */
@Composable
fun BlueTopBar(
    title: String,
    showBack: Boolean = true,
    onBack: () -> Unit = {},
    trailing: (@Composable () -> Unit)? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        color = AppColors.PrimaryBlue
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBack) {
                IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(8.dp))
            }

            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )

            if (trailing != null) {
                Box(contentAlignment = Alignment.CenterEnd) { trailing() }
            }
        }
    }
}

/**
 * Header Unificado con Esquinas Redondeadas en la Parte Inferior
 * Estilo consistente para TODAS las pantallas de la app
 */
@Composable
fun UnifiedScreenHeader(
    title: String,
    subtitle: String? = null,
    onBackClick: (() -> Unit)? = null,
    actionIcon: @Composable (() -> Unit)? = null,
    backgroundColor: Color = Color(0xFF2563EB),
    contentColor: Color = Color.White
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
        color = backgroundColor,
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = if (subtitle != null) 8.dp else 0.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (onBackClick != null) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Atrás",
                                tint = contentColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = contentColor,
                            maxLines = 1
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                fontSize = 13.sp,
                                color = contentColor.copy(alpha = 0.85f),
                                maxLines = 1
                            )
                        }
                    }
                }

                if (actionIcon != null) {
                    Box(modifier = Modifier.size(40.dp), contentAlignment = Alignment.Center) {
                        actionIcon()
                    }
                }
            }
        }
    }
}

// ============================================
// SINCRONIZACIÓN DE STATUS BAR CON HEADER
// ============================================

/**
 * Sincroniza automáticamente el color de la barra de estado (status bar) con el color del header.
 * Los iconos de la barra se adaptan según el brillo del color del header.
 * 
 * @param headerColor Color de la cabecera (TopAppBar)
 * 
 * Uso:
 * SyncStatusBarWithHeader(headerColor = Color(0xFF2563EB))
 */
@Composable
fun SyncStatusBarWithHeader(
    headerColor: Color = Color(0xFF2563EB),
    navigationBarColor: Color? = null
) {
    val systemUiController = rememberSystemUiController()
    
    // Determinar si los iconos deben ser oscuros o claros basado en el brillo del color
    // Colores oscuros (como azul #2563EB): iconos claros (darkIcons = false)
    // Colores claros: iconos oscuros (darkIcons = true)
    val isDarkHeader = isColorDark(headerColor)
    val darkIcons = !isDarkHeader // Si header es oscuro, los iconos deben ser claros
    
    LaunchedEffect(headerColor, navigationBarColor) {
        systemUiController.setStatusBarColor(
            color = headerColor,
            darkIcons = darkIcons
        )
        
        // Opcional: sincronizar la barra de navegación también
        if (navigationBarColor != null) {
            systemUiController.setNavigationBarColor(
                color = navigationBarColor,
                darkIcons = isColorDark(navigationBarColor)
            )
        }
    }
}

/**
 * Determina si un color es oscuro o claro usando la fórmula de luminancia relativa
 * basada en el estándar WCAG.
 * 
 * @return true si el color es oscuro, false si es claro
 */
fun isColorDark(color: Color): Boolean {
    // Convertir ARGB a valores 0-1
    val r = color.red
    val g = color.green
    val b = color.blue
    
    // Calcular luminancia relativa según WCAG
    val luminance = (0.299f * r + 0.587f * g + 0.114f * b)
    
    // Si luminancia > 0.5, es claro y necesita iconos oscuros
    // Si luminancia <= 0.5, es oscuro y necesita iconos claros
    return luminance <= 0.5f
}

/**
 * Versión mejorada de Scaffold que sincroniza automáticamente el status bar
 * con el color del topBar.
 * 
 * Características:
 * - Sincronización automática de color status bar
 * - Adaptación automática de iconos (claro/oscuro)
 * - Compatible con navegación inferior
 * - Mantiene todas las características de Scaffold estándar
 * 
 * Uso:
 * ScaffoldWithStatusBarSync(
 *     topBar = { /* TopAppBar */ },
 *     topBarColor = Color(0xFF2563EB),
 *     containerColor = Color.White,
 *     content = { padding -> /* content */ }
 * )
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldWithStatusBarSync(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = {},
    topBarColor: Color = Color(0xFF2563EB),
    bottomBar: @Composable () -> Unit = {},
    bottomBarColor: Color? = null,
    containerColor: Color = Color.White,
    contentColor: Color = Color.Unspecified,
    content: @Composable (PaddingValues) -> Unit
) {
    // Sincronizar status bar con topBar color
    SyncStatusBarWithHeader(
        headerColor = topBarColor,
        navigationBarColor = bottomBarColor
    )
    
    Scaffold(
        modifier = modifier,
        topBar = topBar,
        bottomBar = bottomBar,
        containerColor = containerColor,
        contentColor = contentColor,
        content = content
    )
}