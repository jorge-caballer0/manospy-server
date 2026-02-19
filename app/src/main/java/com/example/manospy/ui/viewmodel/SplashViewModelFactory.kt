package com.example.manospy.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.manospy.data.api.ApiService
import com.example.manospy.data.local.SessionManager

class SplashViewModelFactory(
    private val context: Context,
    private val apiService: ApiService,
    private val mainViewModel: MainViewModel
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val session = SessionManager(context)
        @Suppress("UNCHECKED_CAST")
        return SplashViewModel(apiService, session, mainViewModel) as T
    }
}
