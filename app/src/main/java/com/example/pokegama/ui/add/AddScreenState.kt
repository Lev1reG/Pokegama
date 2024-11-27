package com.example.pokegama.ui.add

import android.net.Uri

data class AddScreenState(
    val isLoading: Boolean = false,
    val type: String = "",
    val name: String = "",
    val facilityImg: String = "",
    val facilityImgName: String = "",
    val faculty: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val description: String = "",
    val isAccepted: Boolean = false,
    val fileUri: Uri? = null
)
