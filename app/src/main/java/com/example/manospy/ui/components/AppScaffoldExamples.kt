package com.example.manospy.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.theme.CorporateBlue
import com.example.manospy.ui.theme.DarkGray

/**
 * EJEMPLOS DE PANTALLAS USANDO AppScaffold
 * 
 * Este archivo contiene ejemplos de cómo usar AppScaffold en diferentes tipos de pantallas
 */

// ============================================================================
// EJEMPLO 1: Pantalla Simple con Lista
// ============================================================================
@Composable
fun ExampleListScreen() {
    val items = listOf(
        "Elemento 1",
        "Elemento 2",
        "Elemento 3",
        "Elemento 4",
        "Elemento 5"
    )

    AppScaffold(
        title = "Lista de Elementos",
        contentModifier = Modifier.padding(horizontal = 0.dp)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(items) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Text(
                        text = item,
                        modifier = Modifier.padding(16.dp),
                        fontSize = 16.sp,
                        color = DarkGray
                    )
                }
            }
        }
    }
}

// ============================================================================
// EJEMPLO 2: Pantalla con Formulario
// ============================================================================
@Composable
fun ExampleFormScreen() {
    AppScaffold(
        title = "Formulario",
        contentModifier = Modifier.padding(horizontal = 0.dp)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección 1
            Text(
                text = "Información Personal",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = DarkGray
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Nombre:", fontSize = 12.sp, color = DarkGray)
                    Text("Correo:", fontSize = 12.sp, color = DarkGray)
                    Text("Teléfono:", fontSize = 12.sp, color = DarkGray)
                }
            }

            // Botones
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = CorporateBlue)
            ) {
                Text("Guardar", color = Color.White, fontSize = 14.sp)
            }

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0))
            ) {
                Text("Cancelar", color = DarkGray, fontSize = 14.sp)
            }
        }
    }
}

// ============================================================================
// EJEMPLO 3: Pantalla con Contenido Centrado
// ============================================================================
@Composable
fun ExampleCenteredScreen() {
    AppScaffold(
        title = "Bienvenido"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "¡Hola!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Este es un ejemplo de pantalla centrada",
                fontSize = 14.sp,
                color = Color(0xFF999999)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = CorporateBlue)
            ) {
                Text("Continuar", color = Color.White)
            }
        }
    }
}

// ============================================================================
// EJEMPLO 4: Pantalla con Navbar Inferior
// ============================================================================
@Composable
fun ExampleScreenWithNavBar() {
    AppScaffold(
        title = "Con Navegación",
        hasBottomNavigation = true
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(10) { index ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
                ) {
                    Text(
                        text = "Item $index",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp,
                        color = DarkGray
                    )
                }
            }
        }
    }
}

// ============================================================================
// EJEMPLO 5: Pantalla Minimalista con SimpleAppScaffold
// ============================================================================
@Composable
fun ExampleSimpleScreen() {
    SimpleAppScaffold(
        title = "Pantalla Simple"
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Contenido simple sin container redondeado",
                fontSize = 16.sp,
                color = DarkGray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
