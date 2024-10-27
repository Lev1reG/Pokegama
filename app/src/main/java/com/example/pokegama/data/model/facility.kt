package com.example.pokegama.data.model

data class Facility(
    val type: String,
    val name: String,
    val imageUri: String,
    val faculty: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String? = null,
    var isAccepted: Boolean = false
)