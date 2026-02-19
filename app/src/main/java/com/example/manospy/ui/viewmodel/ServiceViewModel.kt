package com.example.manospy.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manospy.data.api.RetrofitClient
import com.example.manospy.data.model.Message
import com.example.manospy.data.model.MessageInput
import com.example.manospy.data.model.CreateChatRequest
import com.example.manospy.data.model.CreateChatResponse
import com.example.manospy.data.model.ConvertChatResponse
import com.example.manospy.data.model.RatingRequest
import com.example.manospy.data.model.ReputationResponse
import com.example.manospy.data.model.Reservation
import com.example.manospy.data.model.ReservationStatus
import com.example.manospy.data.model.ServiceRequest
import com.example.manospy.data.model.ServiceRequestInput
import com.example.manospy.data.model.ServiceRequestStatus
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class ServiceViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    // ==========================
    // Estado de nueva reservación
    // ==========================
    private val _reservationStep1Data = MutableStateFlow<Map<String, Any>>(emptyMap())
    val reservationStep1Data: StateFlow<Map<String, Any>> = _reservationStep1Data

    private val _reservationStep2Data = MutableStateFlow<Map<String, Any>>(emptyMap())
    val reservationStep2Data: StateFlow<Map<String, Any>> = _reservationStep2Data

    fun updateReservationStep1(
        category: String,
        categoryLabel: String = "",
        description: String,
        photoUris: List<Uri>
    ) {
        viewModelScope.launch {
            _reservationStep1Data.value = mapOf(
                "category" to category,
                "categoryLabel" to categoryLabel,
                "description" to description,
                "photoUris" to photoUris
            )
        }
    }

    fun updateReservationStep2(
        location: String,
        latitude: Double,
        longitude: Double,
        date: String,
        time: String
    ) {
        viewModelScope.launch {
            _reservationStep2Data.value = mapOf(
                "location" to location,
                "latitude" to latitude,
                "longitude" to longitude,
                "date" to date,
                "time" to time
            )
        }
    }

    // ==========================
    // Solicitudes de servicio
    // ==========================
    private val _serviceRequests =
        MutableStateFlow<NetworkResult<List<ServiceRequest>>>(NetworkResult.Loading)
    val serviceRequests: StateFlow<NetworkResult<List<ServiceRequest>>> = _serviceRequests

    private val _pendingRequests =
        MutableStateFlow<NetworkResult<List<ServiceRequest>>>(NetworkResult.Loading)
    val pendingRequests: StateFlow<NetworkResult<List<ServiceRequest>>> = _pendingRequests

    fun fetchServiceRequests(oficio: String, ciudad: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _serviceRequests.value = NetworkResult.Loading
            _serviceRequests.value = safeApiCall { api.getServiceRequestsByFilters(oficio, ciudad) }
        }
    }

    fun fetchServiceRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            _pendingRequests.value = NetworkResult.Loading
            _pendingRequests.value = safeApiCall { api.getServiceRequests() }
        }
    }

    // ==========================
    // Crear nueva solicitud
    // ==========================
    private val _createRequestStatus =
        MutableStateFlow<NetworkResult<ServiceRequest>?>(null)
    val createRequestStatus: StateFlow<NetworkResult<ServiceRequest>?> = _createRequestStatus

    private val _lastCreatedRequest = MutableStateFlow<ServiceRequest?>(null)
    val lastCreatedRequest: StateFlow<ServiceRequest?> = _lastCreatedRequest

    fun createRequest(
        clientId: String,
        category: String,
        categoryLabel: String = "",
        description: String,
        location: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        preferredDate: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _createRequestStatus.value = NetworkResult.Loading
            val requestInput = ServiceRequestInput(
                clientId = clientId,
                category = category,
                description = description,
                location = location,
                latitude = latitude,
                longitude = longitude,
                preferredDate = preferredDate
            )
            val result = safeApiCall { api.createServiceRequest(requestInput) }
            _createRequestStatus.value = result
            android.util.Log.d("ServiceViewModel", "createRequest result: $result")
            // Guardar la última solicitud creada
            if (result is NetworkResult.Success) {
                android.util.Log.d("ServiceViewModel", "Request created successfully: ${result.data}")
                _lastCreatedRequest.value = result.data
                // Convertir a Reservation y guardar en reservationDetail
                val reservation = Reservation(
                    id = result.data.id,
                    serviceRequestId = result.data.id,
                    clientId = clientId,
                    professionalId = "",
                    status = ReservationStatus.PENDING,
                    clientName = null,
                    serviceName = categoryLabel.ifEmpty { category },
                    date = preferredDate,
                    timeStart = null,
                    timeEnd = null,
                    durationMinutes = null,
                    type = "A Domicilio",
                    location = location,
                    clientNotes = description
                )
                _reservationDetail.value = reservation
                android.util.Log.d("ServiceViewModel", "Reservation saved: $reservation")
            } else {
                android.util.Log.e("ServiceViewModel", "Failed to create request: ${if (result is NetworkResult.Error) result.message else "Unknown error"}")
            }
        }
    }

    // Permitir limpiar el estado de creación de solicitud para evitar efectos colaterales
    fun clearCreateRequestStatus() {
        viewModelScope.launch {
            _createRequestStatus.value = null
        }
    }

    // ==========================
    // Reservas del cliente/profesional
    // ==========================
    private val _reservations =
        MutableStateFlow<NetworkResult<List<Reservation>>>(NetworkResult.Loading)
    val reservations: StateFlow<NetworkResult<List<Reservation>>> = _reservations

    private val _reservationsCompleted =
        MutableStateFlow<NetworkResult<List<Reservation>>>(NetworkResult.Loading)
    val reservationsCompleted: StateFlow<NetworkResult<List<Reservation>>> = _reservationsCompleted

    private val _acceptedReservations = MutableStateFlow<NetworkResult<List<Reservation>>>(NetworkResult.Loading)
    val acceptedReservations: StateFlow<NetworkResult<List<Reservation>>> = _acceptedReservations

    private val _completedReservations = MutableStateFlow<NetworkResult<List<Reservation>>>(NetworkResult.Loading)
    val completedReservations: StateFlow<NetworkResult<List<Reservation>>> = _completedReservations

    private val _cancelledReservations = MutableStateFlow<NetworkResult<List<Reservation>>>(NetworkResult.Loading)
    val cancelledReservations: StateFlow<NetworkResult<List<Reservation>>> = _cancelledReservations

    // ==========================
    // Detalle de reserva
    // ==========================
    private val _reservationDetail = MutableStateFlow<Reservation?>(null)
    val reservationDetail: StateFlow<Reservation?> = _reservationDetail

    private val _reservationDetailLoading = MutableStateFlow(false)
    val reservationDetailLoading: StateFlow<Boolean> = _reservationDetailLoading

    private val _reservationDetailError = MutableStateFlow<String?>(null)
    val reservationDetailError: StateFlow<String?> = _reservationDetailError

    // ==========================
    // Todas las reservas y solicitudes combinadas
    // ==========================
    private val _allReservationsAndRequests =
        MutableStateFlow<NetworkResult<List<Any>>>(NetworkResult.Loading)
    val allReservationsAndRequests: StateFlow<NetworkResult<List<Any>>> = _allReservationsAndRequests

    fun fetchAllReservationsAndRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            _allReservationsAndRequests.value = NetworkResult.Loading

            // Cargar ambas fuentes de datos
            val reservationsResult = safeApiCall { api.getReservations(null) }
            val requestsResult = safeApiCall { api.getServiceRequests() }

            when {
                reservationsResult is NetworkResult.Success && requestsResult is NetworkResult.Success -> {
                    val reservations = reservationsResult.data.filter { it.status == ReservationStatus.ACCEPTED || it.status == ReservationStatus.COMPLETED }
                    val requests = requestsResult.data.filter { it.status == ServiceRequestStatus.PENDING }

                    // Combinar en una lista de Any
                    val combined = mutableListOf<Any>()
                    combined.addAll(requests)
                    combined.addAll(reservations)

                    _allReservationsAndRequests.value = NetworkResult.Success(combined)
                }
                reservationsResult is NetworkResult.Error -> {
                    _allReservationsAndRequests.value = NetworkResult.Error(reservationsResult.message)
                }
                requestsResult is NetworkResult.Error -> {
                    _allReservationsAndRequests.value = NetworkResult.Error(requestsResult.message)
                }
            }
        }
    }

    fun fetchReservations(userId: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _reservations.value = NetworkResult.Loading
            _reservations.value = safeApiCall { api.getReservations(null) }
        }
    }

    fun fetchReservationsByStatus(status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            when (status) {
                "completed" -> {
                    _reservationsCompleted.value = NetworkResult.Loading
                    _reservationsCompleted.value = safeApiCall { api.getReservations("completed") }
                }
                "accepted" -> {
                    _acceptedReservations.value = NetworkResult.Loading
                    _acceptedReservations.value = safeApiCall { api.getReservations("accepted") }
                }
            }
        }
    }

    // ==========================
    // Métodos para cliente
    // ==========================
    fun loadClientReservations(clientId: String) {
        fetchReservations()
    }

    fun loadClientServiceRequests(clientId: String) {
        // TODO: Implement when backend supports getting client service requests
        // For now, pending requests are handled as PENDING reservations
    }

    // ==========================
    // Actualizar estados de reserva
    // ==========================
    fun acceptReservation(reservationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall { api.acceptReservation(reservationId) }
            // Recargar reservas después de aceptar
            fetchReservations()
        }
    }

    fun cancelReservation(reservationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall { api.cancelReservation(reservationId) }
            // Limpiar detalle y refrescar listas para que la UI (y el navbar) se actualicen
            _reservationDetail.value = null
            // Refrescar reservas y solicitudes para sincronizar estado
            fetchReservations()
            fetchServiceRequests()
            // Limpiar estado de creación por seguridad
            _lastCreatedRequest.value = null
            _reservationStep1Data.value = emptyMap()
            _reservationStep2Data.value = emptyMap()
        }
    }

    fun cancelServiceRequest(requestId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall { api.cancelServiceRequest(requestId) }
            // Limpiar estado relacionado con la solicitud cancelada
            _reservationDetail.value = null
            _lastCreatedRequest.value = null
            _createRequestStatus.value = null
            _reservationStep1Data.value = emptyMap()
            _reservationStep2Data.value = emptyMap()

            // Refrescar la lista de solicitudes después de cancelar
            fetchServiceRequests()
        }
    }

    fun completeReservation(reservationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall { api.completeReservation(reservationId) }
            // Recargar reservas
            fetchReservations()
        }
    }

    fun getPendingRequests() {
        viewModelScope.launch(Dispatchers.IO) {
            _pendingRequests.value = NetworkResult.Loading
            _pendingRequests.value = safeApiCall { api.getServiceRequests() }
        }
    }

    fun getAcceptedReservations() {
        viewModelScope.launch(Dispatchers.IO) {
            _acceptedReservations.value = NetworkResult.Loading
            _acceptedReservations.value = safeApiCall { api.getReservations("accepted") }
        }
    }

    fun getCompletedReservations() {
        viewModelScope.launch(Dispatchers.IO) {
            _completedReservations.value = NetworkResult.Loading
            _completedReservations.value = safeApiCall { api.getReservations("completed") }
        }
    }

    fun getCancelledReservations() {
        viewModelScope.launch(Dispatchers.IO) {
            _cancelledReservations.value = NetworkResult.Loading
            _cancelledReservations.value = safeApiCall { api.getReservations("cancelled") }
        }
    }

    fun getActiveReservations(): List<Reservation> {
        return (_acceptedReservations.value as? NetworkResult.Success)?.data ?: emptyList()
    }

    fun hasPendingRequest(): Boolean {
        return (_pendingRequests.value as? NetworkResult.Success)?.data?.any { it.status?.name?.lowercase() == "pending" } ?: false
    }

    fun getActiveReservationOrRequest(): Any? {
        val accepted = (_acceptedReservations.value as? NetworkResult.Success)?.data?.firstOrNull { it.status?.name?.lowercase() == "accepted" }
        if (accepted != null) return accepted
        val pending = (_pendingRequests.value as? NetworkResult.Success)?.data?.firstOrNull { it.status?.name?.lowercase() == "pending" }
        return pending
    }

    fun fetchReservationDetail(reservationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _reservationDetailLoading.value = true
            _reservationDetailError.value = null
            _reservationDetail.value = null
            
            android.util.Log.d("ServiceViewModel", "fetchReservationDetail: Fetching for ID: $reservationId")
            
            val result = safeApiCall { api.getReservationById(reservationId) }
            when (result) {
                is NetworkResult.Success -> {
                    _reservationDetail.value = result.data
                    _reservationDetailError.value = null
                    android.util.Log.d("ServiceViewModel", "fetchReservationDetail: Success - ${result.data}")
                }
                is NetworkResult.Error -> {
                    _reservationDetail.value = null
                    _reservationDetailError.value = result.message ?: "Error al cargar los detalles de la reserva"
                    android.util.Log.e("ServiceViewModel", "fetchReservationDetail: Error - ${result.message}")
                }
                else -> {
                    _reservationDetail.value = null
                    _reservationDetailError.value = "No se pudieron cargar los detalles"
                    android.util.Log.e("ServiceViewModel", "fetchReservationDetail: Unknown error")
                }
            }
            _reservationDetailLoading.value = false
        }
    }

    fun updateReservationStatus(reservationId: String, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = safeApiCall { api.updateReservationStatus(reservationId, status) }
            when (result) {
                is NetworkResult.Success -> {
                    _reservationDetail.value = result.data
                }
                else -> {
                    _reservationDetail.value = null
                }
            }
        }
    }

    // ==========================
    // Reputación del profesional
    // ==========================
    private val _professionalReputation = MutableStateFlow<NetworkResult<ReputationResponse>?>(null)
    val professionalReputation: StateFlow<NetworkResult<ReputationResponse>?> = _professionalReputation

    fun fetchProfessionalReputation(professionalId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _professionalReputation.value = safeApiCall { api.getReputation(professionalId) }
        }
    }

    // ==========================
    // Chat / Mensajes
    // ==========================
    private val _messages = MutableStateFlow<NetworkResult<List<Message>>>(NetworkResult.Loading)
    val messages: StateFlow<NetworkResult<List<Message>>> = _messages

    fun fetchMessages(reservationId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _messages.value = NetworkResult.Loading
            _messages.value = safeApiCall { api.getMessages(reservationId) }
        }
    }

    fun sendMessage(reservationId: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = safeApiCall { api.sendMessage(reservationId, MessageInput("", text)) }
            if (result is NetworkResult.Success) {
                fetchMessages(reservationId)
            }
        }
    }

    // Chats previos a formalizar
    fun createChat(offerId: String, professionalId: String? = null) = viewModelScope.launch(Dispatchers.IO) {
        val request = CreateChatRequest(offerId = offerId, professionalId = professionalId)
        val result = safeApiCall { api.createChat(request) }
        if (result is NetworkResult.Success) {
            // devolver o navegar desde la UI con result.data.chatId
            android.util.Log.d("ServiceViewModel", "Chat creado: ${result.data}")
        } else {
            android.util.Log.e("ServiceViewModel", "Error creando chat: ${(result as? NetworkResult.Error)?.message}")
        }
    }

    suspend fun createChatSync(offerId: String, professionalId: String? = null): NetworkResult<CreateChatResponse> {
        return withContext(Dispatchers.IO) {
            safeApiCall { api.createChat(CreateChatRequest(offerId = offerId, professionalId = professionalId)) }
        }
    }

    fun fetchChatMessages(chatId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _messages.value = NetworkResult.Loading
            _messages.value = safeApiCall { api.getChatMessages(chatId) }
        }
    }

    fun sendChatMessage(chatId: String, text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = safeApiCall { api.postChatMessage(chatId, MessageInput("", text)) }
            if (result is NetworkResult.Success) {
                fetchChatMessages(chatId)
            }
        }
    }

    suspend fun convertChatToReservation(chatId: String): NetworkResult<ConvertChatResponse> =
        withContext(Dispatchers.IO) {
            safeApiCall { api.convertChat(chatId) }
        }

    fun submitReview(reservationId: String, rating: Int, reviewText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall { api.rateReservation(reservationId, RatingRequest(rating, reviewText)) }
        }
    }

    // ==========================
    // Métodos de navegación inteligente
    // ==========================
    fun getActivePendingReservation(): Reservation? {
        val reservations = (_reservations.value as? NetworkResult.Success)?.data ?: return null
        return reservations.find { it.status.name.uppercase() == "PENDING" }
    }

    fun getActiveAcceptedReservation(): Reservation? {
        val reservations = (_reservations.value as? NetworkResult.Success)?.data ?: return null
        return reservations.find { it.status.name.uppercase() == "ACCEPTED" }
    }

    // ==========================
    // Filtros por estado de solicitud
    // ==========================
    // Limpiar datos por cambio de usuario
    // ==========================
    fun clearAllData() {
        viewModelScope.launch {
            // Limpiar datos de reservaciones
            _reservationDetail.value = null
            _reservationDetailLoading.value = false
            _lastCreatedRequest.value = null
            
            // Limpiar datos de formularios
            _reservationStep1Data.value = emptyMap()
            _reservationStep2Data.value = emptyMap()
            
            // Limpiar datos de solicitudes
            _serviceRequests.value = NetworkResult.Loading
            _pendingRequests.value = NetworkResult.Loading
            _reservations.value = NetworkResult.Loading
            _reservationsCompleted.value = NetworkResult.Loading
            _acceptedReservations.value = NetworkResult.Loading
            _completedReservations.value = NetworkResult.Loading
            _cancelledReservations.value = NetworkResult.Loading
            _allReservationsAndRequests.value = NetworkResult.Loading
            
            android.util.Log.d("ServiceViewModel", "All data cleared for new user session")
        }
    }

    // ==========================
    // Ofertas de profesionales
    // ==========================
    fun getOffers(): List<ProfessionalOffer> {
        // Si existe una última solicitud creada, devolverla como ejemplo de oferta
        val last = _lastCreatedRequest.value
        if (last != null) {
            return listOf(
                ProfessionalOffer(
                    id = last.id,
                    clientName = last.clientName ?: "Cliente",
                    serviceName = last.category,
                    budget = "Gs. 0"
                )
            )
        }

        // Fallback: ofertas de ejemplo para mostrar tarjetas en el Home
        return listOf(
            ProfessionalOffer(id = "offer1", clientName = "María G.", serviceName = "Plomería - Reparación", budget = "Gs. 150.000", clientRating = 4.8, reviewCount = 12, urgency = "Hoy"),
            ProfessionalOffer(id = "offer2", clientName = "Carlos R.", serviceName = "Electricidad - Instalación", budget = "Gs. 220.000", clientRating = 4.6, reviewCount = 8, urgency = "24h"),
            ProfessionalOffer(id = "offer3", clientName = "Luisa M.", serviceName = "Aire Acondicionado - Mantenimiento", budget = "Gs. 180.000", clientRating = 4.9, reviewCount = 20, urgency = "Hoy")
        )
    }


    // Safe API call wrapper
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

    // Funciones de filtrado para ClientHistoryScreen
    fun getFilteredAllReservations(): List<Reservation> {
        return (_reservations.value as? NetworkResult.Success)?.data ?: emptyList()
    }

    fun getFilteredCompletedReservations(): List<Reservation> {
        return getFilteredAllReservations().filter { it.status == ReservationStatus.COMPLETED }
    }

    fun getFilteredInProcessReservations(): List<Reservation> {
        return getFilteredAllReservations().filter { it.status == ReservationStatus.ACCEPTED }
    }

    fun getFilteredPendingRequests(): List<ServiceRequest> {
        return (_pendingRequests.value as? NetworkResult.Success)?.data?.filter {
            it.status == ServiceRequestStatus.PENDING
        } ?: emptyList()
    }

    // Función para refrescar datos
    fun refreshData() {
        fetchReservations()
        fetchServiceRequests()
    }

    // ==========================
    // Gestión de direcciones
    // ==========================
    private val _clientAddresses =
        MutableStateFlow<NetworkResult<List<com.example.manospy.data.model.ClientAddress>>>(NetworkResult.Loading)
    val clientAddresses: StateFlow<NetworkResult<List<com.example.manospy.data.model.ClientAddress>>> = _clientAddresses

    // Inicializar como null para no mostrar estado "Guardando" al montar la pantalla
    private val _saveAddressStatus =
        MutableStateFlow<NetworkResult<com.example.manospy.data.model.ClientAddress>?>(null)
    val saveAddressStatus: StateFlow<NetworkResult<com.example.manospy.data.model.ClientAddress>?> = _saveAddressStatus

    fun fetchAddresses() {
        viewModelScope.launch(Dispatchers.IO) {
            _clientAddresses.value = NetworkResult.Loading
            _clientAddresses.value = safeApiCall { api.getAddresses() }
        }
    }

    fun saveAddress(
        label: String,
        fullAddress: String,
        latitude: Double? = null,
        longitude: Double? = null,
        reference: String? = null
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _saveAddressStatus.value = NetworkResult.Loading
            
            // Dividir fullAddress en dirección y ciudad
            val parts = fullAddress.split(",").map { it.trim() }
            val ciudad = if (parts.size > 1) parts.last() else "Asunción" // Última parte como ciudad, o Asunción por defecto
            val direccion = if (parts.size > 1) parts.dropLast(1).joinToString(", ") else fullAddress // Todo menos la última parte
            
            val request = com.example.manospy.data.model.SaveAddressRequest(
                label = label,
                direccion = direccion,
                ciudad = ciudad,
                latitude = latitude,
                longitude = longitude,
                reference = reference
            )
            _saveAddressStatus.value = safeApiCall { api.saveAddress(request) }
            // Refrescar lista de direcciones después de guardar
            fetchAddresses()
        }
    }

    fun deleteAddress(addressId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            safeApiCall { api.deleteAddress(addressId) }
            fetchAddresses()
        }
    }

    // ==========================
    // Selected Offer para detalle
    // ==========================
    private val _selectedOffer = MutableStateFlow<ProfessionalOffer?>(null)
    val selectedOffer: StateFlow<ProfessionalOffer?> = _selectedOffer

    fun setSelectedOffer(offer: ProfessionalOffer?) {
        _selectedOffer.value = offer
    }
}

// Modelo simple para ofertas
data class ProfessionalOffer(
    val id: String = "",
    val clientName: String = "",
    val serviceName: String = "",
    val budget: String = "",
    val clientRating: Double = 0.0,
    val reviewCount: Int = 0,
    val urgency: String = "",
    val avatarUrl: String = ""
)
