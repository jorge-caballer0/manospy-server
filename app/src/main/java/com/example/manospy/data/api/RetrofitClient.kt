package com.example.manospy.data.api

import android.content.Context
import com.example.manospy.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // URL pública de Render
    private const val BASE_URL = "https://manospy-server.onrender.com/"
    
    private var sessionManager: SessionManager? = null

    fun initialize(context: Context) {
        if (sessionManager == null) {
            sessionManager = SessionManager(context)
        }
    }

    // Interceptor para logging (solo en debug)
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Interceptor para agregar token de autenticación
    private val authInterceptor = Interceptor { chain ->
        var request = chain.request()
        
        // Agregar token si existe
        sessionManager?.getToken()?.let { token ->
            request = request.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        }
        
        chain.proceed(request)
    }

    // Interceptor para manejar errores globales
    private val errorInterceptor = Interceptor { chain ->
        val request = chain.request()
        val response = chain.proceed(request)
        // No lanzar excepción aquí - dejar que Retrofit maneje los errores
        // y la aplicación los procese gracefully
        response
    }

    // Cliente HTTP con timeouts e interceptores
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // tiempo de conexión
        .readTimeout(30, TimeUnit.SECONDS)    // tiempo de lectura
        .writeTimeout(30, TimeUnit.SECONDS)   // tiempo de escritura
        .addInterceptor(authInterceptor)      // agregar token
        .addInterceptor(logging)              // logs en Logcat
        .addInterceptor(errorInterceptor)     // manejo de errores
        .build()

    // Retrofit configurado con Gson
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(ApiService::class.java)
    }
}
