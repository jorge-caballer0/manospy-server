package com.example.manospy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manospy.data.model.Reservation
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * GUÍA: Cómo agregar estos métodos a ServiceViewModel
 * 
 * Abre ServiceViewModel.kt y agrega estos métodos públicos al final de la clase:
 * 
 * ```kotlin
 * fun cancelCurrentReservation() {
 *     viewModelScope.launch {
 *         _reservationDetail.value = null
 *         _lastCreatedRequest.value = null
 *         _createRequestStatus.value = NetworkResult.Idle
 *     }
 * }
 * 
 * fun hasActiveReservation(): Boolean {
 *     return _reservationDetail.value != null
 * }
 * 
 * fun clearAllReservationData() {
 *     viewModelScope.launch {
 *         _reservationDetail.value = null
 *         _lastCreatedRequest.value = null
 *         _createRequestStatus.value = NetworkResult.Idle
 *         _serviceRequests.value = NetworkResult.Loading
 *         _reservations.value = NetworkResult.Loading
 *     }
 * }
 * ```
 */

// ========== MÉTODOS QUE DEBES COPIAR A ServiceViewModel =========

// Cancela la solicitud de reserva actual y limpia el estado
fun _exampleCancelCurrentReservation() {
    // viewModelScope.launch {
    //     _reservationDetail.value = null
    //     _lastCreatedRequest.value = null
    //     _createRequestStatus.value = NetworkResult.Idle
    // }
}

// Obtiene si hay una solicitud/reserva activa
fun _exampleHasActiveReservation(): Boolean {
    // return _reservationDetail.value != null
    return false
}

// Limpia todos los datos cuando el usuario completa el servicio o hace logout
fun _exampleClearAllReservationData() {
    // viewModelScope.launch {
    //     _reservationDetail.value = null
    //     _lastCreatedRequest.value = null
    //     _createRequestStatus.value = NetworkResult.Idle
    //     _serviceRequests.value = NetworkResult.Loading
    //     _reservations.value = NetworkResult.Loading
    // }
}
