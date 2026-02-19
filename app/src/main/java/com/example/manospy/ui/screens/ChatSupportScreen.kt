package com.example.manospy.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Dialog
import java.text.SimpleDateFormat
import java.util.*
import com.example.manospy.ui.navigation.Screen

// Modelo de mensaje
sealed class ChatMessage {
    data class Text(val text: String, val sender: String, val timestamp: String) : ChatMessage()
    data class Image(val uri: Uri, val sender: String, val timestamp: String) : ChatMessage()
    object Typing : ChatMessage()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatSupportScreen(
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

    var messages by remember { mutableStateOf(listOf<ChatMessage>()) }
    var input by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }
    var showImageDialog by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador para seleccionar imagen
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            messages = messages + ChatMessage.Image(uri, "user", getCurrentTime())
        }
    }

    // Mensaje inicial mínimo (chat vacío con nota de sistema)
    LaunchedEffect(Unit) {
        messages = listOf(
            ChatMessage.Text("Te has unido al chat. Un agente te atenderá en breve.", "system", "")
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgLight)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header Azul
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .shadow(4.dp, RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)),
                color = Color(0xFF2563EB),
                shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = { onBack() },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        "Soporte ManosPy",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Mensajes
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                messages.forEach { msg ->
                    when (msg) {
                        is ChatMessage.Text -> {
                            ChatBubble(
                                text = msg.text,
                                sender = msg.sender,
                                timestamp = msg.timestamp,
                                primary = primary,
                                white = white
                            )
                        }
                        is ChatMessage.Image -> {
                                ImageBubble(
                                    uri = msg.uri,
                                    sender = msg.sender,
                                    timestamp = msg.timestamp,
                                    primary = primary,
                                    white = white
                                )
                        }
                        ChatMessage.Typing -> {
                            TypingBubble(white)
                        }
                    }
                }
            }

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(white)
                    .padding(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Adjuntar",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = input,
                    onValueChange = { input = it },
                    placeholder = { Text("Escribe un mensaje...") },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFFF3F4F6),
                        focusedContainerColor = Color(0xFFF3F4F6),
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (input.isNotBlank()) {
                            messages = messages + ChatMessage.Text(input, "user", getCurrentTime())
                            input = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(primary, RoundedCornerShape(16.dp))
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Enviar",
                        tint = white,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(
    text: String,
    sender: String,
    timestamp: String,
    primary: Color,
    white: Color
) {
    val isUser = sender == "user"
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .background(
                    if (isUser) primary else white,
                    RoundedCornerShape(24.dp)
                )
                .padding(16.dp)
        ) {
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = if (isUser) white else Color(0xFF1E293B),
                    fontSize = 15.sp
                )
            )
            if (timestamp.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    timestamp,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = Color(0xFF9CA3AF)
                    ),
                    modifier = Modifier.align(if (isUser) Alignment.End else Alignment.Start)
                )
            }
        }
    }
}

@Composable
private fun ImageBubble(
    uri: Uri,
    sender: String,
    timestamp: String,
    primary: Color,
    white: Color
) {
    val isUser = sender == "user"
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .background(
                    if (isUser) primary else white,
                    RoundedCornerShape(24.dp)
                )
                .padding(8.dp)
        ) {
            // Mostrar imagen
            Text("[Imagen enviada]", color = if (isUser) white else Color(0xFF1E293B))
            if (timestamp.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    timestamp,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 10.sp,
                        color = Color(0xFF9CA3AF)
                    ),
                    modifier = Modifier.align(if (isUser) Alignment.End else Alignment.Start)
                )
            }
        }
    }
}

@Composable
private fun TypingBubble(white: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Row(
            modifier = Modifier
                .background(white, RoundedCornerShape(24.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(3) {
                Surface(
                    modifier = Modifier.size(8.dp),
                    shape = CircleShape,
                    color = Color(0xFFCBD5E1)
                ) {}
            }
        }
    }
}

private fun getCurrentTime(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date())
}
