package com.example.manospy.ui.viewmodel

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.manospy.data.api.PhotoUploadRequest
import com.example.manospy.data.api.RetrofitClient
import com.example.manospy.data.local.SessionManager
import com.example.manospy.data.model.AuthResponse
import com.example.manospy.data.model.ProfessionalRegisterPayload
import com.example.manospy.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.*

class ProfessionalRegisterViewModel(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val api = RetrofitClient.apiService

    // Paso actual (1, 2, 3)
    private val _currentStep = MutableStateFlow(1)
    val currentStep: StateFlow<Int> = _currentStep

    // Estado de registro (Loading, Success, Error, Idle)
    private val _registerState = MutableStateFlow<NetworkResult<AuthResponse>>(NetworkResult.Idle)
    val registerState: StateFlow<NetworkResult<AuthResponse>> = _registerState

    // Estado de upload de foto
    private val _photoUploadState = MutableStateFlow<NetworkResult<String>>(NetworkResult.Idle)
    val photoUploadState: StateFlow<NetworkResult<String>> = _photoUploadState

    // Payload que se completa a lo largo de los pasos
    var payload: ProfessionalRegisterPayload = ProfessionalRegisterPayload()
        private set

    // ---------- Step 1 ----------
    fun validateStep1(
        name: String,
        lastName: String,
        email: String,
        phone: String,
        password: String
    ): Boolean {
        return name.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() &&
                phone.isNotBlank() &&
                password.isNotBlank()
    }

    fun updateStep1Data(
        name: String,
        email: String,
        phone: String,
        photoUri: String?
    ) {
        payload = payload.copy(
            name = name,
            email = email,
            phone = phone,
            profilePhotoUri = photoUri
        )
        _currentStep.value = 2
    }

    fun nextStepStep1(
        name: String,
        lastName: String,
        email: String,
        phone: String,
        password: String
    ): Boolean {
        return if (validateStep1(name, lastName, email, phone, password)) {
            payload = payload.copy(
                name = name,
                lastName = lastName,
                email = email,
                phone = phone,
                password = password
            )
            _currentStep.value = 2
            true
        } else {
            false
        }
    }

    // ---------- Step 2 ----------
    fun validateStep2(services: List<String>, cities: List<String>): Boolean {
        return services.isNotEmpty() && cities.isNotEmpty()
    }

    fun updateStep2Data(services: List<String>) {
        payload = payload.copy(services = services)
        _currentStep.value = 3
    }

    fun nextStepStep2(services: List<String>, cities: List<String>): Boolean {
        return if (validateStep2(services, cities)) {
            payload = payload.copy(services = services, cities = cities)
            _currentStep.value = 3
            true
        } else {
            false
        }
    }

    // ---------- Step 3 ----------
    fun setIdFront(url: String) {
        payload = payload.copy(idFrontUrl = url)
    }

    fun setIdBack(url: String) {
        payload = payload.copy(idBackUrl = url)
    }

    fun setCertificates(urls: List<String>) {
        payload = payload.copy(certificates = urls.filterNotNull())
    }

    fun validateStep3(
        idFrontUrl: String?,
        idBackUrl: String?,
        certificates: List<String>?
    ): Boolean {
        return !idFrontUrl.isNullOrBlank() &&
                !idBackUrl.isNullOrBlank() &&
                !certificates.isNullOrEmpty()
    }

    fun finishRegistration(skipDocs: Boolean = false) {
        if (skipDocs) {
            // Permitir finalizar registro sin documentos (modo pruebas / omitir)
            registerProfessional(payload)
            return
        }

        if (validateStep3(payload.idFrontUrl, payload.idBackUrl, payload.certificates)) {
            registerProfessional(payload)
        }
    }

    // ---------- Navegación entre pasos ----------
    fun previousStep() {
        if (_currentStep.value > 1) _currentStep.value = _currentStep.value - 1
    }

    // ---------- Llamada final ----------
    fun uploadProfilePhoto(bitmap: Bitmap?) {
        if (bitmap == null) {
            _photoUploadState.value = NetworkResult.Error("No bitmap provided")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                _photoUploadState.value = NetworkResult.Loading
                
                // Convertir bitmap a base64
                val base64 = bitmapToBase64(bitmap)
                val fileName = "profile_${System.currentTimeMillis()}.jpg"
                
                // Llamar API para upload
                val request = PhotoUploadRequest(
                    base64Data = base64,
                    fileName = fileName
                )
                
                val response = api.uploadPhotoBase64(request)
                
                if (response.isSuccessful && response.body() != null) {
                    val photoUrl = response.body()!!.photoUrl
                    payload = payload.copy(profilePhotoUri = photoUrl)
                    _photoUploadState.value = NetworkResult.Success(photoUrl)
                } else {
                    _photoUploadState.value = NetworkResult.Error("Error: ${response.code()} ${response.message()}")
                }
            } catch (e: HttpException) {
                _photoUploadState.value = NetworkResult.Error("HTTP Error: ${e.message()}")
            } catch (e: IOException) {
                _photoUploadState.value = NetworkResult.Error("Network Error: ${e.message}")
            } catch (e: Exception) {
                _photoUploadState.value = NetworkResult.Error("Unknown Error: ${e.message}")
            }
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos)
        val imageBytes = baos.toByteArray()
        return android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT)
    }

    private fun registerProfessional(payload: ProfessionalRegisterPayload) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _registerState.value = NetworkResult.Loading
                val result = safeApiCall { api.registerProfessional(payload) }

                when (result) {
                    is NetworkResult.Success -> {
                        val auth = result.data
                        // Validar que auth y user no sean null antes de guardar
                        if (auth != null && auth.user != null && auth.token != null) {
                            sessionManager.saveUser(auth.user, auth.token)
                            _registerState.value = result
                        } else {
                            _registerState.value = NetworkResult.Error("Error: Respuesta del servidor incompleta")
                        }
                    }
                    is NetworkResult.Error -> {
                        _registerState.value = result
                    }
                    is NetworkResult.Loading, is NetworkResult.Idle -> {
                        _registerState.value = result
                    }
                }
            } catch (e: Exception) {
                // Manejo de excepciones inesperadas
                _registerState.value = NetworkResult.Error("Error inesperado: ${e.message}")
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
