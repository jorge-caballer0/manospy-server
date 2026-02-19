package com.example.manospy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.manospy.ui.theme.CorporateBlue

/**
 * EJEMPLOS DE SISTEMA DE TARJETAS RESPONSIVAS
 * 
 * Este archivo contiene ejemplos prácticos de cómo implementar
 * el sistema de tarjetas en diferentes escenarios reales.
 * 
 * Patrones demostrados:
 * 1. CardLayout con Column - Para listas pequeñas
 * 2. LazyCardLayout - Para listas grandes y dinámicas
 * 3. Tarjetas con estado visual
 * 4. Secciones agrupadas
 * 5. Tarjetas con acciones interactivas
 */

// ==========================================
// EJEMPLO 1: LAYOUT SIMPLE CON CARDS
// ==========================================

/**
 * Ejemplo de pantalla simple con AppScaffold + CardLayout
 * 
 * Patrón recomendado para:
 * - Pantallas con pocos elementos (<10)
 * - Contenido estático
 * - Formularios
 */
@Composable
fun SimpleCardLayoutExample() {
    AppScaffold(
        title = "Ejemplo Layout Simple",
        onBackClick = { }
    ) { paddingValues ->
        CardLayout(
            modifier = Modifier.padding(paddingValues),
            hasBottomNavigation = true
        ) {
            // Tarjeta 1: Información simple
            SectionCard(title = "Datos Importantes") {
                Text("Aquí va el contenido que necesitas mostrar de forma clara")
            }

            // Tarjeta 2: Info con valores
            InfoCard(
                title = "Estado del Servicio",
                subtitle = "Solicitado",
                value = "Pendiente de aprobación"
            )

            // Tarjeta 3: Con acciones
            ActionCard(
                title = "Confirmación",
                description = "¿Deseas continuar con esta acción?",
                actions = listOf(
                    "Cancelar" to { },
                    "Aceptar" to { }
                )
            )
        }
    }
}

// ==========================================
// EJEMPLO 2: LISTA DINÁMICA CON LAZY LAYOUT
// ==========================================

/**
 * Ejemplo de lista dinámica con AppScaffold + LazyCardLayout
 * 
 * Patrón recomendado para:
 * - Listas con muchos elementos (>20)
 * - Datos fetched desde API
 * - Scroll performance crítico
 */
@Composable
fun LazyCardLayoutExample() {
    // Simulación de datos
    val items = listOf(
        "Reserva #001" to "Pendiente",
        "Reserva #002" to "Aceptada",
        "Reserva #003" to "Completada"
    )

    AppScaffold(
        title = "Lista de Reservas",
        onBackClick = { }
    ) { paddingValues ->
        LazyCardLayout(
            modifier = Modifier.padding(paddingValues),
            hasBottomNavigation = true
        ) {
            items(items) { (title, status) ->
                StatusCard(
                    label = status,
                    statusColor = when (status) {
                        "Pendiente" -> Color(0xFFFB923C)
                        "Aceptada" -> Color(0xFF10B981)
                        else -> Color(0xFF64748B)
                    },
                    title = title,
                    description = "Detalles de la reserva"
                )
            }
        }
    }
}

// ==========================================
// EJEMPLO 3: LAYOUT CON SECCIONES
// ==========================================

/**
 * Ejemplo con múltiples secciones agrupadas
 * 
 * Patrón recomendado para:
 * - Organizar contenido por categoría
 * - Menús estructurados
 * - Configuraciones agrupadas
 */
@Composable
fun SectionedLayoutExample() {
    AppScaffold(
        title = "Panel de Control",
        onBackClick = { }
    ) { paddingValues ->
        CardLayout(
            modifier = Modifier.padding(paddingValues),
            hasBottomNavigation = true
        ) {
            // Sección 1: Resumen
            SectionLayout(title = "Resumen") {
                HighlightCard(
                    title = "Total de Reservas",
                    highlight = "24"
                )
            }

            // Sección 2: Estado
            SectionLayout(title = "Estado") {
                InfoCard(
                    title = "Aceptadas",
                    value = "18"
                )
                Spacer(modifier = Modifier.height(12.dp))
                InfoCard(
                    title = "Pendientes",
                    value = "6"
                )
            }

            // Sección 3: Acciones
            SectionLayout(title = "Acciones", showDivider = false) {
                ActionCard(
                    title = "¿Preparado?",
                    description = "Inicia tu jornada de trabajo",
                    actions = listOf(
                        "Cancelar" to { },
                        "Iniciar" to { }
                    )
                )
            }
        }
    }
}

// ==========================================
// EJEMPLO 4: CARDS INTERACTIVAS (EXPANDIBLES)
// ==========================================

