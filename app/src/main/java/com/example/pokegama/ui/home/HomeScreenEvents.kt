package com.example.pokegama.ui.home

sealed class HomeScreenEvents {
    data class ShowToast(val message: String) : HomeScreenEvents()

    object ShowNoInternetDialog : HomeScreenEvents()

}