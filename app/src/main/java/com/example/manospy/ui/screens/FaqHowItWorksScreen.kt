package com.example.manospy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.components.AppScaffold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FaqHowItWorksScreen() {
    val textPrimary = Color(0xFF111827)
    val textSecondary = Color(0xFF6B7280)
    val backgroundColor = Color(0xFFF9FAFB)

    AppScaffold(
        title = "Pregunta",
        onBackClick = { },
        hasBottomNavigation = false
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "¿Cómo funciona Manospy?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    lineHeight = 32.sp
                )
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Manospy conecta clientes con profesionales verificados. El cliente busca un servicio, revisa perfiles y calificaciones, y contacta al profesional. El profesional recibe solicitudes y ofrece sus servicios desde la app.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    color = textSecondary,
                    lineHeight = 28.sp,
                    fontSize = 18.sp
                )
            )
        }
    }
}
