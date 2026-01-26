package com.example.manospy.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.manospy.data.model.User
import com.google.gson.Gson

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("manospy_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveUser(user: User, token: String) {
        prefs.edit()
            .putString("user", gson.toJson(user))
            .putString("token", token)
            .putString("USER_ROLE", user.role)           // ✅ CORRECCIÓN 2: Guardar rol
            .putString("USER_STATUS", user.status)       // ✅ CORRECCIÓN 2: Guardar status
            .apply()
    }

    fun getUser(): User? {
        val userJson = prefs.getString("user", null)
        return userJson?.let { gson.fromJson(it, User::class.java) }
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    // ✅ CORRECCIÓN 2: Obtener rol del usuario
    fun getUserRole(): String? {
        return prefs.getString("USER_ROLE", null)
    }

    // ✅ CORRECCIÓN 2: Obtener status del usuario
    fun getUserStatus(): String? {
        return prefs.getString("USER_STATUS", null)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
