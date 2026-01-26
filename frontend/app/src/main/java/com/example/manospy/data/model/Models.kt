package com.example.manospy.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val token: String? = null,
    val status: String? = null,
    val phoneNumber: String? = null,
    val idNumber: String? = null,
    val category: String? = null,
    val bio: String? = null,
    val experience: String? = null,
    val location: String? = null,
    val documentUrl: String? = null,
    val rejectionReason: String? = null,
    // ✅ CORRECCIÓN 4: Campos profesional que faltaban
    val services: List<String>? = null,        // Array de servicios (ej: ["Masajes", "SPA"])
    val cities: List<String>? = null,          // Array de ciudades
    val certificates: List<String>? = null,    // URLs de certificados
    val idFrontUrl: String? = null,            // Foto cédula frente
    val idBackUrl: String? = null              // Foto cédula dorso
)

enum class ServiceRequestStatus {
    PENDING,
    ACCEPTED,
    CANCELLED
}

data class ServiceRequest(
    val id: String,
    val clientId: String,
    val description: String,
    val category: String,
    val status: ServiceRequestStatus,
    val createdAt: String,
    val location: String? = null,
    val preferredDate: String? = null,
    val clientName: String? = null
)

enum class ReservationStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED
}

data class Reservation(
    val id: String,
    val serviceRequestId: String,
    val clientId: String,
    val professionalId: String,
    val status: ReservationStatus,
    val professionalName: String? = null,
    val clientName: String? = null,

    // ✅ Campos adicionales para ReservationDetailScreen
    val serviceName: String? = null,        // Nombre del servicio (ej: "Masaje Relajante Premium")
    val date: String? = null,               // Fecha de la reserva
    val timeStart: String? = null,          // Hora inicio
    val timeEnd: String? = null,            // Hora fin
    val durationMinutes: Int? = null,       // Duración en minutos
    val type: String? = null,               // Tipo (ej: "A Domicilio")
    val location: String? = null,           // Dirección
    val clientNotes: String? = null         // Notas del cliente
)

data class Message(
    val id: String,
    val reservationId: String,
    val senderId: String,
    val content: String,
    val timestamp: Long
)

data class CreateReviewRequest(
    val rating: Int,
    val comment: String? = null
)

data class ReviewResponse(
    val id: String,
    val rating: Int,
    val comment: String?,
    val clientId: String,
    val clientName: String,
    val professionalId: String,
    val professionalName: String,
    val createdAt: String
)

data class MetricsResponse(
    val totalReservations: Int,
    val completedReservations: Int,
    val cancellations: Int,
    val averageRating: Double,
    val totalReviews: Int
)

data class ReputationResponse(
    val rating: Double,
    val reviews: Int,
    val completedServices: Int
)

data class ProfessionalStatusResponse(
    val id: String,
    val status: String,
    val message: String? = null
)

data class UpdateStatusRequest(
    val status: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterClientRequest(
    val name: String,
    val email: String,
    val password: String,
    val phoneNumber: String? = null
)

data class ProfessionalRegisterPayload(
    val name: String,
    val idNumber: String,
    val email: String,
    val password: String,
    val phoneNumber: String,
    val services: List<String>,
    val cities: List<String>,
    val documentUrl: String? = null,
    val idFrontUrl: String? = null,
    val idBackUrl: String? = null,
    val certificates: List<String>? = null
)

data class ServiceRequestInput(
    val description: String,
    val category: String,
    val location: String,
    val preferredDate: String? = null
)

data class AuthResponse(
    val user: User,
    val token: String
)
