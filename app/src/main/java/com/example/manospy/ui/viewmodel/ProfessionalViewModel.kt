package com.example.manospy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manospy.data.api.RetrofitClient
import com.example.manospy.data.model.Reservation
import com.example.manospy.data.model.ServiceRequest
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.IOException

class ProfessionalViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    // ==========================
    // Solicitudes Pendientes
    // ==========================
    private val _serviceRequests =
        MutableStateFlow<NetworkResult<List<ServiceRequest>>>(NetworkResult.Idle)
    val serviceRequests: StateFlow<NetworkResult<List<ServiceRequest>>> = _serviceRequests

    fun loadServiceRequests(oficio: String, ciudad: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _serviceRequests.value = NetworkResult.Loading
            _serviceRequests.value = safeApiCall { api.getServiceRequestsByFilters(oficio, ciudad) }
        }
    }

    // ==========================
    // Reservas Actuales
    // ==========================
    private val _reservations =
        MutableStateFlow<NetworkResult<List<Reservation>>>(NetworkResult.Idle)
    val reservations: StateFlow<NetworkResult<List<Reservation>>> = _reservations

    fun loadReservations(professionalId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _reservations.value = NetworkResult.Loading
            _reservations.value = safeApiCall { api.getReservations(null) }
        }
    }

    // ==========================
    // Aceptar Solicitud
    // ==========================
    private val _acceptResult =
        MutableStateFlow<NetworkResult<Reservation>>(NetworkResult.Idle)
    val acceptResult: StateFlow<NetworkResult<Reservation>> = _acceptResult

    fun acceptServiceRequest(requestId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _acceptResult.value = NetworkResult.Loading
            _acceptResult.value = safeApiCall { api.acceptServiceRequest(requestId) }
        }
    }

    // ==========================
    // Actualizar estado de reserva
    // ==========================
    private val _updateResult =
        MutableStateFlow<NetworkResult<Reservation>>(NetworkResult.Idle)
    val updateResult: StateFlow<NetworkResult<Reservation>> = _updateResult

    fun updateReservationStatus(reservationId: String, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _updateResult.value = NetworkResult.Loading
            _updateResult.value = safeApiCall { api.updateReservationStatus(reservationId, status) }
        }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> retrofit2.Response<T>): NetworkResult<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(body)
                } else {
                    NetworkResult.Error("Response body is null")
                }
            } else {
                NetworkResult.Error("Error: ${response.code()} ${response.message()}")
            }
        } catch (e: HttpException) {
            NetworkResult.Error("HTTP Error: ${e.message()}")
        } catch (e: IOException) {
            NetworkResult.Error("Network Error: ${e.message}")
        } catch (e: Exception) {
            NetworkResult.Error("Unknown Error: ${e.message}")
        }
    }
}
