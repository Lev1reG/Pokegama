package com.example.pokegama.data.model

data class Facility(
    val id: String,
    val type: String,
    val name: String,
    val faculty: String,
    val latitude: Double,
    val longitude: Double,
    val description: String,
    var isAccepted: Boolean
)