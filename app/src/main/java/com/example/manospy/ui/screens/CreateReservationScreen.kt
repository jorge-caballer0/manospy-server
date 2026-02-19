package com.example.manospy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import com.example.manospy.ui.components.LocationSearchField
import com.example.manospy.ui.components.AppScaffold
import com.example.manospy.ui.theme.AppColors
import com.example.manospy.ui.theme.AppDimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateReservationScreen(onNavigateBack: () -> Unit = {}) {
    val brandBlue = AppColors.BrandBlue
    val accentCyan = AppColors.AccentCyan
    val textPrimary = AppColors.TextPrimary
    val textSecondary = AppColors.TextSecondary
    val bgLight = AppColors.BgLight
    val cardBg = AppColors.BgWhite
    val successGreen = AppColors.AccentGreen

    var selectedService by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var selectedTime by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var currentStep by remember { mutableStateOf(0) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showLocationDialog by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        photoUri = it
    }

    val steps = listOf("Servicio", "Fecha & Hora", "Ubicación", "Detalles & Foto", "Confirmación")

    AppScaffold(
        title = "Nueva Solicitud",
        onBackClick = onNavigateBack
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Progress Indicator
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(steps.size) { index ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (index <= currentStep) brandBlue else Color(0xFFE2E8F0)
                                    )
                            )
                        }
                    }
                    Text(
                        "Paso ${currentStep + 1} de ${steps.size}: ${steps[currentStep]}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = textSecondary,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // Content por paso
            item {
                when (currentStep) {
                    0 -> StepSelectService(selectedService) { selectedService = it }
                    1 -> StepDateAndTime(selectedDate, selectedTime, 
                        onDateClick = { showDatePicker = true },
                        onTimeClick = { showTimePicker = true },
                        onDateChange = { selectedDate = it },
                        onTimeChange = { selectedTime = it }
                    )
                    2 -> StepLocation(location) { location = it }
                    3 -> StepDetailsAndPhoto(description, photoUri,
                        onDescriptionChange = { description = it },
                        onPhotoClick = { photoPicker.launch("image/*") }
                    )
                    4 -> StepConfirmation(selectedService, selectedDate, selectedTime, location, description, photoUri)
                }
            }

            // Botones de navegación
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = textSecondary)
                        ) {
                            Text("Atrás", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Button(
                        onClick = { 
                            if (currentStep < steps.size - 1) currentStep++ 
                        },
                        enabled = isStepValid(currentStep, selectedService, selectedDate, selectedTime, location),
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = brandBlue)
                    ) {
                        Text(
                            if (currentStep == steps.size - 1) "Enviar solicitud" else "Siguiente",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismiss = { showDatePicker = false },
            onDateSelect = { date ->
                selectedDate = date
                showDatePicker = false
            }
        )
    }

    // Time Picker Dialog
    if (showTimePicker) {
        TimePickerDialog(
            onDismiss = { showTimePicker = false },
            onTimeSelect = { time ->
                selectedTime = time
                showTimePicker = false
            }
        )
    }
}

@Composable
private fun StepSelectService(selected: String, onSelect: (String) -> Unit) {
    val services = listOf("Limpieza", "Plomería", "Electricidad", "Mantenimiento", "Pintura", "Reparación", "Jardinería", "Carpintería")
    val brandBlue = Color(0xFF0EA5E9)
    val accentCyan = Color(0xFF06B6D4)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "¿Qué servicio necesitas?",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = Color(0xFF0F172A)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(services.size) { index ->
                val service = services[index]
                val isSelected = service == selected
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onSelect(service) }
                        .shadow(if (isSelected) 8.dp else 2.dp),
                    color = if (isSelected) brandBlue else Color.White,
                    tonalElevation = 2.dp
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Build,
                            contentDescription = service,
                            tint = if (isSelected) Color.White else brandBlue,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            service,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            ),
                            color = if (isSelected) Color.White else Color(0xFF0F172A),
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

// ======================== HELPER FUNCTIONS ========================

