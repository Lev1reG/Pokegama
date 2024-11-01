package com.example.pokegama.ui.facilities

import com.example.pokegama.data.model.local.Facility

data class FacilitiesScreenState(
    val facilityItems: List<Facility> = emptyList(),
    val facilityType: String? = null
)
