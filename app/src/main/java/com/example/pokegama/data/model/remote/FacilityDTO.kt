package com.example.pokegama.data.model.remote

data class FacilityDTO(
    val type: String,
    val name: String,
    val facilityImg: String,
    val faculty: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String? = null,
    var isAccepted: Boolean = false,
)