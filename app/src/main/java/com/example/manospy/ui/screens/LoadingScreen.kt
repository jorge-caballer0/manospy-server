package com.example.manospy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 🛡️ Escudo de seguridad
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Shield Icon",
                tint = Color(0xFF0D9488),
                modifier = Modifier.size(64.dp)
            )

            Spacer(Modifier.height(16.dp))

            // 🧠 Branding
            Text(
                text = "MANOSPY",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
            )
            Text(
                text = "TECNOLOGÍA PREMIUM",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B)
            )

            Spacer(Modifier.height(32.dp))

            // 🔄 Animación de carga
            CircularProgressIndicator(color = Color(0xFF0D9488))

            Spacer(Modifier.height(24.dp))

            // 🔐 Mensaje de conexión
            Text(
                text = "Asegurando datos...",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )
            Text(
                text = "Iniciando conexión cifrada",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(Modifier.height(48.dp))

            // 📦 Versión y autor
            Text(
                text = "v1.0.24 - Desarrollado por MANOSPY Tech",
                fontSize = 10.sp,
                color = Color(0xFFCBD5E1)
            )
        }
    }
}
