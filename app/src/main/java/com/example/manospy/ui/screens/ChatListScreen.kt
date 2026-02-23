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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import com.example.manospy.util.NetworkResult
import com.example.manospy.data.model.Message
import com.example.manospy.data.model.ChatListItem
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

    // Cargar chats reales del ViewModel
    val chatsResult = if (serviceViewModel != null) {
        serviceViewModel.chats.collectAsState(initial = NetworkResult.Loading).value
    } else {
        NetworkResult.Loading
    }

    // Cargar chats al iniciar la pantalla
    LaunchedEffect(Unit) {
        serviceViewModel?.fetchChats()
    }

    // Extraer lista de chats del resultado
    val chatList = remember(chatsResult) {
        when (chatsResult) {
            is NetworkResult.Success<*> -> {
                try {
                    (chatsResult as NetworkResult.Success<List<ChatListItem>>).data
                } catch (e: Exception) {
                    emptyList()
                }
            }
            else -> emptyList()
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    
    // Filtrar chats según búsqueda
    val filteredChats = remember(chatList, searchQuery) {
        if (searchQuery.isBlank()) {
            chatList
        } else {
            chatList.filter { chat ->
                (chat.professional?.name ?: "").contains(searchQuery, ignoreCase = true) ||
                chat.lastMessage.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    AppScaffold(title = "Mensajes", onBackClick = null) { innerPadding ->
        // Mantener sincronización de status bar con color de cabecera
        SyncStatusBarWithHeader(headerColor = Color(0xFF2563EB))

        when (chatsResult) {
            is NetworkResult.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = primaryBlue)
                }
            }
            is NetworkResult.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Error al cargar mensajes",
                        color = Color.Red
                    )
                }
            }
            else -> {
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
    }
}

// Nueva función componible para renderizar items de chat con datos reales
@Composable
private fun ChatItemRow(
    chat: ChatListItem,
    brandBlue: Color,
    cardBg: Color,
    textPrimary: Color,
    textSecondary: Color,
    navController: androidx.navigation.NavController?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                navController?.navigate(com.example.manospy.ui.navigation.Screen.Chat.createRoute(chat.id))
            }
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        color = cardBg,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar del profesional
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
                shape = CircleShape,
                color = Color(0xFFE0E7FF),
                shadowElevation = 2.dp
            ) {
                if (chat.professional?.profilePhotoUrl != null) {
                    AsyncImage(
                        model = chat.professional.profilePhotoUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = brandBlue
                        )
                    }
                }
            }

            // Datos del chat
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = chat.professional?.name ?: "Profesional",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary,
                    maxLines = 1
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    // Tildes de lectura
                    val readStatusText = when (chat.lastMessageReadStatus) {
                        "read" -> "✓✓" // dos tildes (leído)
                        "delivered" -> "✓" // un tilde (entregado)
                        else -> "" // sin tilde o enviado
                    }
                    
                    if (readStatusText.isNotEmpty()) {
                        Text(
                            text = readStatusText,
                            fontSize = 10.sp,
                            color = if (chat.lastMessageReadStatus == "read") brandBlue else textSecondary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = chat.lastMessage,
                        fontSize = 12.sp,
                        color = textSecondary,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Timestamp
            Text(
                text = formatTimestamp(chat.lastMessageTime),
                fontSize = 10.sp,
                color = textSecondary
            )
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
            else → "Hace una semana"
        }
    }
}
