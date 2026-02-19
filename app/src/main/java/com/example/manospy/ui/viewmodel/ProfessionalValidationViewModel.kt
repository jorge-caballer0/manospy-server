package com.example.manospy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manospy.data.api.RetrofitClient
import com.example.manospy.data.model.ProfessionalStatusResponse
import com.example.manospy.data.model.UpdateStatusRequest
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

// Estados posibles del flujo de validación
sealed class ValidationUiState {
    object Idle : ValidationUiState()
    object Loading : ValidationUiState()
    data class Success(val status: ProfessionalStatusResponse) : ValidationUiState()
    data class Error(val message: String) : ValidationUiState()
}

class ProfessionalValidationViewModel : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _uiState = MutableStateFlow<ValidationUiState>(ValidationUiState.Idle)
    val uiState: StateFlow<ValidationUiState> = _uiState

    fun fetchStatus(professionalId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ValidationUiState.Loading
            val result = safeApiCall { api.getProfessionalStatus(professionalId) }
            _uiState.value = when (result) {
                is NetworkResult.Success -> ValidationUiState.Success(result.data)
                is NetworkResult.Error -> ValidationUiState.Error(result.message ?: "Error desconocido")
                else -> ValidationUiState.Error("Error inesperado")
            }
        }
    }

    fun updateStatus(professionalId: String, newStatus: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = ValidationUiState.Loading
            val result = safeApiCall { api.updateProfessionalStatus(professionalId, UpdateStatusRequest(newStatus)) }
            _uiState.value = when (result) {
                is NetworkResult.Success -> ValidationUiState.Success(result.data)
                is NetworkResult.Error -> ValidationUiState.Error(result.message ?: "Error desconocido")
                else -> ValidationUiState.Error("Error inesperado")
            }
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
