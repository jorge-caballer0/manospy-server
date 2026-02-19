@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.manospy.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBackIos
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Carpenter
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.ui.viewmodel.ProfessionalRegisterViewModel

data class ServiceCategory(
    val id: String,
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun ProfessionalRegisterStep2Screen(
    viewModel: ProfessionalRegisterViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var selectedServices by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedCities by remember { mutableStateOf<List<String>>(emptyList()) }
    var showMoreCategories by remember { mutableStateOf(false) }
    var currentTab by remember { mutableStateOf(0) } // 0 = Servicios, 1 = Ciudades

    val primaryBlue = Color(0xFF0056D2)
    val textPrimary = Color(0xFF0F172A)
    val textSecondary = Color(0xFF64748B)
    val fieldOutline = Color(0xFFE2E8F0)
    val lightBg = Color(0xFFF8FAFC)

    // Base categories (8 initially shown)
    val baseCategories = listOf(
        ServiceCategory("plumbing", "Plomería", Icons.Default.Build),
        ServiceCategory("electricity", "Electricidad", Icons.Default.Bolt),
        ServiceCategory("carpentry", "Carpintería", Icons.Default.Carpenter),
        ServiceCategory("hvac", "Aire Acond.", Icons.Default.AcUnit),
        ServiceCategory("painting", "Pintura", Icons.Default.Palette),
        ServiceCategory("masonry", "Albañilería", Icons.Default.Handyman),
        ServiceCategory("cleaning", "Limpieza", Icons.Default.CleaningServices),
        ServiceCategory("gardening", "Jardinería", Icons.Default.LocalFlorist)
    )

    // Extended categories for "Ver más"
    val extraCategories = listOf(
        ServiceCategory("welding", "Soldadura", Icons.Default.Build),
        ServiceCategory("mechanics", "Mecánica", Icons.Default.Build),
        ServiceCategory("glazing", "Cristalería", Icons.Default.Build),
        ServiceCategory("hvac_service", "Refrigeración", Icons.Default.AcUnit)
    )

    // Ciudades de cobertura disponibles
    val availableCities = listOf(
        "Asunción",
        "San Juan Bautista",
        "Caaguazú",
        "Villarrica",
        "Concepción",
        "Caazapá",
        "Coronel Oviedo",
        "Encarnación",
        "Salto del Guairá",
        "Ciudad del Este",
        "Colón",
        "Pedro Juan Caballero",
        "Iguazú",
        "Pab. Riquelme",
        "Filadelfia"
    )

    val displayedCategories = if (showMoreCategories) baseCategories + extraCategories else baseCategories
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = lightBg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(scrollState)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBackIos,
                        contentDescription = "Atrás",
                        tint = textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Especialidades",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textPrimary
                        ),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "PASO 2 DE 3",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = textSecondary,
                            letterSpacing = 1.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Box(modifier = Modifier.size(40.dp))
            }

            // Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Tabs selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .background(Color(0xFFF3F4F6), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tab 1: Servicios
                    Button(
                        onClick = { currentTab = 0 },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentTab == 0) primaryBlue else Color.Transparent,
                            contentColor = if (currentTab == 0) Color.White else textSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text("Servicios", fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.width(4.dp))
                    
                    // Tab 2: Ciudades
                    Button(
                        onClick = { currentTab = 1 },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (currentTab == 1) primaryBlue else Color.Transparent,
                            contentColor = if (currentTab == 1) Color.White else textSecondary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                    ) {
                        Text("Cobertura", fontWeight = FontWeight.Bold)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // TAB 0: Servicios
                if (currentTab == 0) {
                    // Question section
                    Text(
                        text = "¿Cuál es tu oficio?",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Selecciona una o más categorías para las que ofreces servicios.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textSecondary,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Spacer(modifier = Modifier.height(24.dp))

                    // Categories grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 600.dp)
                    ) {
                        items(displayedCategories) { category ->
                            ServiceCategoryCard(
                                category = category,
                                isSelected = selectedServices.contains(category.id),
                                onSelect = {
                                    selectedServices = if (selectedServices.contains(category.id)) {
                                        selectedServices.minus(category.id)
                                    } else {
                                        selectedServices.plus(category.id)
                                    }
                                },
                                primaryBlue = primaryBlue,
                                textPrimary = textPrimary,
                                fieldOutline = fieldOutline
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // "Ver más categorías" button
                    if (!showMoreCategories) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) { showMoreCategories = true },
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = primaryBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ver más categorías",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = primaryBlue
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
                
                // TAB 1: Ciudades de cobertura
                if (currentTab == 1) {
                    Text(
                        text = "¿Dónde ofreces servicios?",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 22.sp,
                            color = textPrimary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Selecciona una o más ciudades donde cubrirás servicios.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = textSecondary,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Cities grid (2 columns)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 600.dp)
                    ) {
                        items(availableCities) { city ->
                            CityCard(
                                cityName = city,
                                isSelected = selectedCities.contains(city),
                                onSelect = {
                                    selectedCities = if (selectedCities.contains(city)) {
                                        selectedCities.minus(city)
                                    } else {
                                        selectedCities.plus(city)
                                    }
                                },
                                primaryBlue = primaryBlue,
                                textPrimary = textPrimary,
                                fieldOutline = fieldOutline
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }

        // Bottom buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(2.dp, fieldOutline),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = textPrimary)
            ) {
                Text(
                    text = "Atrás",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()
            val btnScale by animateFloatAsState(
                if (isPressed && selectedServices.isNotEmpty()) 0.97f else 1f,
                label = "scale"
            )

            Button(
                onClick = {
                    if (selectedServices.isNotEmpty() && selectedCities.isNotEmpty()) {
                        viewModel.nextStepStep2(
                            services = selectedServices,
                            cities = selectedCities
                        )
                        onNext()
                    }
                },
                enabled = selectedServices.isNotEmpty() && selectedCities.isNotEmpty(),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .scale(btnScale)
                    .shadow(
                        elevation = 12.dp,
                        shape = RoundedCornerShape(18.dp),
                        spotColor = primaryBlue
                    ),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryBlue,
                    disabledContainerColor = Color(0xFFD1D5DB)
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                interactionSource = interactionSource
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Siguiente",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceCategoryCard(
    category: ServiceCategory,
    isSelected: Boolean,
    onSelect: () -> Unit,
    primaryBlue: Color,
    textPrimary: Color,
    fieldOutline: Color,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        if (isSelected) primaryBlue else fieldOutline,
        label = "borderColor"
    )
    val backgroundColor by animateColorAsState(
        if (isSelected) primaryBlue.copy(alpha = 0.05f) else Color.White,
        label = "backgroundColor"
    )
    val iconBgColor by animateColorAsState(
        if (isSelected) primaryBlue else Color(0xFFF3F4F6),
        label = "iconBgColor"
    )
    val iconTintColor by animateColorAsState(
        if (isSelected) primaryBlue else Color(0xFF9CA3AF),
        label = "iconTintColor"
    )

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onSelect() }
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            // Icon background
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = iconBgColor,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = if (isSelected) Color.White else iconTintColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Category name
            Text(
                text = category.name,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    fontSize = 13.sp
                ),
                textAlign = TextAlign.Center
            )
        }

        // Checkmark badge - top right
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Selected",
                tint = primaryBlue,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(24.dp)
            )
        }
    }
}

@Composable
fun CityCard(
    cityName: String,
    isSelected: Boolean,
    onSelect: () -> Unit,
    primaryBlue: Color,
    textPrimary: Color,
    fieldOutline: Color,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        if (isSelected) primaryBlue else fieldOutline,
        label = "cityBorderColor"
    )
    val backgroundColor by animateColorAsState(
        if (isSelected) primaryBlue.copy(alpha = 0.05f) else Color.White,
        label = "cityBackgroundColor"
    )

    Box(
        modifier = modifier
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onSelect() }
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = cityName,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    fontSize = 14.sp
                )
            )
            
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = primaryBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
