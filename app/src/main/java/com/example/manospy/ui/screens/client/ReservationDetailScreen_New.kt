package com.example.manospy.ui.screens.client

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.manospy.ui.viewmodel.ServiceViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationDetailScreenNew(
    navController: NavController,
    reservationId: String,
    svcViewModel: ServiceViewModel,
    isPending: Boolean = false
) {
    ReservationDetailScreen(navController = navController, reservationId = reservationId, viewModel = svcViewModel)
}
