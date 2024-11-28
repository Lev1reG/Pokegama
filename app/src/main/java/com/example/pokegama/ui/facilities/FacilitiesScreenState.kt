package com.example.pokegama.ui.facilities

import com.example.pokegama.data.model.local.Facility

data class FacilitiesScreenState(
    val lastUpdate: Long = System.currentTimeMillis() - 5000,
    val isLoading: Boolean = false,
    val facilityItems: List<Facility> = emptyList(),
    val userLocation: Pair<Double, Double>? = Pair(-7.77083, 110.37762),
    val facilityType: String? = null
)
