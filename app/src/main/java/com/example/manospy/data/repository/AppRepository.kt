package com.example.manospy.data.repository

import com.example.manospy.data.api.ApiService
import com.example.manospy.data.model.*
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class AppRepository(
    private val api: ApiService
) {

    // ---------- AUTH ----------
    suspend fun login(email: String, password: String, role: String): NetworkResult<AuthResponse> =
        safeApiCall { api.login(LoginRequest(email, password, role)) }

    suspend fun registerClient(name: String, email: String, password: String, phone: String): NetworkResult<AuthResponse> =
        safeApiCall { api.registerClient(RegisterClientRequest(name, email, password, phone)) }

    suspend fun registerProfessional(payload: ProfessionalRegisterPayload): NetworkResult<AuthResponse> =
        safeApiCall { api.registerProfessional(payload) }

    suspend fun me(): NetworkResult<User> = safeApiCall { api.me() }

    // ✅ NUEVO: Actualizar perfil de usuario
    suspend fun updateProfile(name: String? = null, email: String? = null, phoneNumber: String? = null): NetworkResult<User> =
        safeApiCall { api.updateProfile(com.example.manospy.data.api.UpdateProfileRequest(name, email, phoneNumber)) }

    // ✅ NUEVO: Solicitar verificación de teléfono
    suspend fun requestPhoneVerification(newPhoneNumber: String, method: String = "sms"): NetworkResult<com.example.manospy.data.api.PhoneVerificationResponse> =
        safeApiCall { api.requestPhoneVerification(com.example.manospy.data.api.RequestPhoneVerificationRequest(newPhoneNumber, method)) }

    // ✅ NUEVO: Verificar código y cambiar teléfono
    suspend fun verifyPhoneWithCode(verificationId: String, code: String): NetworkResult<User> =
        safeApiCall { api.verifyPhoneWithCode(com.example.manospy.data.api.VerifyPhoneCodeRequest(verificationId, code)) }

    // ✅ NUEVO: Solicitar verificación de correo
    suspend fun requestEmailVerification(): NetworkResult<com.example.manospy.data.api.PhoneVerificationResponse> =
        safeApiCall { api.requestEmailVerification() }

    // ✅ NUEVO: Verificar código de correo
    suspend fun verifyEmailCode(verificationId: String, code: String): NetworkResult<User> =
        safeApiCall { api.verifyEmailCode(com.example.manospy.data.api.VerifyEmailCodeRequest(verificationId, code)) }

    // ✅ NUEVO: Solicitar cambio de correo
    suspend fun requestEmailChange(newEmail: String): NetworkResult<com.example.manospy.data.api.PhoneVerificationResponse> =
        safeApiCall { api.requestEmailChange(com.example.manospy.data.api.RequestEmailChangeRequest(newEmail)) }

    // ✅ NUEVO: Verificar cambio de correo
    suspend fun verifyEmailChange(verificationId: String, code: String): NetworkResult<User> =
        safeApiCall { api.verifyEmailChange(com.example.manospy.data.api.VerifyEmailChangeRequest(verificationId, code)) }

    // ---------- PROFESSIONAL VALIDATION ----------
    suspend fun getProfessionalStatus(professionalId: String): NetworkResult<ProfessionalStatusResponse> =
        safeApiCall { api.getProfessionalStatus(professionalId) }

    suspend fun updateProfessionalStatus(professionalId: String, status: String): NetworkResult<ProfessionalStatusResponse> =
        safeApiCall { api.updateProfessionalStatus(professionalId, UpdateStatusRequest(status)) }

    // ---------- SERVICE REQUESTS ----------
    suspend fun getServiceRequests(): NetworkResult<List<ServiceRequest>> =
        safeApiCall { api.getServiceRequests() }

    suspend fun getServiceRequestsByFilters(oficio: String, ciudad: String): NetworkResult<List<ServiceRequest>> =
        safeApiCall { api.getServiceRequestsByFilters(oficio, ciudad) }

    suspend fun createServiceRequest(request: ServiceRequestInput): NetworkResult<ServiceRequest> =
        safeApiCall { api.createServiceRequest(request) }

    // ---------- RESERVATIONS ----------
    suspend fun getReservationsByUser(status: String? = null): NetworkResult<List<Reservation>> =
        safeApiCall { api.getReservations(status) }

    suspend fun acceptServiceRequest(requestId: String): NetworkResult<Reservation> =
        safeApiCall { api.acceptServiceRequest(requestId) }

    suspend fun updateReservationStatus(reservationId: String, status: String): NetworkResult<Reservation> =
        safeApiCall { api.updateReservationStatus(reservationId, status) }

    // ✅ NUEVO: Obtener detalle de reserva
    suspend fun getReservationById(reservationId: String): NetworkResult<Reservation> =
        safeApiCall { api.getReservationById(reservationId) }

    // ✅ NUEVO: Cancelar reserva
    suspend fun cancelReservation(reservationId: String) =
        withContext(Dispatchers.IO) {
            val response = api.cancelReservation(reservationId)
            if (!response.isSuccessful) {
                throw IOException("Error al cancelar reserva: ${response.code()} ${response.message()}")
            }
        }

    // ---------- MESSAGES ----------
    suspend fun getMessages(reservationId: String): NetworkResult<List<Message>> =
        safeApiCall { api.getMessages(reservationId) }

    suspend fun sendMessage(reservationId: String, content: String): NetworkResult<Message> =
        safeApiCall { api.sendMessage(reservationId, MessageInput(senderId = "", content = content)) } // senderId se obtiene del JWT en backend

    // Chats (pre-reservation)
    suspend fun createChat(offerId: String, professionalId: String? = null): NetworkResult<com.example.manospy.data.model.CreateChatResponse> =
        safeApiCall { api.createChat(com.example.manospy.data.model.CreateChatRequest(offerId, professionalId)) }

    suspend fun getChatMessages(chatId: String): NetworkResult<List<Message>> =
        safeApiCall { api.getChatMessages(chatId) }

    suspend fun postChatMessage(chatId: String, content: String): NetworkResult<Message> =
        safeApiCall { api.postChatMessage(chatId, com.example.manospy.data.model.MessageInput(senderId = "", content = content)) }

    suspend fun convertChat(chatId: String): NetworkResult<com.example.manospy.data.model.ConvertChatResponse> =
        safeApiCall { api.convertChat(chatId) }

    // ---------- REPUTATION & REVIEWS ----------
    suspend fun getReputation(professionalId: String): NetworkResult<ReputationResponse> =
        safeApiCall { api.getReputation(professionalId) }

    suspend fun getReviews(professionalId: String, page: Int, size: Int): NetworkResult<List<ReviewResponse>> =
        safeApiCall { api.getReviews(professionalId, page, size) }

    suspend fun postReview(professionalId: String, request: CreateReviewRequest): NetworkResult<ReviewResponse> =
        safeApiCall { api.postReview(professionalId, request) }

    // ---------- METRICS ----------
    suspend fun getMetrics(professionalId: String): NetworkResult<MetricsResponse> =
        safeApiCall { api.getMetrics(professionalId) }

    // ✅ NUEVO: Calificar reserva
    suspend fun rateReservation(reservationId: String, rating: Int, comment: String?): NetworkResult<Unit> =
        safeApiCall { api.rateReservation(reservationId, RatingRequest(rating, comment)) }

    // ---------- PHOTO UPLOAD ----------
    suspend fun uploadPhotoBase64(base64Data: String, fileName: String): NetworkResult<com.example.manospy.data.api.PhotoUploadResponse> =
        safeApiCall { api.uploadPhotoBase64(com.example.manospy.data.api.PhotoUploadRequest(base64Data, fileName)) }

    // ---------- SAFE CALL ----------
    private suspend fun <T> safeApiCall(call: suspend () -> retrofit2.Response<T>): NetworkResult<T> =
        withContext(Dispatchers.IO) {
            try {
                val response = call()
                if (response.isSuccessful && response.body() != null) {
                    NetworkResult.Success(response.body()!!)
                } else {
                    val errorMsg = "Error ${response.code()}: ${response.message()}"
                    NetworkResult.Error(errorMsg)
                }
            } catch (e: HttpException) {
                NetworkResult.Error("HTTP error ${e.code()}: ${e.message()}")
            } catch (e: IOException) {
                NetworkResult.Error("Error de conexión: ${e.localizedMessage}")
            } catch (e: Exception) {
                NetworkResult.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
}
