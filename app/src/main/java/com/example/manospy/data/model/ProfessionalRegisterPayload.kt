package com.example.manospy.data.model


data class ProfessionalRegisterPayload(
    val name: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val profilePhotoUri: String? = null,
    val services: List<String> = emptyList(),
    val cities: List<String> = emptyList(),
    val idFrontUrl: String? = null,
    val idBackUrl: String? = null,
    val certificates: List<String> = emptyList()
)
