package com.example.manospy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manospy.data.api.ApiService
import com.example.manospy.data.local.SessionManager
import com.example.manospy.data.model.User
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed class SplashDestination {
    object Loading : SplashDestination()
    object GoLogin : SplashDestination()
    data class GoMain(val user: User) : SplashDestination()
}

data class SplashUiState(
    val destination: SplashDestination = SplashDestination.Loading,
    val error: String? = null
)

class SplashViewModel(
    private val api: ApiService,
    private val session: SessionManager,
    private val mainViewModel: MainViewModel
) : ViewModel() {

    private val _state = MutableStateFlow(SplashUiState())
    val state: StateFlow<SplashUiState> = _state

    fun start() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val token = session.getToken()
                if (token.isNullOrBlank()) {
                    _state.value = SplashUiState(SplashDestination.GoLogin)
                    return@launch
                }

                when (val res = safeApiCall { api.me() }) {
                    is NetworkResult.Success -> {
                        val user = res.data
                        session.saveUser(user, token)
                        mainViewModel.setUser(user)
                        _state.value = SplashUiState(SplashDestination.GoMain(user))
                    }
                    is NetworkResult.Error -> {
                        session.clearSession()
                        _state.value = SplashUiState(
                            destination = SplashDestination.GoLogin,
                            error = res.message
                        )
                    }
                    is NetworkResult.Loading -> {
                        // Estado de carga: mantenemos la pantalla de splash
                        _state.value = SplashUiState(SplashDestination.Loading)
                    }
                    is NetworkResult.Idle -> {
                        // Estado inicial: no hacemos nada especial
                        _state.value = SplashUiState(SplashDestination.Loading)
                    }
                }
            } catch (e: Exception) {
                // Capturar cualquier excepción y redirigir a login
                session.clearSession()
                _state.value = SplashUiState(
                    destination = SplashDestination.GoLogin,
                    error = e.message ?: "Error desconocido"
                )
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
