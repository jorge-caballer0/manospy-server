@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.manospy.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import android.content.Intent
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.manospy.ui.viewmodel.ProfessionalRegisterViewModel
import com.example.manospy.util.NetworkResult

@Composable
fun ProfessionalRegisterStep3Screen(
    viewModel: ProfessionalRegisterViewModel,
    onBack: () -> Unit,
    onFinish: (com.example.manospy.data.model.User?) -> Unit
) {
    // Colores del tema Stitch
    val primaryBlue = Color(0xFF0056D2)
    val backgroundLight = Color(0xFFF8FAFC)
    val textPrimary = Color(0xFF1E293B)
    val textSecondary = Color(0xFF64748B)
    val amberAlert = Color(0xFFFCD34D)
    val greenSuccess = Color(0xFF10B981)
    val borderGray = Color(0xFFE2E8F0)
    
    // Estado local para documentos
    var idFrontUri by remember { mutableStateOf<Uri?>(null) }
    var idBackUri by remember { mutableStateOf<Uri?>(null) }
    var certificatesList by remember { mutableStateOf<List<Pair<String, Uri>>>(emptyList()) }
    
    // Estado del registro
    val registerState by viewModel.registerState.collectAsState()
    
    // Navegar cuando el registro sea exitoso
    LaunchedEffect(registerState) {
        val currentState = registerState
        if (currentState is NetworkResult.Success) {
            val user = currentState.data.user
            if (user != null) {
                onFinish(user)
            }
        }
    }
    
    // Launchers para seleccionar fotos/documentos
    val context = LocalContext.current
    val idFrontLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            idFrontUri = it
            viewModel.setIdFront(it.toString())
        }
    }
    
    val idBackLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            idBackUri = it
            viewModel.setIdBack(it.toString())
        }
    }

    // Launchers para abrir la galería directamente (ACTION_PICK)
    val idFrontGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                idFrontUri = it
                viewModel.setIdFront(it.toString())
            }
        }
    }

    val idBackGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                idBackUri = it
                viewModel.setIdBack(it.toString())
            }
        }
    }
    
    val certificateLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = "Certificado_${System.currentTimeMillis()}.pdf"
            certificatesList = certificatesList + (fileName to it)
            viewModel.setCertificates(certificatesList.map { pair -> pair.second.toString() })
        }
    }

    // Launcher para abrir documentos locales (PDFs o imágenes) usando ACTION_OPEN_DOCUMENT
    val certificateDocumentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri = result.data?.data
            uri?.let {
                val fileName = "Certificado_${System.currentTimeMillis()}.pdf"
                certificatesList = certificatesList + (fileName to it)
                viewModel.setCertificates(certificatesList.map { pair -> pair.second.toString() })
                // Intent persistable permission
                try {
                    context.contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    // ignore if not possible
                }
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundLight)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Atrás",
                            tint = textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Text(
                        "Documentación (3/3)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.size(40.dp))
                }
            }
            
            // Icono con badge
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box {
                        // Icono documento rotado
                        Surface(
                            modifier = Modifier
                                .size(64.dp)
                                .rotate(3f)
                                .clip(RoundedCornerShape(16.dp)),
                            color = primaryBlue,
                            shadowElevation = 12.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Description,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier
                                        .size(32.dp)
                                        .rotate(-3f)
                                )
                            }
                        }
                        
                        // Badge nube
                        Surface(
                            modifier = Modifier
                                .size(32.dp)
                                .align(Alignment.BottomEnd)
                                .offset(x = (4.dp), y = (4.dp))
                                .clip(RoundedCornerShape(6.dp)),
                            color = amberAlert,
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Outlined.CloudUpload,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            
            // Título y descripción
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Verifica tu identidad",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = primaryBlue,
                        textAlign = TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        "Sube fotos claras de tus documentos para\nhabilitar tu perfil.",
                        fontSize = 14.sp,
                        color = textSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 18.sp
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            
            // Sección Cédula de Identidad
            item {
                Text(
                    "Cédula de Identidad (Frente y Dorso)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )
            }
            
            // FRENTE de Cédula
            item {
                if (idFrontUri != null) {
                    DocumentPreviewCard(
                        uri = idFrontUri!!,
                        label = "Cédula (Frente)",
                        onRemove = {
                            idFrontUri = null
                            viewModel.setIdFront("")
                        },
                        backgroundColor = Color.White,
                        borderColor = borderGray,
                        greenSuccess = greenSuccess,
                        textPrimary = textPrimary
                    )
                } else {
                    DocumentUploadCard(
                        onUploadClick = { 
                            // Abrir galería para seleccionar imagen desde dispositivo
                            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            idFrontGalleryLauncher.launch(pickIntent)
                        },
                        backgroundColor = backgroundLight,
                        borderColor = borderGray,
                        primaryColor = primaryBlue,
                        amberAlert = amberAlert,
                        label = "Cédula - Frente"
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
            
            // DORSO de Cédula
            item {
                if (idBackUri != null) {
                    DocumentPreviewCard(
                        uri = idBackUri!!,
                        label = "Cédula (Dorso)",
                        onRemove = {
                            idBackUri = null
                            viewModel.setIdBack("")
                        },
                        backgroundColor = Color.White,
                        borderColor = borderGray,
                        greenSuccess = greenSuccess,
                        textPrimary = textPrimary
                    )
                } else {
                    DocumentUploadCard(
                        onUploadClick = { 
                            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            idBackGalleryLauncher.launch(pickIntent)
                        },
                        backgroundColor = backgroundLight,
                        borderColor = borderGray,
                        primaryColor = primaryBlue,
                        amberAlert = amberAlert,
                        label = "Cédula - Dorso"
                    )
                }
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
            
            // Sección Certificados
            item {
                Text(
                    "Certificados de Formación",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = textPrimary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )
            }
            
            // Lista de certificados cargados
            items(certificatesList.size) { index ->
                CertificateItem(
                    fileName = certificatesList[index].first,
                    onDelete = {
                        certificatesList = certificatesList.filterIndexed { i, _ -> i != index }
                        viewModel.setCertificates(certificatesList.map { it.second.toString() })
                    },
                    textPrimary = textPrimary,
                    textSecondary = textSecondary,
                    greenSuccess = greenSuccess
                )
            }
            
            // Área para agregar certificado
            item {
                AddCertificateCard(
                    onAddClick = {
                        // Abrir selector de documentos (PDF o imagenes)
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "*/*"
                            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "image/*"))
                        }
                        certificateDocumentLauncher.launch(intent)
                    },
                    borderColor = borderGray,
                    textSecondary = textSecondary
                )
            }
            
            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
        
        // Bottom Bar con botones
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .padding(24.dp)
        ) {
            val isFormValid = idFrontUri != null && idBackUri != null && certificatesList.isNotEmpty()
            val isLoading = registerState is NetworkResult.Loading
            
            Button(
                onClick = { viewModel.finishRegistration() },
                enabled = isFormValid && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryBlue,
                    disabledContainerColor = Color(0xFFCBD5E1)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 8.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        if (isLoading) "Validando..." else "Finalizar Registro",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    if (!isLoading) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            // Opción para omitir la carga de documentos (útil en pruebas)
            TextButton(
                onClick = { viewModel.finishRegistration(skipDocs = true) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Finalizar sin documentos",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryBlue
                )
            }

            Button(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = textSecondary
                ),
                border = BorderStroke(1.5.dp, Color(0xFFCBD5E1))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Atrás",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        // Footer
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color(0xFFCBD5E1),
                                Color.Transparent
                            )
                        )
                    )
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                "Paso 3 de 3: Verificación Final",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                letterSpacing = 0.1.sp
            )
        }
    }
}

