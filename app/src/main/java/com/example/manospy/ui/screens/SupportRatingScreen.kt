package com.example.manospy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.example.manospy.data.api.RetrofitClient
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.R
import androidx.compose.ui.platform.LocalContext
import com.example.manospy.data.local.SessionManager

@Composable
fun SupportRatingScreen(
    agentName: String = "Carlos Rodriguez",
    agentId: String = "1",
    onSend: (rating: Int, tags: List<String>, comment: String) -> Unit,
    onBack: () -> Unit
) {
    val tagsList = listOf("Amable", "Resolvió mi duda", "Rápido", "Claro", "Paciente")
    var rating by remember { mutableStateOf(4) }
    var selectedTags by remember { mutableStateOf(listOf("Resolvió mi duda")) }
    var comment by remember { mutableStateOf("") }
    var sending by remember { mutableStateOf(false) }
    var sendSuccess by remember { mutableStateOf(false) }
    var sendError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val userId = sessionManager.getUserId() ?: "1"
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7F8))
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 16.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    tint = Color(0xFF64748B)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Calificar Soporte",
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F172A)),
                modifier = Modifier.weight(8f)
            )
            Spacer(modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Check icon and thank you
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFEFF6FF), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_check_circle),
                    contentDescription = "Check",
                    tint = Color(0xFF137FEC),
                    modifier = Modifier.size(48.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("¡Muchas gracias!", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF0F172A))
            Text("Tu opinión nos ayuda a mejorar", fontSize = 15.sp, color = Color(0xFF64748B))
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Agent info
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .border(1.dp, Color(0xFFF1F5F9), shape = RoundedCornerShape(24.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFDBEAFE), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = "Person",
                    tint = Color(0xFF137FEC),
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("Atendido por", fontSize = 12.sp, color = Color(0xFF64748B))
                Text(agentName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .background(Color(0xFFD1FADF), shape = RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_verified),
                    contentDescription = "Especialista",
                    tint = Color(0xFF22C55E),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text("Especialista", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color(0xFF16A34A))
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        // Rating stars
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¿Cómo calificarías el servicio?", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Color(0xFF334155))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..5).forEach { i ->
                    Icon(
                        painter = painterResource(id = R.drawable.ic_star),
                        contentDescription = "Star $i",
                        tint = if (i <= rating) Color(0xFFFBBF24) else Color(0xFFE2E8F0),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { rating = i }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        // Tags
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "¿QUÉ FUE LO QUE MÁS TE GUSTÓ?",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            @OptIn(ExperimentalLayoutApi::class)
            FlowRow(
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                tagsList.forEach { tag ->
                    val selected = tag in selectedTags
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(if (selected) Color(0xFF137FEC).copy(alpha = 0.1f) else Color.White)
                            .border(
                                1.dp,
                                if (selected) Color(0xFF137FEC) else Color(0xFFE2E8F0),
                                CircleShape
                            )
                            .clickable {
                                selectedTags = if (selected) selectedTags - tag else selectedTags + tag
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            tag,
                            color = if (selected) Color(0xFF137FEC) else Color(0xFF64748B),
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        // Comment
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "COMENTARIOS ADICIONALES (OPCIONAL)",
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            BasicTextField(
                value = comment,
                onValueChange = { comment = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(Color.White, shape = RoundedCornerShape(18.dp))
                    .border(1.dp, Color(0xFFF1F5F9), shape = RoundedCornerShape(18.dp))
                    .padding(12.dp),
                textStyle = TextStyle(fontSize = 15.sp, color = Color(0xFF334155)),
                decorationBox = { innerTextField ->
                    if (comment.isEmpty()) {
                        Text("Cuéntanos más sobre tu experiencia...", color = Color(0xFF94A3B8), fontSize = 15.sp)
                    }
                    innerTextField()
                }
            )
        }
        Spacer(modifier = Modifier.height(18.dp))
        // Send button
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        sending = true
                        sendError = null
                        try {
                            val response = RetrofitClient.apiService.rateSupport(
                                mapOf(
                                    "user_id" to userId,
                                    "agent_id" to agentId,
                                    "rating" to rating,
                                    "tags" to selectedTags,
                                    "comment" to comment
                                )
                            )
                            if (response.isSuccessful) {
                                sendSuccess = true
                                onSend(rating, selectedTags, comment)
                            } else {
                                sendError = response.errorBody()?.string() ?: "Error desconocido"
                            }
                        } catch (e: Exception) {
                            sendError = e.message
                        }
                        sending = false
                    }
                },
                enabled = !sending,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF137FEC))
            ) {
                if (sending) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(28.dp))
                } else {
                    Text("Enviar Calificación", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
            if (sendError != null) {
                Text(sendError!!, color = Color.Red, fontSize = 14.sp)
            }
            if (sendSuccess) {
                Text("¡Calificación enviada!", color = Color(0xFF22C55E), fontSize = 16.sp)
            }
        }
    }
}