private fun isStepValid(step: Int, service: String, date: LocalDate?, time: String, location: String): Boolean {
    return when (step) {
        0 -> service.isNotBlank()
        1 -> date != null && time.isNotBlank()
        2 -> location.isNotBlank()
        3 -> true // Details y foto son opcionales
        else -> true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepDateAndTime(
    selectedDate: LocalDate?,
    selectedTime: String,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onTimeChange: (String) -> Unit
) {
    val brandBlue = Color(0xFF0EA5E9)
    val cardBg = Color.White
    val textPrimary = Color(0xFF0F172A)
    val fieldBg = Color(0xFFF8FAFC)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "¿Cuándo necesitas el servicio?",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
        )

        // Fecha
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onDateClick() }
                .shadow(2.dp),
            color = cardBg
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("📅 Fecha", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                    Text(
                        selectedDate?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) ?: "Seleccionar fecha",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = textPrimary
                    )
                }
                Icon(Icons.Outlined.DateRange, contentDescription = null, tint = brandBlue, modifier = Modifier.size(24.dp))
            }
        }

        // Hora
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onTimeClick() }
                .shadow(2.dp),
            color = cardBg
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("🕐 Hora", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                    Text(
                        selectedTime.ifEmpty { "Seleccionar hora" },
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = textPrimary
                    )
                }
                Icon(Icons.Filled.AccessTime, contentDescription = null, tint = brandBlue, modifier = Modifier.size(24.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepLocation(location: String, onLocationChange: (String) -> Unit) {
    val brandBlue = Color(0xFF0EA5E9)
    val textPrimary = Color(0xFF0F172A)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "¿Dónde lo necesitas?",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
        )

        LocationSearchField(
            value = location,
            onValueChange = onLocationChange,
            onSuggestionSelected = { suggestion ->
                onLocationChange(suggestion.fullAddress)
            },
            placeholder = "Busca tu dirección...",
            showSuggestions = true
        )

        Text(
            "💡 Propina: Sé específico. Busca tu calle o barrio en las sugerencias.",
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8), fontSize = 11.sp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StepDetailsAndPhoto(
    description: String,
    photoUri: Uri?,
    onDescriptionChange: (String) -> Unit,
    onPhotoClick: () -> Unit
) {
    val brandBlue = Color(0xFF0EA5E9)
    val cardBg = Color.White
    val textPrimary = Color(0xFF0F172A)
    val fieldBg = Color(0xFFF8FAFC)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Detalles y Foto",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
        )

        // Descripción
        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = { Text("Descripción adicional") },
            placeholder = { Text("Cuéntanos más detalles...") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            shape = RoundedCornerShape(12.dp),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = brandBlue,
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = fieldBg
            )
        )

        // Foto
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .clickable { onPhotoClick() }
                .shadow(2.dp),
            color = cardBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Foto seleccionada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Text("📸 Foto seleccionada", style = MaterialTheme.typography.labelSmall, color = Color(0xFF10B981))
                } else {
                    Icon(
                        Icons.Default.PhotoCamera,
                        contentDescription = null,
                        tint = Color(0xFF0EA5E9),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        "Cargar foto (opcional)",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = textPrimary
                    )
                    Text(
                        "Tap para seleccionar una foto",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepConfirmation(
    service: String,
    date: LocalDate?,
    time: String,
    location: String,
    description: String,
    photoUri: Uri?
) {
    val brandBlue = Color(0xFF0EA5E9)
    val cardBg = Color.White
    val textPrimary = Color(0xFF0F172A)
    val successGreen = Color(0xFF10B981)

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Confirma tu solicitud",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = textPrimary
            )
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .shadow(2.dp),
            color = cardBg
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ConfirmationRow("Servicio", service)
                Divider(color = Color(0xFFE2E8F0))
                ConfirmationRow("Fecha", date?.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")) ?: "-")
                Divider(color = Color(0xFFE2E8F0))
                ConfirmationRow("Hora", time)
                Divider(color = Color(0xFFE2E8F0))
                ConfirmationRow("Ubicación", location)
                if (description.isNotBlank()) {
                    Divider(color = Color(0xFFE2E8F0))
                    ConfirmationRow("Detalles", description)
                }
                if (photoUri != null) {
                    Divider(color = Color(0xFFE2E8F0))
                    Text("✅ Foto incluida", style = MaterialTheme.typography.labelSmall, color = successGreen)
                }
            }
        }

        Text(
            "Al enviar, aceptas los Términos de Servicio y reconoces que un profesional se contactará contigo.",
            style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF94A3B8)),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun ConfirmationRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8), modifier = Modifier.weight(0.3f))
        Text(value, style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold), modifier = Modifier.weight(0.7f))
    }
}

@Composable
private fun DatePickerDialog(onDismiss: () -> Unit, onDateSelect: (LocalDate) -> Unit) {
    val today = LocalDate.now()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar fecha") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Navegación mes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
                    }
                    Text(currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")), fontWeight = FontWeight.Bold)
                    IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Siguiente mes")
                    }
                }

                // Días de la semana
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("L", "M", "M", "J", "V", "S", "D").forEach {
                        Text(it, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                }

                // Calendario
                val daysInMonth = currentMonth.lengthOfMonth()
                val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value % 7

                LazyVerticalGrid(columns = GridCells.Fixed(7), modifier = Modifier.fillMaxWidth()) {
                    items(firstDayOfWeek) {
                        // Días en blanco al inicio
                    }
                    items(daysInMonth) { dayIndex ->
                        val date = currentMonth.atDay(dayIndex + 1)
                        val isToday = date == today
                        val isSelectable = date >= today

                        Button(
                            onClick = { if (isSelectable) onDateSelect(date) },
                            enabled = isSelectable,
                            modifier = Modifier
                                .padding(2.dp)
                                .fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isToday) Color(0xFF0EA5E9) else Color(0xFFF0F0F0),
                                disabledContainerColor = Color(0xFFEEEEEE)
                            )
                        ) {
                            Text((dayIndex + 1).toString(), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Listo") }
        }
    )
}

@Composable
private fun TimePickerDialog(onDismiss: () -> Unit, onTimeSelect: (String) -> Unit) {
    val hours = (8..18).map { "${it.toString().padStart(2, '0')}:00" }
    var selectedHour by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar hora") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hours.size) { index ->
                    val hour = hours[index]
                    Button(
                        onClick = { selectedHour = hour },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedHour == hour) Color(0xFF0EA5E9) else Color(0xFFF0F0F0)
                        )
                    ) {
                        Text(hour, color = if (selectedHour == hour) Color.White else Color.Black)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedHour.isNotEmpty()) {
                        onTimeSelect(selectedHour)
                    }
                },
                enabled = selectedHour.isNotEmpty()
            ) {
                Text("Aceptar")
            }
        }
    )
}
