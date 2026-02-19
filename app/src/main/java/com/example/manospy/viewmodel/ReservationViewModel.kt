package com.example.manospy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manospy.data.api.RetrofitClient
import com.example.manospy.data.model.Reservation
import com.example.manospy.data.model.ReservationStatus
import com.example.manospy.data.model.ServiceRequestStatus
import com.example.manospy.data.model.ServiceRequest
import com.example.manospy.data.repository.AppRepository
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class ReservationViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    // Estados para reservas y solicitudes
    private val _reservations = MutableStateFlow<NetworkResult<List<Reservation>>>(NetworkResult.Loading)
    val reservations: StateFlow<NetworkResult<List<Reservation>>> = _reservations

    private val _serviceRequests = MutableStateFlow<NetworkResult<List<ServiceRequest>>>(NetworkResult.Loading)
    val serviceRequests: StateFlow<NetworkResult<List<ServiceRequest>>> = _serviceRequests

    init {
        fetchReservations()
        fetchServiceRequests()
    }

    fun fetchReservations(userId: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _reservations.value = NetworkResult.Loading
            _reservations.value = safeApiCall { api.getReservations() }
        }
    }

    fun fetchServiceRequests(userId: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            _serviceRequests.value = NetworkResult.Loading
            _serviceRequests.value = safeApiCall { api.getServiceRequests() }
        }
    }

    // Funciones de filtrado
    fun getAllReservations(): List<Reservation> {
        return (_reservations.value as? NetworkResult.Success)?.data ?: emptyList()
    }

    fun getCompletedReservations(): List<Reservation> {
        return (_reservations.value as? NetworkResult.Success)?.data?.filter {
            it.status == ReservationStatus.COMPLETED
        } ?: emptyList()
    }

    fun getInProcessReservations(): List<Reservation> {
        return (_reservations.value as? NetworkResult.Success)?.data?.filter {
            it.status == ReservationStatus.ACCEPTED
        } ?: emptyList()
    }

    fun getPendingRequests(): List<ServiceRequest> {
        return (_serviceRequests.value as? NetworkResult.Success)?.data?.filter {
            it.status == ServiceRequestStatus.PENDING
        } ?: emptyList()
    }

    // Función para refrescar datos
    fun refreshData() {
        fetchReservations()
        fetchServiceRequests()
    }

    // Safe API call wrapper
    private suspend fun <T> safeApiCall(call: suspend () -> Response<T>): NetworkResult<T> =
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
                NetworkResult.Error("HTTP error: ${e.message()}")
            } catch (e: IOException) {
                NetworkResult.Error("Error de conexión: ${e.localizedMessage}")
            } catch (e: Exception) {
                NetworkResult.Error(e.localizedMessage ?: "Error desconocido")
            }
        }
}