package com.example.pokegama.ui.add

data class AddScreenState(
    val type: String = "",
    val name: String = "",
    val facilityImg: String = "",
    val faculty: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String = "",
    val isAccepted: Boolean = false,
)
