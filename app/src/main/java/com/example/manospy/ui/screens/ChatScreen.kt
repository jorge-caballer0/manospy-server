package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.manospy.data.model.Message
import com.example.manospy.data.local.SessionManager
import com.example.manospy.ui.navigation.Screen
import com.example.manospy.ui.viewmodel.ServiceViewModel
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.ui.draw.shadow
import java.text.SimpleDateFormat
import java.util.*
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.components.SyncStatusBarWithHeader
import com.example.manospy.ui.theme.CorporateBlue
import com.example.manospy.ui.theme.AppColors
import com.example.manospy.ui.theme.AppDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    reservationIdOrChatId: String,
    viewModel: ServiceViewModel = viewModel()
) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = remember { sessionManager.getUserId() }
    
    // Debug logging
    LaunchedEffect(userId) {
        android.util.Log.d("ChatScreen", "UserId obtenido: $userId")
    }
    
    val scope = rememberCoroutineScope()
    val reservationDetail by viewModel.reservationDetail.collectAsState()
    val messages by viewModel.messages.collectAsState()
    val messageError by viewModel.messageError.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    var messageText by remember { mutableStateOf("") }
    var showRatingScreen by remember { mutableStateOf(false) }
    var chatStartTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    // Mostrar error en Snackbar si hay
    LaunchedEffect(messageError) {
        if (messageError != null) {
            snackbarHostState.showSnackbar(
                message = messageError ?: "Error al enviar mensaje",
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessageError()
        }
    }

    // Cargar datos al iniciar: detectar si es chatId o reservationId
    LaunchedEffect(reservationIdOrChatId) {
        android.util.Log.d("ChatScreen", "LaunchedEffect: ID=$reservationIdOrChatId")
        
        if (reservationIdOrChatId.startsWith("chat_")) {
            // Es un chatId (formato: chat_10_1234567890)
            android.util.Log.d("ChatScreen", "Detectado como chatId, cargando mensajes del chat")
            viewModel.fetchChatMessages(reservationIdOrChatId)
        } else {
            // Asumir que es un reservationId, intentar cargarlo
            android.util.Log.d("ChatScreen", "Intentando cargar como reservationId")
            viewModel.fetchReservationDetail(reservationIdOrChatId)

            // Esperar brevemente a que termine
            kotlinx.coroutines.delay(100)

            if (viewModel.reservationDetail.value != null) {
                // Es una reserva formal
                android.util.Log.d("ChatScreen", "Cargado como reservación, obteniendo mensajes")
                viewModel.fetchMessages(reservationIdOrChatId)
            } else {
                // Probablemente es un chatId que llegó sin el prefijo "chat_"
                android.util.Log.d("ChatScreen", "No es una reservación, intentando como chatId")
                viewModel.fetchChatMessages(reservationIdOrChatId)
            }
        }
    }

    // Auto-scroll al último mensaje
    LaunchedEffect(messages) {
        if (messages is NetworkResult.Success && (messages as NetworkResult.Success<List<Message>>).data.isNotEmpty()) {
            listState.animateScrollToItem((messages as NetworkResult.Success<List<Message>>).data.size - 1)
            
            // Marcar mensajes recibidos como leídos
            val messagesList = (messages as NetworkResult.Success<List<Message>>).data
            messagesList.forEach { msg ->
                if (msg.senderId != userId && msg.readStatus != "read") {
                    viewModel.markMessageAsRead(msg.id)
                }
            }
        }
    }

    // Timer de 5 minutos para auto-finalizar chat
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(5 * 60 * 1000) // 5 minutos
        val professionalName = reservationDetail?.professionalName ?: "Profesional"
        navController.navigate(Screen.ChatRating.createRoute(reservationIdOrChatId, professionalName))
    }

    // Si presiona Finalizar, navegar a pantalla de rating
    LaunchedEffect(showRatingScreen) {
        if (showRatingScreen) {
            val professionalName = reservationDetail?.professionalName ?: "Profesional"
            navController.navigate(Screen.ChatRating.createRoute(reservationIdOrChatId, professionalName))
        }
    }

    AppScaffold(
        title = "Mensajes",
        onBackClick = { navController.popBackStack() }
    ) { innerPadding ->
        // Mantener sincronización de status bar con la cabecera
        SyncStatusBarWithHeader(headerColor = CorporateBlue)

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            modifier = Modifier.fillMaxSize()
        ) { snackbarPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
                    .padding(innerPadding)
                    .padding(snackbarPadding)
            ) {
            when (val msgResult = messages) {
                is NetworkResult.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is NetworkResult.Success -> {
                    if (msgResult.data.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.ChatBubbleOutline,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = Color(0xFF94A3B8)
                                )
                                Text(
                                    "Sin mensajes aún",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    "Inicia una conversación con el profesional",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF94A3B8),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(msgResult.data) { message ->
                                ChatBubble(message, userId)
                            }
                        }
                    }
                }
                is NetworkResult.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Error al cargar mensajes: ${msgResult.message}",
                            color = Color.Red,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {}
            }

            // Bottom actions & input
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                // Acciones superior (minimalista y compacta)
                Surface(
                    color = Color(0xFFF8FAFC),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Botón Confirmar (primario)
                        Button(
                            onClick = {
                                scope.launch {
                                    if (viewModel.reservationDetail.value != null) {
                                        navController.navigate(com.example.manospy.ui.navigation.Screen.ReservationAccepted.createRoute(reservationIdOrChatId))
                                    } else {
                                        // Convertir chat a reservación
                                        val result = viewModel.convertChatToReservation(reservationIdOrChatId)
                                        if (result is NetworkResult.Success) {
                                            val reservationId = result.data.reservationId
                                            // Cambiar estado a IN_PROGRESS porque ya se coordinó en el chat
                                            viewModel.updateReservationStatus(reservationId, "IN_PROGRESS")
                                            // Esperar un poco para que se procese
                                            kotlinx.coroutines.delay(500)
                                            navController.navigate(com.example.manospy.ui.navigation.Screen.ReservationAccepted.createRoute(reservationId))
                                        } else {
                                            android.util.Log.e("ChatScreen", "Error al convertir chat: ${(result as? NetworkResult.Error)?.message}")
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .height(40.dp)
                                .weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0056D2)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Confirmar",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                fontSize = 11.sp
                            )
                        }

                        // Botón Cancelar (secundario)
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    if (viewModel.reservationDetail.value != null) {
                                        viewModel.cancelReservation(reservationIdOrChatId)
                                    }
                                    navController.popBackStack()
                                }
                            },
                            modifier = Modifier
                                .height(40.dp)
                                .weight(1f),
                            border = BorderStroke(1.dp, Color(0xFFDC2626).copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Cancelar",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Campo de mensaje (mejorado)
                Surface(
                    color = Color.White,
                    shadowElevation = 6.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            placeholder = {
                                Text(
                                    "Escribe un mensaje...",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = Color(0xFF94A3B8)
                                    )
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF1F5F9),
                                unfocusedContainerColor = Color(0xFFF1F5F9),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )

                        IconButton(
                            onClick = {
                                if (messageText.isNotBlank()) {
                                    if (viewModel.reservationDetail.value != null) {
                                        viewModel.sendMessage(reservationIdOrChatId, messageText, userId)
                                    } else {
                                        viewModel.sendChatMessage(reservationIdOrChatId, messageText, userId)
                                    }
                                    messageText = ""
                                }
                            },
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color(0xFF137FEC), CircleShape),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Enviar",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
    }
}

