package com.example.manospy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * PATRONES DE LAYOUT RESPONSIVOS
 * 
 * Contenedores optimizados para distribuir tarjetas correctamente,
 * adaptándose a cualquier tamaño de pantalla y considerando
 * la presencia de navbar inferior.
 */

// ==========================================
// CARD LAYOUT - Contenedor con scroll
// ==========================================

/**
 * CardLayout - Contenedor base con scroll para tarjetas
 * 
 * Proporciona un layout consistente con:
 * - Scroll vertical automático
 * - Padding correcto entre tarjetas (12dp)
 * - Margin inferior para navbar (si existe)
 * - Fondo consistente
 * - Espacio reservado para navbar (80dp)
 *
 * @param modifier Modificador principal
 * @param hasBottomNavigation True si debe reservar espacio para navbar
 * @param backgroundColor Color de fondo del contenedor
 * @param contentPadding Padding de contenido vertical (default: 16dp)
 * @param verticalSpacing Espacio entre tarjetas (default: 12dp)
 * @param content Composable con las tarjetas
 */
@Composable
fun CardLayout(
    modifier: Modifier = Modifier,
    hasBottomNavigation: Boolean = true,
    backgroundColor: Color = Color(0xFFF8FAFC),
    contentPadding: Int = 16,
    verticalSpacing: Int = 12,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
    ) {
        // Padding superior
        Spacer(modifier = Modifier.height(contentPadding.dp))

        // Contenido
        content()

        // Padding inferior (extra para navbar si existe)
        Spacer(modifier = Modifier.height(
            if (hasBottomNavigation) (contentPadding + 80).dp else contentPadding.dp
        ))
    }
}

// ==========================================
// LAZY CARD LAYOUT - Para listas largas
// ==========================================

/**
 * LazyCardLayout - Similar a CardLayout pero optimizado para listas largas
 * 
 * Usa LazyColumn internamente, mejor para:
 * - Listas con muchos elementos (>20)
 * - Scroll performance crítico
 * - Datos dinámicos que se cargan bajo demanda
 *
 * @param modifier Modificador principal
 * @param hasBottomNavigation True si debe reservar espacio para navbar
 * @param backgroundColor Color de fondo
 * @param contentPadding Padding de contenido vertical (default: 16dp)
 * @param verticalSpacing Espacio entre tarjetas (default: 12dp)
 * @param content Lambda con LazyListScope para agregar items
 */
@Composable
fun LazyCardLayout(
    modifier: Modifier = Modifier,
    hasBottomNavigation: Boolean = true,
    backgroundColor: Color = Color(0xFFF8FAFC),
    contentPadding: Int = 16,
    verticalSpacing: Int = 12,
    content: LazyListScope.() -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        verticalArrangement = Arrangement.spacedBy(verticalSpacing.dp),
        contentPadding = PaddingValues(
            top = contentPadding.dp,
            bottom = if (hasBottomNavigation) (contentPadding + 80).dp else contentPadding.dp,
            start = 0.dp,
            end = 0.dp
        ),
        content = content
    )
}

// ==========================================
// GRID CARD LAYOUT - Para grid de tarjetas
// ==========================================

/**
 * GridCardLayout - Contenedor para distribuir tarjetas en columnas
 * 
 * Automáticamente distribuye tarjetas en 1, 2 o más columnas
 * dependiendo del espacio vertical disponible.
 *
 * @param modifier Modificador principal
 * @param columns Número de columnas (default: calcula automáticamente)
 * @param hasBottomNavigation True si debe reservar espacio para navbar
 * @param content Composable con las tarjetas
 */
@Composable
fun GridCardLayout(
    modifier: Modifier = Modifier,
    columns: Int = 1,
    hasBottomNavigation: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                vertical = 16.dp,
                horizontal = 16.dp
            )
    ) {
        content()

        // Padding inferior adicional para navbar
        if (hasBottomNavigation) {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

// ==========================================
// SECTION LAYOUT - Agrupar tarjetas por sección
// ==========================================

/**
 * SectionLayout - Agrupa tarjetas bajo un encabezado de sección
 * 
 * Útil para:
 * - Agrupar tarjetas relacionadas
 * - Mostrar títulos de sección
 * - Mantener consistencia visual
 *
 * @param title Título de la sección
 * @param showDivider Mostrar línea divisora después (default: true)
 * @param content Tarjetas dentro de la sección
 */
@Composable
fun SectionLayout(
    title: String,
    showDivider: Boolean = true,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Título de sección
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
            color = androidx.compose.ui.graphics.Color(0xFF94A3B8)
        )

        // Contenido
        content()

        // Divisor opcional
        if (showDivider) {
            DividerCard(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

// ==========================================
// EXPANDABLE CARD LAYOUT - Tarjetas expandibles
// ==========================================

/**
 * ExpandableCardContent - Contenedor para tarjetas expandibles
 * 
 * Proporciona estructura consistente para tarjetas que 
 * pueden expandirse/contraerse.
 *
 * @param isExpanded Estado de expansión
 * @param modifier Modificador principal
 * @param collapsedContent Contenido cuando está contraída
 * @param expandedContent Contenido cuando está expandida
 */
@Composable
fun ExpandableCardContent(
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    collapsedContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (isExpanded) {
            expandedContent()
        } else {
            collapsedContent()
        }
    }
}

// ==========================================
// CARD PROVIDER - Envases para patrones comunes
// ==========================================

/**
 * CardProvider - Wrapper para mantener márgenes consistentes
 * 
 * Asegura que todas las tarjetas tengan márgenes uniformes (16dp)
 * sin importar dónde se usen.
 *
 * @param modifier Modificador adicional
 * @param content Tarjeta o tarjetas internas
 */
@Composable
fun CardProvider(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        content()
    }
}

// ==========================================
// EMPTY STATE CARD - Para estados vacíos
// ==========================================

/**
 * EmptyStateCard - Tarjeta para mostrar estado vacío
 * 
 * Proporciona feedback visual cuando no hay contenido.
 *
 * @param title Título del estado vacío
 * @param description Descripción adicional
 * @param modifier Modificador adicional
 */
@Composable
fun EmptyStateCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    BaseCard(
        modifier = modifier,
        backgroundColor = Color(0xFFF8FAFC)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = androidx.compose.ui.graphics.Color(0xFF1F2937),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = androidx.compose.ui.graphics.Color(0xFF64748B)
            )
        }
    }
}

// ==========================================
// LOADING STATE CARD - Para estados de carga
// ==========================================

/**
 * LoadingStateCard - Tarjeta para mostrar estado de carga
 * 
 * Proporciona feedback visual durante carga de datos.
 *
 * @param modifier Modificador adicional
 */
@Composable
fun LoadingStateCard(
    modifier: Modifier = Modifier
) {
    BaseCard(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                strokeWidth = 2.dp,
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary
            )
        }
    }
}
