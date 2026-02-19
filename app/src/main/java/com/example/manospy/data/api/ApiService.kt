package com.example.manospy.data.api

import com.example.manospy.data.model.AuthResponse
import com.example.manospy.data.model.ClientAddress
import com.example.manospy.data.model.CreateReservationRequest
import com.example.manospy.data.model.CreateReviewRequest
import com.example.manospy.data.model.LoginRequest
import com.example.manospy.data.model.Message
import com.example.manospy.data.model.MessageInput
import com.example.manospy.data.model.MetricsResponse
import com.example.manospy.data.model.ProfessionalStatusResponse
import com.example.manospy.data.model.ProfessionalRegisterPayload
import com.example.manospy.data.model.RatingRequest
import com.example.manospy.data.model.RegisterClientRequest
import com.example.manospy.data.model.Reservation
import com.example.manospy.data.model.ReputationResponse
import com.example.manospy.data.model.ReviewResponse
import com.example.manospy.data.model.SaveAddressRequest
import com.example.manospy.data.model.ServiceRequest
import com.example.manospy.data.model.ServiceRequestInput
import com.example.manospy.data.model.UpdateStatusRequest
import com.example.manospy.data.model.User
import com.example.manospy.data.model.CreateChatRequest
import com.example.manospy.data.model.CreateChatResponse
import com.example.manospy.data.model.ConvertChatResponse
import retrofit2.Response
import retrofit2.http.HTTP
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class PhotoUploadRequest(
    val base64Data: String,
    val fileName: String
)

data class PhotoUploadResponse(
    val success: Boolean,
    val photoUrl: String,
    val fullUrl: String
)

// ✅ NUEVO: Para actualizar perfil
data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null
)

// ✅ NUEVO: Para solicitar verificación de teléfono
data class RequestPhoneVerificationRequest(
    val newPhoneNumber: String,
    val method: String? = "sms" // "sms" o "whatsapp"
)

data class PhoneVerificationResponse(
    val success: Boolean,
    val message: String? = null,
    val verificationId: String?,
    val code: String? = null // ✅ Código OTP para abrir WhatsApp manualmente
)

// ✅ NUEVO: Para verificar códiho y cambiar teléfono
data class VerifyPhoneCodeRequest(
    val verificationId: String,
    val code: String
)

// ✅ NUEVO: Para solicitar verificación de correo
data class RequestEmailVerificationRequest(
    val dummy: String = "" // No requiere params
)

// ✅ NUEVO: Para verificar código de correo
data class VerifyEmailCodeRequest(
    val verificationId: String,
    val code: String
)

// ✅ NUEVO: Para solicitar cambio de correo
data class RequestEmailChangeRequest(
    val newEmail: String
)

// ✅ NUEVO: Para verificar cambio de correo
data class VerifyEmailChangeRequest(
    val verificationId: String,
    val code: String
)

interface ApiService {

    // ---------- AUTH ----------
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register/client")
    suspend fun registerClient(@Body request: RegisterClientRequest): Response<AuthResponse>

    @POST("auth/register/professional")
    suspend fun registerProfessional(@Body data: ProfessionalRegisterPayload): Response<AuthResponse>

    @GET("auth/me")
    suspend fun me(): Response<User>

    // ✅ NUEVO: Actualizar perfil de usuario
    @PUT("auth/profile")
    suspend fun updateProfile(@Body data: UpdateProfileRequest): Response<User>

    // ✅ NUEVO: Solicitar cambio de teléfono (enviar OTP)
    @POST("auth/phone/verify-request")
    suspend fun requestPhoneVerification(@Body data: RequestPhoneVerificationRequest): Response<PhoneVerificationResponse>

    // ✅ NUEVO: Verificar código y cambiar teléfono
    @POST("auth/phone/verify")
    suspend fun verifyPhoneWithCode(@Body data: VerifyPhoneCodeRequest): Response<User>

    // ✅ NUEVO: Solicitar verificación de correo
    @POST("auth/email/verify-request")
    suspend fun requestEmailVerification(): Response<PhoneVerificationResponse>

    // ✅ NUEVO: Verificar código de correo
    @POST("auth/email/verify")
    suspend fun verifyEmailCode(@Body data: VerifyEmailCodeRequest): Response<User>

    // ✅ NUEVO: Solicitar cambio de correo
    @POST("auth/email/change-request")
    suspend fun requestEmailChange(@Body data: RequestEmailChangeRequest): Response<PhoneVerificationResponse>

    // ✅ NUEVO: Verificar cambio de correo
    @POST("auth/email/change-verify")
    suspend fun verifyEmailChange(@Body data: VerifyEmailChangeRequest): Response<User>

    // ---------- PROFESSIONAL VALIDATION ----------
    @GET("professional/{id}/status")
    suspend fun getProfessionalStatus(@Path("id") id: String): Response<ProfessionalStatusResponse>

    @PUT("professional/{id}/status")
    suspend fun updateProfessionalStatus(
        @Path("id") id: String,
        @Body request: UpdateStatusRequest
    ): Response<ProfessionalStatusResponse>

    // ---------- SERVICE REQUESTS ----------
    @GET("services/requests")
    suspend fun getServiceRequests(): Response<List<ServiceRequest>>

    @GET("services/requests")
    suspend fun getServiceRequestsByFilters(
        @Query("oficio") oficio: String,
        @Query("ciudad") ciudad: String
    ): Response<List<ServiceRequest>>