@Composable
fun ChatBubble(message: Message, clientId: String) {
    // Usamos el campo 'text' o 'content' según esté definido en Models.kt
    // Nota: He visto Message con 'text' y Message con 'content' en diferentes búsquedas.
    // Vamos a usar 'text' que es lo que vi en Models.kt.
    
    val isOwn = message.senderId == clientId
    val brandBlue = Color(0xFF137FEC)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isOwn) Arrangement.End else Arrangement.Start,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            if (!isOwn) {
                Surface(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape),
                    shape = CircleShape,
                    color = Color(0xFFE0E7FF),
                    shadowElevation = 1.dp
                ) {
                    Icon(
                        Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp),
                        tint = brandBlue
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
            }

            Surface(
                shape = RoundedCornerShape(
                    topStart = if (isOwn) 18.dp else 4.dp,
                    topEnd = if (isOwn) 4.dp else 18.dp,
                    bottomStart = 18.dp,
                    bottomEnd = 18.dp
                ),
                color = if (isOwn) brandBlue else Color.White,
                shadowElevation = if (isOwn) 3.dp else 2.dp,
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    color = if (isOwn) Color.White else Color(0xFF0F172A),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        lineHeight = 20.sp
                    )
                )
            }

            if (isOwn) {
                Spacer(modifier = Modifier.width(4.dp))
                // Tildes de lectura (solo en mensajes propios)
                val readStatusText = when (message.readStatus) {
                    "read" -> "✓✓" // dos tildes (leído)
                    "delivered" -> "✓" // un tilde (entregado)
                    else -> "•" // punto para enviado
                }
                Text(
                    text = readStatusText,
                    fontSize = 9.sp,
                    color = if (message.readStatus == "read") brandBlue else Color(0xFF64748B),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Bottom)
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(top = 4.dp, start = if (isOwn) 0.dp else 32.dp, end = if (isOwn) 32.dp else 0.dp)
                .align(if (isOwn) Alignment.End else Alignment.Start),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formatMessageTime(message.timestamp),
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}

private fun formatMessageTime(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("HH:mm", Locale("es"))
    return format.format(date)
}
