package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors
import com.example.manospy.ui.theme.CorporateBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalChatScreen() {
    val brandBlue = Color(0xFF0EA5E9)
    val bgLight = Color(0xFFF8FAFC)
    val textDark = Color(0xFF0F172A)
    val textGray = Color(0xFF64748B)

    AppScaffold(
        title = "Mensajes",
        onBackClick = { }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                reverseLayout = false,
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            "MONDAY, JUNE 12",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B),
                                letterSpacing = 1.sp
                            )
                        )
                    }
                }
                
                items(chatHistoryMocks) { msg ->
                    ChatBubbleProfessional(msg, brandBlue, textGray, textDark)
                }
            }

            // Bottom input migrated from bottomBar
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .imePadding()
                    .fillMaxWidth()
            ) {
                Surface(
                    tonalElevation = 8.dp,
                    color = Color.White,
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(44.dp).background(Color(0xFFF1F5F9), CircleShape)
                        ) {
                            Icon(Icons.Default.Add, null, tint = textGray)
                        }
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        TextField(
                            value = "",
                            onValueChange = {},
                            placeholder = { Text("Type a message...", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.weight(1f).heightIn(min = 44.dp),
                            shape = RoundedCornerShape(22.dp),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF8FAFC),
                                unfocusedContainerColor = Color(0xFFF8FAFC),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                disabledIndicatorColor = Color.Transparent
                            )
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(48.dp).background(brandBlue, CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, null, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubbleProfessional(
    msg: ChatMessageMock,
    brandBlue: Color,
    textGray: Color,
    textDark: Color
) {
    val isMe = msg.isMe
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMe) {
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.LightGray)) {
                Icon(Icons.Default.Person, null, modifier = Modifier.align(Alignment.Center).size(18.dp), tint = Color.White)
            }
            Spacer(Modifier.width(8.dp))
        }
        
        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
            Surface(
                color = if (isMe) brandBlue else Color.White,
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isMe) 20.dp else 4.dp,
                    bottomEnd = if (isMe) 4.dp else 20.dp
                ),
                tonalElevation = if (isMe) 0.dp else 1.dp
            ) {
                Text(
                    text = msg.text,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = if (isMe) Color.White else textDark,
                        lineHeight = 20.sp
                    )
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(msg.time, style = MaterialTheme.typography.labelSmall.copy(color = textGray))
                if (isMe) {
                    Spacer(Modifier.width(4.dp))
                    Icon(Icons.Default.DoneAll, null, tint = brandBlue, modifier = Modifier.size(14.dp))
                }
            }
        }
        
        if (isMe) {
            Spacer(Modifier.width(8.dp))
            Box(modifier = Modifier.size(32.dp).clip(CircleShape).background(Color.LightGray)) {
                Icon(Icons.Default.Person, null, modifier = Modifier.align(Alignment.Center).size(18.dp), tint = Color.White)
            }
        }
    }
}

data class ChatMessageMock(val text: String, val time: String, val isMe: Boolean)

val chatHistoryMocks = listOf(
    ChatMessageMock(
        "Hello! I saw your portfolio on MANOSPY. I have a question regarding the service project scope for the upcoming villa renovation.",
        "10:42 AM",
        false
    ),
    ChatMessageMock(
        "Of course! I'm here to help. What specific details would you like to discuss? I'm free to go over the floor plans now.",
        "10:45 AM",
        true
    ),
    ChatMessageMock(
        "Can we adjust the timeline for the final delivery? We're hoping to move the date up by two weeks if possible.",
        "10:48 AM",
        false
    ),
    ChatMessageMock(
        "That's a tight squeeze, but if we finalize the material selection by Friday, I can definitely make it work.",
        "10:50 AM",
        true
    )
)
