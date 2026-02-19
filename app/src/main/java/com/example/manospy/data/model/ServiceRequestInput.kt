package com.example.manospy.data.model

// Modelo para enviar al backend al crear una solicitud de servicio
data class ServiceRequestInput(
    val clientId: String,
    val category: String,
    val description: String,
    val location: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val preferredDate: String? = null
)