    @POST("services/requests")
    suspend fun createServiceRequest(@Body request: ServiceRequestInput): Response<ServiceRequest>

    @POST("services/requests/{id}/accept")
    suspend fun acceptServiceRequest(@Path("id") requestId: String): Response<Reservation>

    @HTTP(method = "DELETE", path = "services/requests/{id}", hasBody = false)
    suspend fun cancelServiceRequest(@Path("id") requestId: String): Response<Unit>

    // ---------- RESERVATIONS ----------
    @GET("reservations/user")
    suspend fun getReservations(
        @Query("status") status: String? = null
    ): Response<List<Reservation>>

    @PUT("reservations/{id}/status")
    suspend fun updateReservationStatus(
        @Path("id") reservationId: String,
        @Query("status") status: String
    ): Response<Reservation>

    // ✅ NUEVO: Obtener detalle de reserva
    @GET("reservations/{id}")
    suspend fun getReservationById(@Path("id") reservationId: String): Response<Reservation>

    // ✅ NUEVO: Cancelar reserva
    @POST("reservations/{id}/cancel")
    suspend fun cancelReservation(@Path("id") reservationId: String): Response<Unit>

    // ✅ NUEVO: Calificar reserva completada
    @POST("reservations/{id}/rate")
    suspend fun rateReservation(
        @Path("id") reservationId: String,
        @Body body: CreateReviewRequest
    ): Response<ReviewResponse>

    // ---------- REPUTATION ----------
    @GET("professionals/{id}/reputation")
    suspend fun getReputation(@Path("id") professionalId: String): Response<ReputationResponse>

    // ---------- REVIEWS ----------
    @GET("professionals/{id}/reviews")
    suspend fun getReviews(
        @Path("id") professionalId: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<List<ReviewResponse>>

    @POST("professionals/{id}/reviews")
    suspend fun postReview(
        @Path("id") professionalId: String,
        @Body body: CreateReviewRequest
    ): Response<ReviewResponse>

    // ---------- METRICS ----------
    @GET("professionals/{id}/metrics")
    suspend fun getMetrics(@Path("id") professionalId: String): Response<MetricsResponse>

    // ✅ NUEVO: Chat
    @GET("messages/{reservationId}")
    suspend fun getMessages(@Path("reservationId") reservationId: String): Response<List<Message>>

    @POST("messages/{reservationId}/send")
    suspend fun sendMessage(
        @Path("reservationId") reservationId: String,
        @Body message: MessageInput
    ): Response<Message>

    // ✅ NUEVO: Profesional Profile
    @GET("professionals/{id}/profile")
    suspend fun getProfessionalProfile(@Path("id") professionalId: String): Response<User>

    @POST("reservations")
    suspend fun createReservation(@Body request: CreateReservationRequest): Response<Reservation>

    @POST("reservations/from-service-request")
    suspend fun createReservationFromServiceRequest(@Body request: CreateReservationRequest): Response<Reservation>

    @POST("reservations/{reservationId}/accept")
    suspend fun acceptReservation(@Path("reservationId") reservationId: String): Response<Reservation>

    @POST("reservations/{reservationId}/complete")
    suspend fun completeReservation(@Path("reservationId") reservationId: String): Response<Reservation>

    @POST("reservations/{reservationId}/reject")
    suspend fun rejectReservation(@Path("reservationId") reservationId: String): Response<Unit>

    // ✅ NUEVO: Rating
    @POST("reservations/{reservationId}/rate")
    suspend fun rateReservation(
        @Path("reservationId") reservationId: String,
        @Body rating: RatingRequest
    ): Response<Unit>

    // ---------- PHOTO UPLOAD ----------
    @POST("upload/photo")
    suspend fun uploadPhotoBase64(@Body request: PhotoUploadRequest): Response<PhotoUploadResponse>
    
    // ---------- CHATS ----------
    @POST("chats")
    suspend fun createChat(@Body request: CreateChatRequest): Response<CreateChatResponse>

    @GET("chats/{chatId}/messages")
    suspend fun getChatMessages(@Path("chatId") chatId: String): Response<List<Message>>

    @POST("chats/{chatId}/messages")
    suspend fun postChatMessage(@Path("chatId") chatId: String, @Body message: MessageInput): Response<Message>

    @POST("chats/{chatId}/convert")
    suspend fun convertChat(@Path("chatId") chatId: String): Response<ConvertChatResponse>
    
    // Endpoint para calificación de soporte
    @POST("soporte")
    suspend fun rateSupport(@Body body: Map<String, Any>): Response<Unit>

    // ---------- ADDRESSES ----------
    @POST("addresses")
    suspend fun saveAddress(@Body request: SaveAddressRequest): Response<ClientAddress>

    @GET("addresses")
    suspend fun getAddresses(): Response<List<ClientAddress>>

    @GET("addresses/{id}")
    suspend fun getAddressById(@Path("id") addressId: String): Response<ClientAddress>

    @PUT("addresses/{id}")
    suspend fun updateAddress(
        @Path("id") addressId: String,
        @Body request: SaveAddressRequest
    ): Response<ClientAddress>

    @HTTP(method = "DELETE", path = "addresses/{id}", hasBody = false)
    suspend fun deleteAddress(@Path("id") addressId: String): Response<Unit>
}
