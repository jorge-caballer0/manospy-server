package com.example.manospy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.theme.CorporateBlue

/**
 * SISTEMA DE TARJETAS RESPONSIVAS
 * 
 * Un conjunto de componentes reutilizables para crear tarjetas 
 * consistentes, responsivas y adaptables a cualquier pantalla.
 * 
 * Características:
 * - Esquinas redondeadas (12dp por defecto)
 * - Sombra ligera (elevation 2.dp)
 * - Fondo blanco consistente
 * - Padding interno estándar (16dp)
 * - Márgenes laterales uniformes (16dp)
 * - Compatible con LazyColumn y Column
 */

// ==========================================
// BASE CARD - Contenedor reutilizable
// ==========================================

/**
 * BaseCard - Contenedor base para todas las tarjetas
 * 
 * Proporciona estilos consistentes: esquinas redondeadas, sombra, padding.
 * Útil como base para cualquier tipo de tarjeta personalizada.
 *
 * @param modifier Modificador del contenedor
 * @param backgroundColor Color de fondo (default: blanco)
 * @param cornerRadius Esquinas redondeadas (default: 12dp)
 * @param elevation Elevación/sombra (default: 2dp)
 * @param padding Padding interno (default: 16dp)
 * @param content Contenido de la tarjeta
 */
@Composable
fun BaseCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    cornerRadius: Int = 12,
    elevation: Int = 2,
    padding: Int = 16,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(cornerRadius.dp),
        color = backgroundColor,
        shadowElevation = elevation.dp
    ) {
        Box(
            modifier = Modifier.padding(padding.dp)
        ) {
            content()
        }
    }
}

// ==========================================
// SECTION CARD - Tarjeta con título
// ==========================================

/**
 * SectionCard - Tarjeta con título y contenido
 * 
 * Ideal para agrupar contenido relacionado con un título descriptivo.
 *
 * @param title Título de la sección
 * @param modifier Modificador adicional
 * @param contentPadding Padding entre título y contenido (default: 12dp)
 * @param content Contenido de la sección
 */
@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    contentPadding: Int = 12,
    content: @Composable () -> Unit
) {
    BaseCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(contentPadding.dp)
        ) {
            // Título
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0D141B),
                letterSpacing = 0.8.sp
            )

            // Contenido
            content()
        }
    }
}

// ==========================================
// INFO CARD - Información simple
// ==========================================

/**
 * InfoCard - Tarjeta para mostrar información simple
 * 
 * Perfecta para mostrar pares label-valor o información descriptiva.
 *
 * @param title Título principales
 * @param subtitle Subtítulo o descripción (opcional)
 * @param value Valor o contenido principal (opcional)
 * @param modifier Modificador adicional
 * @param onClick Callback cuando se toca (opcional)
 */
@Composable
fun InfoCard(
    title: String,
    subtitle: String? = null,
    value: String? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    BaseCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Título
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1F2937)
            )

            // Subtítulo
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF64748B)
                )
            }

            // Valor
            value?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = CorporateBlue
                )
            }
        }
    }
}

// ==========================================
// ACTION CARD - Tarjeta con acciones
// ==========================================

/**
 * ActionCard - Tarjeta con contenido y botones de acción
 * 
 * Ideal para tarjetas que requieren interacción (editar, eliminar, etc).
 *
 * @param title Título principal
 * @param description Descripción del contenido
 * @param modifier Modificador adicional
 * @param actions Lista de pares (texto de acción, callback)
 */
@Composable
fun ActionCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actions: List<Pair<String, () -> Unit>> = emptyList()
) {
    BaseCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Contenido
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF64748B)
                )
            }

            // Acciones
            if (actions.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    actions.forEachIndexed { index, (text, callback) ->
                        Button(
                            onClick = callback,
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (index == 0) CorporateBlue else Color(0xFFF5F7FA)
                            )
                        ) {
                            Text(
                                text = text,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (index == 0) Color.White else Color(0xFF0D47A1)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// STATUS CARD - Tarjeta con estado
// ==========================================

/**
 * StatusCard - Tarjeta para mostrar estado visual con color
 * 
 * Ideal para reservas, solicitudes, etc. con indicador de estado.
 *
 * @param label Etiqueta del estado
 * @param statusColor Color indicador del estado
 * @param title Título principal
 * @param description Descripción
 * @param modifier Modificador adicional
 */
@Composable
fun StatusCard(
    label: String,
    statusColor: Color,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    BaseCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Indicador de estado
            Surface(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.CenterVertically),
                shape = RoundedCornerShape(6.dp),
                color = statusColor
            ) {}

            // Contenido
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F2937)
                )
                Text(
                    text = description,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}

// ==========================================
// HIGHLIGHT CARD - Tarjeta destacada
// ==========================================

/**
 * HighlightCard - Tarjeta con fondo gradient o color destacado
 * 
 * Ideal para promover información importante o destacada.
 *
 * @param title Título principal
 * @param highlight Texto o contenido destacado
 * @param backgroundColor Color de fondo (default: azul corporativo claro)
 * @param modifier Modificador adicional
 */
@Composable
fun HighlightCard(
    title: String,
    highlight: String,
    backgroundColor: Color = Color(0xFFF0F7FF),
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier,
        backgroundColor = backgroundColor,
        elevation = 0
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )
            Text(
                text = highlight,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = CorporateBlue
            )
        }
    }
}

// ==========================================
// DIVIDER CARD - Separador visual
// ==========================================

/**
 * DividerCard - Elemento separador minimalista
 * 
 * Useful para dividir secciones visualmente.
 */
@Composable
fun DividerCard(
    modifier: Modifier = Modifier,
    height: Int = 1,
    color: Color = Color(0xFFE2E8F0)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height.dp)
            .background(color)
    )
}
