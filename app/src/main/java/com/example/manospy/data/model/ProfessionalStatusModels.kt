package com.example.manospy.data.model

data class ProfessionalStatusResponse(
    val id: String,
    val status: String, // "pending", "approved", "active", "rejected"
    val message: String
)

data class UpdateStatusRequest(
    val status: String
)