/**
 * Ejemplo con tarjetas que se pueden expandir
 * 
 * Patrón recomendado para:
 * - Detalles que se pueden expandir/contraer
 * - Reservas con opciones
 * - Detalles de servicios
 */
@Composable
fun ExpandableCardsExample() {
    val expandedItems = remember { mutableMapOf<Int, Boolean>() }

    AppScaffold(
        title = "Detalles Expandibles",
        onBackClick = { }
    ) { paddingValues ->
        LazyCardLayout(
            modifier = Modifier.padding(paddingValues),
            hasBottomNavigation = true
        ) {
            items(5) { index ->
                val isExpanded = expandedItems[index] ?: false
                
                BaseCard(
                    modifier = Modifier.clickable {
                        expandedItems[index] = !isExpanded
                    }
                ) {
                    ExpandableCardContent(
                        isExpanded = isExpanded,
                        collapsedContent = {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Elemento $index")
                                Text(if (isExpanded) "▲" else "▼")
                            }
                        },
                        expandedContent = {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            ) {
                                Text("Elemento $index (Expandido)")
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Contenido detallado del elemento", 
                                    style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    )
                }
            }
        }
    }
}

// ==========================================
// EJEMPLO 5: ESTADOS VISUALES (CARGA, VACÍO)
// ==========================================

/**
 * Ejemplo con manejo de estados (cargando, vacío, error)
 * 
 * Patrón recomendado para:
 * - Pantallas con contenido dinámico
 * - Manejo de errores
 * - Estados de carga
 */
@Composable
fun StateManagementExample(
    state: String = "content" // "loading", "empty", "error", "content"
) {
    AppScaffold(
        title = "Gestión de Estados",
        onBackClick = { }
    ) { paddingValues ->
        CardLayout(
            modifier = Modifier.padding(paddingValues),
            hasBottomNavigation = true
        ) {
            when (state) {
                "loading" -> {
                    LoadingStateCard()
                }
                "empty" -> {
                    EmptyStateCard(
                        title = "Sin Datos",
                        description = "No hay reservas disponibles. Crea una nueva para comenzar."
                    )
                }
                "error" -> {
                    ActionCard(
                        title = "Error al Cargar",
                        description = "Ocurrió un problema al recuperar los datos.",
                        actions = listOf(
                            "Reintentar" to { }
                        )
                    )
                }
                else -> {
                    // Estado normal con contenido
                    repeat(3) {
                        InfoCard(
                            title = "Reserva #${it + 1}",
                            subtitle = "Aceptada",
                            value = "Detalles disponibles"
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// EJEMPLO 6: PATRÓN COMPLETO (PROFESIONAL)
// ==========================================

/**
 * Ejemplo completo imitando una pantalla profesional real
 * 
 * Características:
 * - AppScaffold con cabecera y botón atrás
 * - Múltiples secciones
 * - Diferentes tipos de tarjetas
 * - Acciones interactivas
 * - Manejo de estados
 */
@Composable
fun CompleteProfessionalExample() {
    val hasData = remember { mutableStateOf(true) }

    AppScaffold(
        title = "Mis Reservas",
        onBackClick = { }
    ) { paddingValues ->
        if (hasData.value) {
            LazyCardLayout(
                modifier = Modifier.padding(paddingValues),
                hasBottomNavigation = true
            ) {
                // Sección: Resumen
                item {
                    SectionLayout(title = "Resumen Rápido") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            HighlightCard(
                                title = "Activas",
                                highlight = "8",
                                modifier = Modifier.weight(1f)
                            )
                            HighlightCard(
                                title = "Completadas",
                                highlight = "24",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Sección: Reservas Activas
                item {
                    SectionLayout(title = "Reservas Activas") {}
                }

                items(3) { index ->
                    StatusCard(
                        label = "Aceptada",
                        statusColor = Color(0xFF10B981),
                        title = "Servicio de Plomería #${index + 1}",
                        description = "En progreso - Profesional: Juan García"
                    )
                }

                // Sección: Acciones
                item {
                    SectionLayout(title = "Acciones", showDivider = false) {
                        CardProvider {
                            Button(
                                onClick = { hasData.value = false },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Simular estado vacío")
                            }
                        }
                    }
                }
            }
        } else {
            CardLayout(
                modifier = Modifier.padding(paddingValues),
                hasBottomNavigation = true
            ) {
                EmptyStateCard(
                    title = "Sin Reservas",
                    description = "No tienes reservas activas. Crea una nueva solicitud de servicio."
                )

                Spacer(modifier = Modifier.height(12.dp))

                CardProvider {
                    Button(
                        onClick = { hasData.value = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver")
                    }
                }
            }
        }
    }
}

// ==========================================
// Nota: Usar el `Modifier.clickable` provisto por androidx.compose.foundation
