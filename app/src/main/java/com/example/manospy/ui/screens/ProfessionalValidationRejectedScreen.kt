package com.example.manospy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.HighlightOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun ProfessionalValidationRejectedScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // ❌ Íconos de rechazo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.DocumentScanner,
                    contentDescription = "Document Icon",
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Filled.HighlightOff,
                    contentDescription = "Rejected Icon",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // 🧠 Título
            Text(
                text = "Validación no aprobada",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(Modifier.height(12.dp))

            // 📄 Mensaje
            Text(
                text = "Lamentablemente, tu perfil no ha sido validado en este momento.",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                lineHeight = 20.sp
            )

            Spacer(Modifier.height(24.dp))

            // 📌 Motivo del rechazo
            Text(
                text = "Motivo del rechazo",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFEF4444)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Documentación incompleta o ilegible.\nPor favor, asegúrate de que todas las fotos sean claras y los datos sean totalmente visibles. Evita reflejos de luz en el documento.",
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                lineHeight = 18.sp
            )

            Spacer(Modifier.height(32.dp))

            // 🔁 Botón para actualizar documentos
            Button(
                onClick = { navController.navigate("step3") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D9488)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Actualizar documentos", fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))

            // 📞 Contactar soporte
            Text(
                text = "Contactar soporte",
                fontSize = 12.sp,
                color = Color(0xFF0D9488),
                modifier = Modifier.clickable { navController.navigate("support") }
            )
        }
    }
}
