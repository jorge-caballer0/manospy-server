package com.example.manospy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.manospy.util.PlacesHelper
import kotlinx.coroutines.launch
import com.example.manospy.ui.theme.AppColors

/**
 * Campo de búsqueda de ubicaciones con autocompletado
 * 
 * @param value Valor actual del campo de búsqueda
 * @param onValueChange Callback cuando cambia el valor
 * @param onSuggestionSelected Callback cuando se selecciona una sugerencia
 * @param modifier Modificador para el composable
 * @param placeholder Texto placeholder
 * @param debounceMs Tiempo de debounce en milisegundos para reducir llamadas API
 */
@Composable
fun LocationSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onSuggestionSelected: (PlacesHelper.LocationSuggestion) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar ubicación...",
    debounceMs: Long = 500,
    showSuggestions: Boolean = true
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var suggestions by remember { mutableStateOf<List<PlacesHelper.LocationSuggestion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    
    // Debounce para evitar llamadas excesivas a Geocoder
    LaunchedEffect(value, showSuggestions) {
        if (value.isBlank() || !showSuggestions) {
            suggestions = emptyList()
            isLoading = false
            return@LaunchedEffect
        }
        
        // Debounce: esperar 500ms antes de buscar
        isLoading = true
        kotlinx.coroutines.delay(debounceMs)
        
        try {
            suggestions = PlacesHelper.searchLocations(
                context = context,
                query = value,
                maxResults = 5,
                filterByUserCountry = false
            )
        } catch (e: Exception) {
            suggestions = emptyList()
        } finally {
            isLoading = false
        }
    }
    
    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Campo de búsqueda
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    placeholder = {
                        Text(
                            placeholder,
                            fontSize = 14.sp,
                            color = AppColors.TextTertiary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.LocationOn,
                            contentDescription = "Ubicación",
                            modifier = Modifier.size(20.dp),
                            tint = AppColors.PrimaryBlue
                        )
                    },
                    trailingIcon = {
                        if (value.isNotEmpty()) {
                            IconButton(
                                onClick = {
                                    onValueChange("")
                                    suggestions = emptyList()
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Limpiar",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = AppColors.TextPrimary),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            // Acción de búsqueda si es necesaria
                        }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = AppColors.TextPrimary,
                        unfocusedTextColor = AppColors.TextPrimary,
                        focusedIndicatorColor = AppColors.PrimaryBlue,
                        unfocusedIndicatorColor = AppColors.BorderGray
                    )
                )
            }
            
            // Sugerencias dropdown
            if (showSuggestions && (suggestions.isNotEmpty() || isLoading) && value.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White),
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color(0xFF2563EB),
                                strokeWidth = 2.dp
                            )
                        }
                    } else if (suggestions.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 250.dp)
                        ) {
                            items(suggestions) { suggestion ->
                                LocationSuggestionItem(
                                    suggestion = suggestion,
                                    onSelect = {
                                        // Let the caller decide how to update the input; only notify selection here
                                        onSuggestionSelected(suggestion)
                                        suggestions = emptyList()
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Item individual de sugerencia de ubicación
 */
@Composable
private fun LocationSuggestionItem(
    suggestion: PlacesHelper.LocationSuggestion,
    onSelect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Ubicación",
                modifier = Modifier.size(20.dp),
                tint = Color(0xFF64748B)
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = suggestion.displayName,
                    fontSize = 14.sp,
                    color = Color(0xFF1F2937),
                    maxLines = 1
                )
                if (suggestion.fullAddress.isNotEmpty()) {
                    Text(
                        text = suggestion.fullAddress,
                        fontSize = 12.sp,
                        color = Color(0xFF64748B),
                        maxLines = 1
                    )
                }
            }
        }
        
        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFFE5E7EB),
            thickness = 0.5.dp
        )
    }
}

/**
 * Componente de búsqueda de ubicación simplificado sin sugerencias dropdown
 * Útil para campos inline sin necesidad de dropdown
 */
@Composable
fun SimpleLocationSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Buscar ubicación...",
    enabled: Boolean = true,
    leadingIconType: String = "location"  // "location" o "search"
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        color = Color.White,
        shape = RoundedCornerShape(12.dp)
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            placeholder = {
                Text(
                    placeholder,
                    fontSize = 14.sp,
                    color = AppColors.TextTertiary
                )
            },
            leadingIcon = {
                val icon = if (leadingIconType == "search") Icons.Filled.Search else Icons.Filled.LocationOn
                val description = if (leadingIconType == "search") "Buscar" else "Ubicación"
                Icon(
                    imageVector = icon,
                    contentDescription = description,
                    modifier = Modifier.size(20.dp),
                    tint = AppColors.PrimaryBlue
                )
            },
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(
                        onClick = { onValueChange("") },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Limpiar",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            },
            singleLine = true,
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = AppColors.TextPrimary),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedTextColor = AppColors.TextPrimary,
                unfocusedTextColor = AppColors.TextPrimary,
                focusedIndicatorColor = AppColors.PrimaryBlue,
                unfocusedIndicatorColor = AppColors.BorderGray,
                disabledContainerColor = Color(0xFFF6F7F8),
                disabledTextColor = AppColors.TextTertiary
            )
        )
    }
}
