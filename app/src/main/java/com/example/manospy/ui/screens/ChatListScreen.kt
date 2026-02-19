package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import com.example.manospy.util.NetworkResult
import com.example.manospy.data.model.Message
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.ui.components.SimpleLocationSearchField
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.components.SyncStatusBarWithHeader
import com.example.manospy.ui.theme.AppColors
import com.example.manospy.ui.theme.AppDimensions

data class ChatItem(
    val id: String,
    val name: String,
    val lastMessage: String,
    val timestamp: String,
    val unread: Int = 0,
    val avatar: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    isClient: Boolean = true,
    navController: androidx.navigation.NavController? = null,
    serviceViewModel: ServiceViewModel? = null
) {
    val primaryBlue = AppColors.PrimaryBlue
    val brandBlue = AppColors.BrandBlue
    val accentCyan = AppColors.AccentCyan
    val textPrimary = AppColors.TextPrimary
    val textSecondary = AppColors.TextSecondary
    val bgLight = AppColors.BgLight
    val cardBg = AppColors.BgWhite
    val context = LocalContext.current

    // Cargar datos reales del ViewModel si está disponible
    val messagesResult = if (serviceViewModel != null) {
        serviceViewModel.messages.collectAsState(initial = NetworkResult.Loading).value
    } else {
        NetworkResult.Loading
    }
    
    // Transformar mensajes en chats únicos
    val chatList = remember(messagesResult) {
        when (messagesResult) {
            is NetworkResult.Success<*> -> {
                try {
                    val messages = (messagesResult as NetworkResult.Success<List<Message>>).data
                    if (messages.isEmpty()) {
                        emptyList()
                    } else {
                        messages
                            .groupBy { it.senderId }
                            .map { (senderId, msgs) ->
                                val lastMsg = msgs.lastOrNull()
                                ChatItem(
                                    id = senderId,
                                    name = "Usuario ${senderId.take(4)}",
                                    lastMessage = lastMsg?.content ?: "",
                                    timestamp = formatTimestamp(lastMsg?.timestamp ?: 0),
                                    unread = 0
                                )
                            }
                    }
                } catch (e: Exception) {
                    emptyList()
                }
            }
            else -> {
                // No mostrar datos de demostración - esperar a que carguen datos reales
                emptyList()
            }
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    
    // Filtrar chats según búsqueda
    val filteredChats = remember(chatList, searchQuery) {
        if (searchQuery.isBlank()) {
            chatList
        } else {
            chatList.filter { chat ->
                chat.name.contains(searchQuery, ignoreCase = true) ||
                chat.lastMessage.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    AppScaffold(title = "Mensajes", onBackClick = null) { innerPadding ->
        // Mantener sincronización de status bar con color de cabecera
        SyncStatusBarWithHeader(headerColor = Color(0xFF2563EB))

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Barra de búsqueda mejorada
            item {
                SimpleLocationSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Buscar conversación...",
                    enabled = true,
                    leadingIconType = "search"  // Usar icono de búsqueda en lugar de ubicación
                )
            }

            if (chatList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = AppDimensions.SpaceXLarge),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "📭",
                                fontSize = 48.sp
                            )
                            Text(
                                "Sin conversaciones aún",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textPrimary,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Tus chats aparecerán aquí",
                                style = MaterialTheme.typography.labelSmall,
                                color = textSecondary
                            )
                        }
                    }
                }
            } else {
                if (filteredChats.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No se encontraron mensajes",
                                style = MaterialTheme.typography.bodyMedium,
                                color = textSecondary
                            )
                        }
                    }
                } else {
                    items(filteredChats) { chat ->
                        ChatItemRow(
                            chat = chat, 
                            brandBlue = brandBlue, 
                            cardBg = cardBg, 
                            textPrimary = textPrimary, 
                            textSecondary = textSecondary,
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatItemRow(
    chat: ChatItem,
    brandBlue: Color,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    navController: androidx.navigation.NavController? = null
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { 
                navController?.navigate("chat/${chat.id}")
            }
            .shadow(2.dp),
        color = cardBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                color = brandBlue.copy(alpha = 0.2f)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        chat.name.first().toString(),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = brandBlue
                        )
                    )
                }
            }

            // Contenido
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    chat.name,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = textPrimary
                    ),
                    maxLines = 1
                )
                Text(
                    chat.lastMessage,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = textSecondary,
                        fontSize = 12.sp
                    ),
                    maxLines = 1
                )
            }

            // Tiempo y badge
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    chat.timestamp,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = textSecondary,
                        fontSize = 11.sp
                    )
                )
                if (chat.unread > 0) {
                    Surface(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape),
                        color = Color(0xFFFF6B6B)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                chat.unread.toString(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    return if (timestamp == 0L) {
        "Ahora"
    } else {
        val now = System.currentTimeMillis()
        val diffMs = now - timestamp
        val diffMins = diffMs / (1000 * 60)
        val diffHours = diffMs / (1000 * 60 * 60)
        val diffDays = diffMs / (1000 * 60 * 60 * 24)
        
        when {
            diffMins < 1 -> "Ahora"
            diffMins < 60 -> "Hace $diffMins min"
            diffHours < 24 -> "Hace $diffHours h"
            diffDays < 7 -> "Hace $diffDays días"
            else -> "Hace una semana"
        }
    }
}
