package com.example.manospy.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.manospy.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainViewModel : ViewModel() {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    // ✅ CORRECCIÓN 3: Exponer userRole para navegación
    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    // ✅ CORRECCIÓN 3: Exponer userStatus
    private val _userStatus = MutableStateFlow<String?>(null)
    val userStatus: StateFlow<String?> = _userStatus

    fun setUser(user: User?) {
        _currentUser.value = user
        _userRole.value = user?.role       // ✅ CORRECCIÓN 3: Actualizar rol
        _userStatus.value = user?.status   // ✅ CORRECCIÓN 3: Actualizar status
    }

    fun clearUser() {
        _currentUser.value = null
        _userRole.value = null
        _userStatus.value = null
    }

    val isClient: Boolean
        get() = _currentUser.value?.role == "client"

    val isProfessional: Boolean
        get() = _currentUser.value?.role == "professional"
}
