package com.example.manospy.util

sealed class NetworkResult<out T> {
    // Estado inicial: nada se está procesando todavía
    object Idle : NetworkResult<Nothing>()

    // Estado de carga
    object Loading : NetworkResult<Nothing>()

    // Éxito con datos
    data class Success<out T>(val data: T) : NetworkResult<T>()

    // Error con mensaje
    data class Error(val message: String) : NetworkResult<Nothing>()
}
