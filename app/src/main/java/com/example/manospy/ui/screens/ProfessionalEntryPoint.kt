package com.example.manospy.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.manospy.ui.viewmodel.ProfessionalValidationViewModel
import com.example.manospy.ui.viewmodel.ValidationUiState
import com.example.manospy.ui.navigation.Screen

@Composable
fun ProfessionalEntryPoint(
    navController: NavController,
    userId: String,
    viewModel: ProfessionalValidationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 🔍 Al entrar, consultamos el estado del profesional
    LaunchedEffect(Unit) {
        viewModel.fetchStatus(userId)
    }

    when (uiState) {
        is ValidationUiState.Idle -> {
            LoadingScreen()
        }
        is ValidationUiState.Loading -> {
            LoadingScreen()
        }
        is ValidationUiState.Success -> {
            val status = (uiState as ValidationUiState.Success).status.status
            when (status) {
                "pending" -> ProfessionalValidationPendingScreen(navController)
                "approved", "active" -> ProfessionalHomeScreen()
                "rejected" -> ProfessionalValidationRejectedScreen(navController)
                else -> ErrorScreen(message = "Estado desconocido: $status")
            }
        }
        is ValidationUiState.Error -> {
            ErrorScreen(
                message = (uiState as ValidationUiState.Error).message,
                onRetry = { viewModel.fetchStatus(userId) },
                onContactSupport = { navController.navigate(Screen.Support.route) }
            )
        }
    }
}
