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
    val idBackUrl: String? = null,             // Foto cédula dorso
    val avatarUrl: String? = null,
    // ✅ Estadísticas de cliente
    val totalRequests: Int = 0,                // Total de solicitudes realizadas
    val completedServices: Int = 0,            // Servicios completados
    val rating: Double = 0.0,                  // Calificación promedio
    val notificationsEnabled: Boolean = true   // Estado de notificaciones
)

enum class ServiceRequestStatus {
    @SerializedName("pending")
    PENDING,
    @SerializedName("accepted")
    ACCEPTED,
    @SerializedName("cancelled")
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
    @SerializedName("pending")
    PENDING,
    @SerializedName("accepted")
    ACCEPTED,
    @SerializedName("completed")
    COMPLETED,
    @SerializedName("cancelled")
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
    val reservationId: String?,
    val chatId: String?,
    val senderId: String,
    val content: String,
    val timestamp: Long
)

data class LoginResponse(
    val token: String,
    val user: User
)

data class AuthRequest(
    val email: String,
    val password: String,
    val name: String? = null,
    val role: String? = null,
    val phoneNumber: String? = null,
    val idNumber: String? = null,
    val category: String? = null,
    val bio: String? = null,
    val experience: String? = null,
    val location: String? = null,
    val documentUrl: String? = null
)

data class LoginRequest(
    val email: String,
    val password: String,
    val role: String
)

data class RegisterClientRequest(
    val name: String,
    val email: String,
    val password: String,
    val phone: String
)

data class ReputationResponse(
    val rating: Double,
    val totalReviews: Int,
    val distribution: RatingDistribution
)

data class RatingDistribution(
    val five: Int,
    val four: Int,
    val three: Int,
    val two: Int,
    val one: Int
)

data class ReviewResponse(
    val id: String,
    val authorName: String,
    val date: String,
    val rating: Int,
    val comment: String,
    val likes: Int,
    val avatarUrl: String? = null
)

data class CreateReviewRequest(
    val rating: Int,
    val comment: String
)

data class MetricsResponse(
    val servicesCount: Int,
    val ratingAverage: Double,
    val trendServices: String,
    val trendRating: String,
    val weeklyActivity: List<Float>,
    val performance: PerformanceBlock,
    val topServices: List<TopServiceItem>
)

data class PerformanceBlock(
    val satisfaction: Float,
    val punctuality: Float,
    val completion: Float
)

data class TopServiceItem(
    val label: String,
    val share: Float
)
// ✅ NUEVO: Modelos para chat y rating
data class MessageInput(
    val senderId: String,
    val content: String
)

data class RatingRequest(
    val rating: Int,
    val comment: String? = null
)

// ✅ NUEVO: Modelo para direcciones guardadas
data class ClientAddress(
    val id: String? = null,
    val clientId: String? = null,
    val label: String,              // "Casa", "Trabajo", etc.
    val fullAddress: String,        // Dirección completa
    val latitude: Double? = null,
    val longitude: Double? = null,
    val reference: String? = null   // Referencia adicional
)

// ✅ Request para guardar dirección
data class SaveAddressRequest(
    val label: String,
    val direccion: String,
    val ciudad: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val reference: String? = null
)

data class CreateReservationRequest(
    val serviceRequestId: String
)

// Chat DTOs
data class CreateChatRequest(
    val offerId: String,
    val professionalId: String? = null
)

data class CreateChatResponse(
    val chatId: String
)

data class ConvertChatResponse(
    val reservationId: String,
    val serviceRequestId: String
)