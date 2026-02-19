package com.example.manospy.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data class para representar items de la BottomNavigationBar
 * 
 * @param id Identificador único del item
 * @param label Texto mostrado debajo del ícono (en español)
 * @param icon Ícono a mostrar
 * @param route Ruta de destino en el NavController
 * @param isCentralButton Si es el botón central destacado (Solicitar/Chat dinámico)
 */
data class BottomNavItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val route: String,
    val isCentralButton: Boolean = false
)

/**
 * Objeto que contiene todas las configuraciones de items de navegación
 * Soporta cambio dinámico del botón central entre Solicitar y Chat
 */
object BottomNavItems {
    
    /**
     * Items cuando NO hay solicitud activa
     * El botón central es Solicitar (+)
     */
    fun clientItemsWithoutActiveRequest(): List<BottomNavItem> = listOf(
        BottomNavItem(
            id = "inicio",
            label = "Inicio",
            icon = Icons.Filled.Home,
            route = "client_home"
        ),
        BottomNavItem(
            id = "solicitar",
            label = "Solicitar",
            icon = Icons.Filled.Add,
            route = "client_solicitar",
            isCentralButton = true // ⭐ BOTÓN CENTRAL DESTACADO
        ),
        BottomNavItem(
            id = "historial",
            label = "Historial",
            icon = Icons.Filled.History,
            route = "client_reservas"
        ),
        BottomNavItem(
            id = "chat",
            label = "Chat",
            icon = Icons.Filled.Message,
            route = "client_chat"
        ),
        BottomNavItem(
            id = "ajustes",
            label = "Ajustes",
            icon = Icons.Filled.Settings,
            route = "client_perfil"
        )
    )
    
    /**
     * Items cuando HAY solicitud aceptada
     * El botón central es Chat (destacado)
     * El tab Solicitar pasa a posición normal
     */
    fun clientItemsWithActiveRequest(): List<BottomNavItem> = listOf(
        BottomNavItem(
            id = "inicio",
            label = "Inicio",
            icon = Icons.Filled.Home,
            route = "client_home"
        ),
        BottomNavItem(
            id = "solicitar",
            label = "Solicitar",
            icon = Icons.Filled.Add,
            route = "client_solicitar",
            isCentralButton = false // Normal, no destacado
        ),
        BottomNavItem(
            id = "chat",
            label = "Chat",
            icon = Icons.Filled.Message,
            route = "client_chat",
            isCentralButton = true // ⭐ BOTÓN CENTRAL DESTACADO
        ),
        BottomNavItem(
            id = "historial",
            label = "Historial",
            icon = Icons.Filled.History,
            route = "client_reservas"
        ),
        BottomNavItem(
            id = "ajustes",
            label = "Ajustes",
            icon = Icons.Filled.Settings,
            route = "client_perfil"
        )
    )
    
    /**
     * Items para profesionales
     */
    fun professionalItems(): List<BottomNavItem> = listOf(
        BottomNavItem(
            id = "inicio",
            label = "Inicio",
            icon = Icons.Filled.Home,
            route = "prof_home"
        ),
        BottomNavItem(
            id = "solicitudes",
            label = "Solicitudes",
            icon = Icons.Filled.Assignment,
            route = "prof_solicitudes"
        ),
        BottomNavItem(
            id = "reservas",
            label = "Reservas",
            icon = Icons.Filled.EventNote,
            route = "prof_reservas"
        ),
        BottomNavItem(
            id = "chat",
            label = "Chat",
            icon = Icons.Filled.Message,
            route = "prof_chat"
        ),
        BottomNavItem(
            id = "perfil",
            label = "Perfil",
            icon = Icons.Filled.Person,
            route = "prof_perfil"
        )
    )
}
