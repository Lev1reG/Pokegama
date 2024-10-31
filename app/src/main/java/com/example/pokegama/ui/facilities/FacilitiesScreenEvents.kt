package com.example.pokegama.ui.facilities

sealed class FacilitiesScreenEvents {
    data class ShowToast(val message: String) : FacilitiesScreenEvents()

    object ShowNoInternetDialog : FacilitiesScreenEvents()

}