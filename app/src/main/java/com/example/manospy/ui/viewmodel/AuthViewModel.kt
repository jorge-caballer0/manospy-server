package com.example.manospy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manospy.data.api.RetrofitClient
import com.example.manospy.data.local.SessionManager
import com.example.manospy.data.model.AuthResponse
import com.example.manospy.data.model.LoginRequest
import com.example.manospy.data.model.RegisterClientRequest
import com.example.manospy.data.model.User
import com.example.manospy.data.model.ProfessionalRegisterPayload
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.IOException

class AuthViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val api = RetrofitClient.apiService

    private val _authState = MutableStateFlow<NetworkResult<AuthResponse>>(NetworkResult.Idle)
    val authState: StateFlow<NetworkResult<AuthResponse>> = _authState

    // ---------- AUTH ----------
    fun login(email: String, password: String, role: String = "client") {
        performAuthCall {
            safeApiCall { api.login(LoginRequest(email, password, role)) }
        }
    }

    fun registerClient(name: String, email: String, password: String, phone: String) {
        performAuthCall {
            safeApiCall { api.registerClient(RegisterClientRequest(name, email, password, phone)) }
        }
    }

    fun registerProfessional(payload: ProfessionalRegisterPayload) {
        performAuthCall {
            safeApiCall { api.registerProfessional(payload) }
        }
    }

    fun logout() {
        sessionManager.clearSession()
        _authState.value = NetworkResult.Idle
    }

    fun getCurrentUser(): User? = sessionManager.getUser()

    // ---------- HELPER ----------
    private fun performAuthCall(apiCall: suspend () -> NetworkResult<AuthResponse>) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _authState.value = NetworkResult.Loading
                val result = apiCall()

                if (result is NetworkResult.Success) {
                    // ✅ Validamos que authResponse no sea null
                    val authResponse = result.data
                    if (authResponse != null && authResponse.user != null) {
                        sessionManager.saveUser(authResponse.user, authResponse.token)
                        _authState.value = result
                    } else {
                        _authState.value = NetworkResult.Error("Error: Respuesta inválida del servidor")
                    }
                } else if (result is NetworkResult.Error) {
                    _authState.value = result
                } else {
                    _authState.value = result
                }
            } catch (e: Exception) {
                _authState.value = NetworkResult.Error("Error: ${e.localizedMessage}")
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