@Composable
private fun DocumentPreviewCard(
    uri: Uri,
    label: String,
    onRemove: () -> Unit,
    backgroundColor: Color,
    borderColor: Color,
    greenSuccess: Color,
    textPrimary: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, greenSuccess, RoundedCornerShape(16.dp))
            .background(backgroundColor),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0FDF4))
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = label,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = greenSuccess,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = textPrimary
                        )
                        Text(
                            "DOCUMENTO CARGADO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            letterSpacing = 0.05.sp
                        )
                    }
                }
                
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun DocumentUploadCard(
    onUploadClick: () -> Unit,
    backgroundColor: Color,
    borderColor: Color,
    primaryColor: Color,
    amberAlert: Color,
    label: String = "Documento"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(2.dp, borderColor, RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onUploadClick() }
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.AddAPhoto,
                contentDescription = null,
                tint = primaryColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Toca para subir",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )
        }
        
        // Badge de estado
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = Color(0xFFFEF3C7),
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(amberAlert, shape = RoundedCornerShape(50))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "PENDIENTE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF92400E),
                    letterSpacing = 0.05.sp
                )
            }
        }
    }
}

@Composable
private fun CertificateItem(
    fileName: String,
    onDelete: () -> Unit,
    textPrimary: Color,
    textSecondary: Color,
    greenSuccess: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp)),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = greenSuccess,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        fileName,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary
                    )
                    Text(
                        "DOCUMENTO CARGADO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = textSecondary,
                        letterSpacing = 0.05.sp
                    )
                }
            }
            
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun AddCertificateCard(
    onAddClick: () -> Unit,
    borderColor: Color,
    textSecondary: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onAddClick() }
            .padding(24.dp),
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "+ Agregar otro certificado",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = textSecondary
            )
        }
    }
}
